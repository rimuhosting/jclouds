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
package org.jclouds.vcloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.RequiresHttp;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextBuilder;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloud.endpoints.VCloud;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VCloudVersions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "vcloud.VCloudVersionsLiveTest")
public class VCloudVersionsLiveTest {
  
   @RequiresHttp
   @ConfiguresRestClient
   private static final class VCloudVersionsRestClientModule extends AbstractModule {
      @SuppressWarnings("unused")
      @Provides
      @Singleton
      protected VCloudVersions provideVCloudVersions(RestClientFactory factory) {
         return factory.create(VCloudVersions.class);
      }

      @Override
      protected void configure() {
         bind(URI.class).annotatedWith(VCloud.class).toInstance(URI.create(endpoint));
      }
   }

   private final class VCloudVersionsContextModule extends AbstractModule {

      @SuppressWarnings( { "unused" })
      @Provides
      @Singleton
      RestContext<VCloudVersions, VCloudVersions> provideContext(Closer closer, VCloudVersions api,
               @VCloud URI endPoint) {
         return new RestContextImpl<VCloudVersions, VCloudVersions>(closer, api, api, endPoint, "");
      }

      @Override
      protected void configure() {

      }
   }

   static String endpoint = checkNotNull(System.getProperty("jclouds.test.endpoint"),
            "jclouds.test.endpoint");

   private RestContext<VCloudVersions, VCloudVersions> context;

   @Test
   public void testGetSupportedVersions() throws Exception {
      VCloudVersions authentication = context.getAsyncApi();
      for (int i = 0; i < 5; i++) {
         SortedMap<String, URI> response = authentication.getSupportedVersions().get(45,
                  TimeUnit.SECONDS);
         assertNotNull(response);
         assertNotNull(response.containsKey("0.8"));
      }
   }

   @BeforeClass
   void setupFactory() {
      context = new RestContextBuilder<VCloudVersions, VCloudVersions>(
               new TypeLiteral<VCloudVersions>() {
               }, new TypeLiteral<VCloudVersions>() {
               }, new Properties()) {

         public void addContextModule(List<Module> modules) {

            modules.add(new VCloudVersionsContextModule());
         }

         @Override
         protected void addClientModule(List<Module> modules) {
            modules.add(new VCloudVersionsRestClientModule());
         }

      }.withModules(new Log4JLoggingModule(),
               new ExecutorServiceModule(new WithinThreadExecutorService())).buildContext();
   }
}
