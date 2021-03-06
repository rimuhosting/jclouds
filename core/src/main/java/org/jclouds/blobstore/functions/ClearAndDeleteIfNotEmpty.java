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
package org.jclouds.blobstore.functions;

import java.lang.reflect.Constructor;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.inject.Inject;

public class ClearAndDeleteIfNotEmpty implements Function<Exception, Void>, InvocationContext {
   static final Void v;
   static {
      Constructor<Void> cv;
      try {
         cv = Void.class.getDeclaredConstructor();
         cv.setAccessible(true);
         v = cv.newInstance();
      } catch (Exception e) {
         throw new Error("Error setting up class", e);
      }
   }
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(BlobStoreConstants.PROPERTY_BLOBSTORE_TIMEOUT)
   protected long requestTimeoutMilliseconds = 30000;

   private final ClearContainerStrategy clear;
   private final AsyncBlobStore connection;

   private GeneratedHttpRequest<?> request;

   @Inject
   protected ClearAndDeleteIfNotEmpty(ClearContainerStrategy clear, AsyncBlobStore connection) {
      this.clear = clear;
      this.connection = connection;
   }

   public Void apply(Exception from) {
      if (from instanceof HttpResponseException) {
         HttpResponseException responseException = (HttpResponseException) from;
         if (responseException.getResponse().getStatusCode() == 404) {
            return v;
         } else if (responseException.getResponse().getStatusCode() == 409) {
            clear.execute(request.getArgs()[0].toString());
            try {
               connection.deleteContainer(request.getArgs()[0].toString()).get(
                        requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
               return v;
            } catch (Exception e) {
               Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
               throw new BlobRuntimeException("Error deleting container: "
                        + request.getArgs()[0].toString(), e);
            }
         }
      }
      return null;
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      this.request = request;
   }

}
