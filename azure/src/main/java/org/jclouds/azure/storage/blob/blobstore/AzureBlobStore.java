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
package org.jclouds.azure.storage.blob.blobstore;

import static org.jclouds.blobstore.options.ListContainerOptions.Builder.recursive;

import java.util.SortedSet;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import org.jclouds.azure.storage.blob.AzureBlobAsyncClient;
import org.jclouds.azure.storage.blob.AzureBlobClient;
import org.jclouds.azure.storage.blob.blobstore.functions.AzureBlobToBlob;
import org.jclouds.azure.storage.blob.blobstore.functions.BlobPropertiesToBlobMetadata;
import org.jclouds.azure.storage.blob.blobstore.functions.BlobToAzureBlob;
import org.jclouds.azure.storage.blob.blobstore.functions.ContainerToResourceMetadata;
import org.jclouds.azure.storage.blob.blobstore.functions.ListBlobsResponseToResourceList;
import org.jclouds.azure.storage.blob.blobstore.functions.ListOptionsToListBlobsOptions;
import org.jclouds.azure.storage.blob.blobstore.internal.BaseAzureBlobStore;
import org.jclouds.azure.storage.blob.domain.ListableContainerProperties;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ListContainerResponse;
import org.jclouds.blobstore.domain.ListResponse;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.jclouds.blobstore.domain.internal.ListResponseImpl;
import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class AzureBlobStore extends BaseAzureBlobStore implements BlobStore {

   @Inject
   public AzureBlobStore(AzureBlobAsyncClient async, AzureBlobClient sync, Factory blobFactory,
            LoggerFactory logFactory, ClearListStrategy clearContainerStrategy,
            BlobPropertiesToBlobMetadata object2BlobMd, AzureBlobToBlob object2Blob,
            BlobToAzureBlob blob2Object,
            ListOptionsToListBlobsOptions container2ContainerListOptions,
            BlobToHttpGetOptions blob2ObjectGetOptions,
            ContainerToResourceMetadata container2ResourceMd,
            ListBlobsResponseToResourceList container2ResourceList, ExecutorService service) {
      super(async, sync, blobFactory, logFactory, clearContainerStrategy, object2BlobMd,
               object2Blob, blob2Object, container2ContainerListOptions, blob2ObjectGetOptions,
               container2ResourceMd, container2ResourceList, service);
   }

   /**
    * This implementation uses the AzureBlob HEAD Object command to return the result
    */
   public BlobMetadata blobMetadata(String container, String key) {
      return object2BlobMd.apply(sync.getBlobProperties(container, key));
   }

   public void clearContainer(final String container) {
      clearContainerStrategy.execute(container, recursive());
   }

   public boolean createContainer(String container) {
      return sync.createContainer(container);
   }

   public void deleteContainer(final String container) {
      sync.deleteContainer(container);
   }

   public boolean exists(String container) {
      return sync.containerExists(container);
   }

   public Blob getBlob(String container, String key,
            org.jclouds.blobstore.options.GetOptions... optionsList) {
      GetOptions httpOptions = blob2ObjectGetOptions.apply(optionsList);
      return object2Blob.apply(sync.getBlob(container, key, httpOptions));
   }

   public ListResponse<? extends ResourceMetadata> list() {
      return new Function<SortedSet<ListableContainerProperties>, org.jclouds.blobstore.domain.ListResponse<? extends ResourceMetadata>>() {
         public org.jclouds.blobstore.domain.ListResponse<? extends ResourceMetadata> apply(
                  SortedSet<ListableContainerProperties> from) {
            return new ListResponseImpl<ResourceMetadata>(Iterables.transform(from,
                     container2ResourceMd), null, null, false);
         }
      }.apply(sync.listContainers());
   }

   public ListContainerResponse<? extends ResourceMetadata> list(String container,
            ListContainerOptions... optionsList) {
      ListBlobsOptions httpOptions = container2ContainerListOptions.apply(optionsList);
      return container2ResourceList.apply(sync.listBlobs(container, httpOptions));
   }

   public String putBlob(String container, Blob blob) {
      return sync.putBlob(container, blob2Object.apply(blob));
   }

   public void removeBlob(String container, String key) {
      sync.deleteBlob(container, key);
   }

}
