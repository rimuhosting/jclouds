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
package org.jclouds.blobstore;

import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.rest.RestContextBuilder;

import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * @author Adrian Cole
 */
public abstract class BlobStoreContextBuilder<A, S> extends RestContextBuilder<A, S> {
   @Override
   public BlobStoreContextBuilder<A, S> withExecutorService(ExecutorService service) {
      return (BlobStoreContextBuilder<A, S>) super.withExecutorService(service);
   }

   @Override
   public BlobStoreContextBuilder<A, S> withModules(Module... modules) {
      return (BlobStoreContextBuilder<A, S>) super.withModules(modules);
   }

   public BlobStoreContextBuilder(TypeLiteral<A> asyncClientType, TypeLiteral<S> syncClientType) {
      this(asyncClientType, syncClientType, new Properties());
   }

   public BlobStoreContextBuilder(TypeLiteral<A> asyncClientType, TypeLiteral<S> syncClientType,
            Properties properties) {
      super(asyncClientType, syncClientType, properties);

   }

   @SuppressWarnings("unchecked")
   @Override
   public BlobStoreContext<A, S> buildContext() {
      return (BlobStoreContext<A, S>) this.buildInjector().getInstance(
               Key.get(Types.newParameterizedType(BlobStoreContext.class,
                        asyncClientType.getType(), syncClientType.getType())));
   }
}