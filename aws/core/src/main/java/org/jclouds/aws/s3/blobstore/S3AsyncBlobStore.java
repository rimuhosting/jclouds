/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.aws.s3.blobstore;

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;

import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.blobstore.functions.BlobToObject;
import org.jclouds.aws.s3.blobstore.functions.BlobToObjectGetOptions;
import org.jclouds.aws.s3.blobstore.functions.BucketToResourceList;
import org.jclouds.aws.s3.blobstore.functions.BucketToResourceMetadata;
import org.jclouds.aws.s3.blobstore.functions.ContainerToBucketListOptions;
import org.jclouds.aws.s3.blobstore.functions.ObjectToBlob;
import org.jclouds.aws.s3.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.aws.s3.blobstore.internal.BaseS3BlobStore;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.options.ListBucketOptions;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.blobstore.domain.internal.ListResponseImpl;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class S3AsyncBlobStore extends BaseS3BlobStore implements AsyncBlobStore {

   @Inject
   public S3AsyncBlobStore(S3AsyncClient async, S3Client sync, Factory blobFactory,
            LoggerFactory logFactory, ClearListStrategy clearContainerStrategy,
            ObjectToBlobMetadata object2BlobMd, ObjectToBlob object2Blob, BlobToObject blob2Object,
            ContainerToBucketListOptions container2BucketListOptions,
            BlobToObjectGetOptions blob2ObjectGetOptions,
            BucketToResourceMetadata bucket2ResourceMd, BucketToResourceList bucket2ResourceList,
            ExecutorService service) {
      super(async, sync, blobFactory, logFactory, clearContainerStrategy, object2BlobMd,
               object2Blob, blob2Object, container2BucketListOptions, blob2ObjectGetOptions,
               bucket2ResourceMd, bucket2ResourceList, service);
   }

   /**
    * This implementation uses the S3 HEAD Object command to return the result
    */
   public Future<BlobMetadata> blobMetadata(String container, String key) {
      return wrapFuture(async.headObject(container, key),
               new Function<ObjectMetadata, BlobMetadata>() {

                  @Override
                  public BlobMetadata apply(ObjectMetadata from) {
                     return object2BlobMd.apply(from);
                  }

               });
   }

   public Future<Void> clearContainer(final String container) {
      return service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            return null;
         }

      });
   }

   public Future<Boolean> createContainer(String container) {
      return async.putBucketIfNotExists(container);
   }

   public Future<Void> deleteContainer(final String container) {
      return service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            async.deleteBucketIfEmpty(container).get();
            return null;
         }

      });
   }

   public Future<Boolean> exists(String container) {
      return async.bucketExists(container);
   }

   public Future<Blob> getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions... optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      Future<S3Object> returnVal = async.getObject(container, key, httpOptions);
      return wrapFuture(returnVal, object2Blob);
   }

   public Future<? extends ListResponse<? extends ResourceMetadata>> list() {
      return wrapFuture(
               async.listOwnedBuckets(),
               new Function<SortedSet<BucketMetadata>, org.jclouds.blobstore.domain.ListResponse<? extends ResourceMetadata>>() {
                  public org.jclouds.blobstore.domain.ListResponse<? extends ResourceMetadata> apply(
                           SortedSet<BucketMetadata> from) {
                     return new ListResponseImpl<ResourceMetadata>(Iterables.transform(from,
                              bucket2ResourceMd), null, null, false);
                  }
               });
   }

   public Future<? extends ListContainerResponse<? extends ResourceMetadata>> list(
            String container, ListContainerOptions... optionsList) {
      ListBucketOptions httpOptions = container2BucketListOptions.apply(optionsList);
      Future<ListBucketResponse> returnVal = async.listBucket(container, httpOptions);
      return wrapFuture(returnVal, bucket2ResourceList);
   }

   public Future<String> putBlob(String container, Blob blob) {
      return async.putObject(container, blob2Object.apply(blob));
   }

   public Future<Void> removeBlob(String container, String key) {
      return async.deleteObject(container, key);
   }

}
