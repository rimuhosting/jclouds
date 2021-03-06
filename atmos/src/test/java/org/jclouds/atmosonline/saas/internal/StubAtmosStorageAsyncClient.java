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
package org.jclouds.atmosonline.saas.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobMetadataToObject;
import org.jclouds.atmosonline.saas.blobstore.functions.BlobToObject;
import org.jclouds.atmosonline.saas.blobstore.functions.ListOptionsToBlobStoreListOptions;
import org.jclouds.atmosonline.saas.blobstore.functions.ObjectToBlob;
import org.jclouds.atmosonline.saas.blobstore.functions.ResourceMetadataListToDirectoryEntryList;
import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.domain.BoundedSortedSet;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.SystemMetadata;
import org.jclouds.atmosonline.saas.domain.UserMetadata;
import org.jclouds.atmosonline.saas.options.ListOptions;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.attr.ConsistencyModels;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.HttpGetOptionsListToGetOptions;
import org.jclouds.blobstore.integration.internal.StubAsyncBlobStore;
import org.jclouds.blobstore.integration.internal.StubAsyncBlobStore.FutureBase;
import org.jclouds.concurrent.FutureFunctionWrapper;
import org.jclouds.http.options.GetOptions;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.util.Utils;

import com.google.common.base.Function;

/**
 * Implementation of {@link AtmosStorageAsyncClient} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 */
@ConsistencyModel(ConsistencyModels.STRICT)
public class StubAtmosStorageAsyncClient implements AtmosStorageAsyncClient {
   private final HttpGetOptionsListToGetOptions httpGetOptionsConverter;
   private final StubAsyncBlobStore blobStore;
   private final LoggerFactory logFactory;
   private final AtmosObject.Factory objectProvider;
   private final ObjectToBlob object2Blob;
   private final BlobToObject blob2Object;
   private final BlobMetadataToObject blob2ObjectInfo;
   private final ListOptionsToBlobStoreListOptions container2ContainerListOptions;
   private final ResourceMetadataListToDirectoryEntryList resource2ObjectList;

   @Inject
   private StubAtmosStorageAsyncClient(StubAsyncBlobStore blobStore, LoggerFactory logFactory,
            AtmosObject.Factory objectProvider,
            HttpGetOptionsListToGetOptions httpGetOptionsConverter, ObjectToBlob object2Blob,
            BlobToObject blob2Object, BlobMetadataToObject blob2ObjectInfo,
            ListOptionsToBlobStoreListOptions container2ContainerListOptions,
            ResourceMetadataListToDirectoryEntryList resource2ContainerList) {
      this.logFactory = logFactory;
      this.blobStore = blobStore;
      this.objectProvider = objectProvider;
      this.httpGetOptionsConverter = httpGetOptionsConverter;
      this.object2Blob = checkNotNull(object2Blob, "object2Blob");
      this.blob2Object = checkNotNull(blob2Object, "blob2Object");
      this.blob2ObjectInfo = checkNotNull(blob2ObjectInfo, "blob2ObjectInfo");
      this.container2ContainerListOptions = checkNotNull(container2ContainerListOptions,
               "container2ContainerListOptions");
      this.resource2ObjectList = checkNotNull(resource2ContainerList, "resource2ContainerList");
   }

   protected <F, T> Future<T> wrapFuture(Future<? extends F> future, Function<F, T> function) {
      return new FutureFunctionWrapper<F, T>(future, function, logFactory.getLogger(function
               .getClass().getName()));
   }

   public Future<URI> createDirectory(String directoryName) {
      final String container;
      if (directoryName.indexOf('/') != -1)
         container = directoryName.substring(0, directoryName.indexOf('/'));
      else
         container = directoryName;
      return wrapFuture(blobStore.createContainer(container), new Function<Boolean, URI>() {

         public URI apply(Boolean from) {
            return URI.create("http://stub/containers/" + container);
         }

      });
   }

