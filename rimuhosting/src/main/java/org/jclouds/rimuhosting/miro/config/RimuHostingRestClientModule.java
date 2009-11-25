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
package org.jclouds.rimuhosting.miro.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.rimuhosting.miro.RimuHosting;
import org.jclouds.rimuhosting.miro.RimuHostingAsyncClient;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.filters.RimuHostingAuthentication;
import org.jclouds.rimuhosting.miro.reference.RimuHostingConstants;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.UnsupportedEncodingException;
import java.net.URI;

/**
 * Configures the RimuHosting connection.
 *
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class RimuHostingRestClientModule extends AbstractModule {

   @Override
   protected void configure() {
      bindErrorHandlers();
      bindRetryHandlers();
   }

   @Provides
   @Singleton
   public RimuHostingAuthentication provideRimuHostingAuthentication(
              @Named(RimuHostingConstants.PROPERTY_RIMUHOSTING_PASSWORD) String apikey)
           throws UnsupportedEncodingException {
      return new RimuHostingAuthentication(apikey);
   }

   @Provides
   @Singleton
   protected RimuHostingAsyncClient provideClient(RestClientFactory factory) {
      return factory.create(RimuHostingAsyncClient.class);
   }

   @Provides
   @Singleton
   public RimuHostingClient provideClient(RimuHostingAsyncClient client) throws IllegalArgumentException,
           SecurityException, NoSuchMethodException {
      return SyncProxy.create(RimuHostingClient.class, client);
   }

   @Provides
   @Singleton
   @RimuHosting
   protected URI provideURI(@Named(RimuHostingConstants.PROPERTY_RIMUHOSTING_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }

   protected void bindErrorHandlers() {
      // TODO
   }

   protected void bindRetryHandlers() {
      // TODO
   }

}