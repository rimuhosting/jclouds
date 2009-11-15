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
package org.jclouds.atmosonline.saas.blobstore.strategy;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.FileType;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.concurrent.FutureFunctionWrapper;
import org.jclouds.logging.Logger;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Sets;

/**
 * Recursively remove a path.
 * 
 * @author Adrian Cole
 */
@Singleton
public class RecursiveRemove implements ClearListStrategy, ClearContainerStrategy {
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;
   protected final AtmosStorageAsyncClient async;
   protected final AtmosStorageClient sync;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public RecursiveRemove(AtmosStorageAsyncClient connection, AtmosStorageClient sync) {
      this.async = connection;
      this.sync = sync;
   }

   public void execute(String containerName) {
      logger.debug("clearing container ", containerName);
      execute(containerName, new ListContainerOptions().recursive());
      logger.trace("cleared container " + containerName);
   }

   private Future<Void> rm(final String fullPath, FileType type, boolean recursive)
            throws InterruptedException, ExecutionException, TimeoutException {
      Set<Future<Void>> deletes = Sets.newHashSet();
      if ((type == FileType.DIRECTORY) && recursive) {
         for (DirectoryEntry child : async.listDirectory(fullPath).get(10, TimeUnit.SECONDS)) {
            deletes.add(rm(fullPath + "/" + child.getObjectName(), child.getType(), true));
         }
      }
      for (Future<Void> isdeleted : deletes) {
         isdeleted.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
      }
      return new FutureFunctionWrapper<Void, Void>(async.deletePath(fullPath),
               new Function<Void, Void>() {

                  public Void apply(Void from) {
                     try {
                        if (!Utils.enventuallyTrue(new Supplier<Boolean>() {
                           public Boolean get() {
                              return !sync.pathExists(fullPath);
                           }
                        }, requestTimeoutMilliseconds)) {
                           throw new IllegalStateException(fullPath
                                    + " still exists after deleting!");
                        }
                        return null;
                     } catch (InterruptedException e) {
                        throw new IllegalStateException(fullPath + " still exists after deleting!",
                                 e);
                     }
                  }

               });
   }

   public void execute(final String containerName, ListContainerOptions options) {
      String path = containerName;
      if (options.getPath() != null)
         path += "/" + options.getPath();
      Set<Future<Void>> deletes = Sets.newHashSet();
      try {
         for (DirectoryEntry md : async.listDirectory(path).get(requestTimeoutMilliseconds,
                  TimeUnit.MILLISECONDS)) {
            deletes.add(rm(path + "/" + md.getObjectName(), md.getType(), options.isRecursive()));
         }
         for (Future<Void> isdeleted : deletes) {
            isdeleted.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
         }
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException("Error deleting path: " + path, e);
      }
   }

}