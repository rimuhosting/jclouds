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
package org.jclouds.rackspace.cloudfiles.blobstore;

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;

import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

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
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.CloudFilesClient;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobStoreListContainerOptionsToListContainerOptions;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobToObject;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.BlobToObjectGetOptions;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ContainerToResourceList;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ObjectToBlob;
import org.jclouds.rackspace.cloudfiles.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.rackspace.cloudfiles.blobstore.internal.BaseCloudFilesBlobStore;
import org.jclouds.rackspace.cloudfiles.domain.CFObject;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rackspace.cloudfiles.domain.ObjectInfo;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class CloudFilesAsyncBlobStore extends BaseCloudFilesBlobStore implements AsyncBlobStore {

   @Inject
   public CloudFilesAsyncBlobStore(CloudFilesAsyncClient async, CloudFilesClient sync,
            Factory blobFactory, LoggerFactory logFactory,
            ClearListStrategy clearContainerStrategy, ObjectToBlobMetadata object2BlobMd,
            ObjectToBlob object2Blob, BlobToObject blob2Object,
            BlobStoreListContainerOptionsToListContainerOptions container2ContainerListOptions,
            BlobToObjectGetOptions blob2ObjectGetOptions,
            ContainerToResourceMetadata container2ResourceMd,
            ContainerToResourceList container2ResourceList, ExecutorService service) {
      super(async, sync, blobFactory, logFactory, clearContainerStrategy, object2BlobMd,
               object2Blob, blob2Object, container2ContainerListOptions, blob2ObjectGetOptions,
               container2ResourceMd, container2ResourceList, service);
   }

   /**
    * This implementation uses the CloudFiles HEAD Object command to return the result
    */
   public Future<BlobMetadata> blobMetadata(String container, String key) {

      return wrapFuture(async.getObjectInfo(container, key),
               new Function<MutableObjectInfoWithMetadata, BlobMetadata>() {

                  @Override
                  public BlobMetadata apply(MutableObjectInfoWithMetadata from) {
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
      return async.createContainer(container);
   }

   public Future<Void> deleteContainer(final String container) {
      return service.submit(new Callable<Void>() {

         public Void call() throws Exception {
            clearContainerStrategy.execute(container, recursive());
            async.deleteContainerIfEmpty(container).get();
            return null;
         }

      });
   }

   public Future<Boolean> exists(String container) {
      return async.containerExists(container);
   }

   public Future<Blob> getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions... optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      Future<CFObject> returnVal = async.getObject(container, key, httpOptions);
      return wrapFuture(returnVal, object2Blob);
   }

   public Future<? extends ListResponse<? extends ResourceMetadata>> list() {
      return wrapFuture(
               async.listContainers(),
               new Function<SortedSet<ContainerMetadata>, org.jclouds.blobstore.domain.ListResponse<? extends ResourceMetadata>>() {
                  public org.jclouds.blobstore.domain.ListResponse<? extends ResourceMetadata> apply(
                           SortedSet<ContainerMetadata> from) {
                     return new ListResponseImpl<ResourceMetadata>(Iterables.transform(from,
                              container2ResourceMd), null, null, false);
                  }
               });
   }

   public Future<? extends ListContainerResponse<? extends ResourceMetadata>> list(
            String container, ListContainerOptions... optionsList) {
      org.jclouds.rackspace.cloudfiles.options.ListContainerOptions httpOptions = container2ContainerListOptions
               .apply(optionsList);
      Future<ListContainerResponse<ObjectInfo>> returnVal = async.listObjects(container,
               httpOptions);
      return wrapFuture(returnVal, container2ResourceList);
   }

   public Future<String> putBlob(String container, Blob blob) {
      return async.putObject(container, blob2Object.apply(blob));
   }

   public Future<Void> removeBlob(String container, String key) {
      return async.removeObject(container, key);
   }

}
