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
package org.jclouds.azure.storage.queue.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azure.storage.AzureQueue;
import org.jclouds.azure.storage.config.AzureStorageRestClientModule;
import org.jclouds.azure.storage.queue.AzureQueueAsyncClient;
import org.jclouds.azure.storage.queue.AzureQueueClient;
import org.jclouds.azure.storage.queue.reference.AzureQueueConstants;
import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;

import com.google.inject.Provides;

/**
 * Configures the Azure Queue Service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class AzureQueueRestClientModule extends AzureStorageRestClientModule {

   @Provides
   @Singleton
   @AzureQueue
   protected URI provideAuthenticationURI(
            @Named(AzureQueueConstants.PROPERTY_AZUREQUEUE_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   @Provides
   @Singleton
   protected AzureQueueAsyncClient provideAsyncClient(RestClientFactory factory) {
      return factory.create(AzureQueueAsyncClient.class);
   }

   @Provides
   @Singleton
   public AzureQueueClient provideClient(AzureQueueAsyncClient client)
            throws IllegalArgumentException, SecurityException, NoSuchMethodException {
      return SyncProxy.create(AzureQueueClient.class, client);
   }

}