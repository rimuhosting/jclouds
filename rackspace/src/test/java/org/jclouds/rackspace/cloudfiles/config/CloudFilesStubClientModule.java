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
package org.jclouds.rackspace.cloudfiles.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Singleton;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.CloudFilesClient;
import org.jclouds.rackspace.cloudfiles.internal.StubCloudFilesAsyncClient;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

@ConfiguresRestClient
public class CloudFilesStubClientModule extends AbstractModule {
   // must be singleton for all threads and all objects or tests may fail;
   static final ConcurrentHashMap<String, ConcurrentMap<String, Blob>> map = new ConcurrentHashMap<String, ConcurrentMap<String, Blob>>();

   @Override
   protected void configure() {

      bind(new TypeLiteral<ConcurrentMap<String, ConcurrentMap<String, Blob>>>() {
      }).toInstance(map);
      bind(new TypeLiteral<CloudFilesAsyncClient>() {
      }).to(new TypeLiteral<StubCloudFilesAsyncClient>() {
      }).asEagerSingleton();
   }

   @Provides
   @Singleton
   public CloudFilesClient provideClient(CloudFilesAsyncClient client)
            throws IllegalArgumentException, SecurityException, NoSuchMethodException {
      return SyncProxy.create(CloudFilesClient.class, client);
   }
}