   public Future<URI> createFile(String parent, AtmosObject object) {
      final String uri = "http://stub/containers/" + parent + "/"
               + object.getContentMetadata().getName();
      String file = object.getContentMetadata().getName();
      String container = parent;
      if (parent.indexOf('/') != -1) {
         container = parent.substring(0, parent.indexOf('/'));
         String path = parent.substring(parent.indexOf('/') + 1);
         if (!path.equals(""))
            object.getContentMetadata().setName(path + "/" + file);
      }
      Blob blob = object2Blob.apply(object);
      return wrapFuture(blobStore.putBlob(container, blob), new Function<String, URI>() {

         public URI apply(String from) {
            return URI.create(uri);
         }

      });
   }

   public Future<Void> deletePath(String path) {
      if (path.indexOf('/') == -1)
         return wrapFuture(blobStore.deleteContainerImpl(path), new Function<Boolean, Void>() {

            public Void apply(Boolean from) {
               return null;
            }

         });
      else {
         String container = path.substring(0, path.indexOf('/'));
         path = path.substring(path.indexOf('/') + 1);
         return blobStore.removeBlob(container, path);
      }
   }

   public Future<SystemMetadata> getSystemMetadata(String path) {
      throw new UnsupportedOperationException();
   }

   public Future<UserMetadata> getUserMetadata(String path) {
      if (path.indexOf('/') == -1)
         throw new UnsupportedOperationException();
      else {
         String container = path.substring(0, path.indexOf('/'));
         path = path.substring(path.indexOf('/') + 1);
         return wrapFuture(blobStore.blobMetadata(container, path),
                  new Function<BlobMetadata, UserMetadata>() {
                     public UserMetadata apply(BlobMetadata from) {
                        return blob2ObjectInfo.apply(from).getUserMetadata();
                     }
                  });
      }
   }

   public Future<AtmosObject> headFile(String path) {
      String container = path.substring(0, path.indexOf('/'));
      path = path.substring(path.indexOf('/') + 1);
      try {
         return wrapFuture(blobStore.getBlob(container, path), blob2Object);
      } catch (Exception e) {
         Utils.<KeyNotFoundException> rethrowIfRuntimeOrSameType(e);
         throw new RuntimeException(e);
      }
   }

   public Future<? extends BoundedSortedSet<? extends DirectoryEntry>> listDirectories(
            ListOptions... optionsList) {
      // org.jclouds.blobstore.options.ListOptions options = container2ContainerListOptions
      // .apply(optionsList);
      return wrapFuture(blobStore.list(), resource2ObjectList);
   }

   public Future<? extends BoundedSortedSet<? extends DirectoryEntry>> listDirectory(
            String directoryName, ListOptions... optionsList) {
      org.jclouds.blobstore.options.ListContainerOptions options = container2ContainerListOptions
               .apply(optionsList);
      String container = directoryName;
      if (directoryName.indexOf('/') != -1) {
         container = directoryName.substring(0, directoryName.indexOf('/'));
         String path = directoryName.substring(directoryName.indexOf('/') + 1);
         if (!path.equals(""))
            options.underPath(path);
      }
      return wrapFuture(blobStore.list(container, options), resource2ObjectList);
   }

   public AtmosObject newObject() {
      return this.objectProvider.create(null);
   }

   public Future<Boolean> pathExists(final String path) {
      if (path.indexOf('/') == -1 || (path.endsWith("/")))
         return blobStore.exists(path);
      else {
         return new FutureBase<Boolean>() {
            public Boolean get() throws InterruptedException, ExecutionException {
               String container = path.substring(0, path.indexOf('/'));
               String blobName = path.substring(path.indexOf('/') + 1);
               try {
                  blobStore.blobMetadata(container, blobName);
                  return true;
               } catch (KeyNotFoundException e) {
                  return false;
               }
            }
         };

      }
   }

   public Future<AtmosObject> readFile(String path, GetOptions... options) {
      String container = path.substring(0, path.indexOf('/'));
      String blobName = path.substring(path.indexOf('/') + 1);
      org.jclouds.blobstore.options.GetOptions getOptions = httpGetOptionsConverter.apply(options);
      return wrapFuture(blobStore.getBlob(container, blobName, getOptions), blob2Object);
   }

   public Future<Void> updateFile(String parent, AtmosObject object) {
      throw new UnsupportedOperationException();
   }

}
