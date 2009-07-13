/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.rackspace.cloudfiles;

import static org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants.PROPERTY_CLOUDFILES_KEY;
import static org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants.PROPERTY_CLOUDFILES_USER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.rackspace.cloudfiles.CloudFilesAuthentication.AuthenticationResponse;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.rest.config.JaxrsModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "cloudfiles.CloudFilesAuthenticationLiveTest")
public class CloudFilesAuthenticationLiveTest {

   protected static final String sysRackspaceUser = System.getProperty(PROPERTY_CLOUDFILES_USER);
   protected static final String sysRackspaceKey = System.getProperty(PROPERTY_CLOUDFILES_KEY);
   private Injector injector;

   @Test
   public void testAuthentication() throws Exception {
      CloudFilesAuthentication authentication = injector
               .getInstance(CloudFilesAuthentication.class);
      AuthenticationResponse response = authentication.authenticate(sysRackspaceUser,
               sysRackspaceKey);
      assertNotNull(response);
      assertNotNull(response.getStorageUrl());
      assertNotNull(response.getCDNManagementUrl());
      assertNotNull(response.getAuthToken());
   }

   @Test(expectedExceptions = HttpResponseException.class)
   public void testBadAuthentication() throws Exception {
      CloudFilesAuthentication authentication = injector
               .getInstance(CloudFilesAuthentication.class);
      try {
         authentication.authenticate("foo", "bar");
      } catch (UndeclaredThrowableException e) {
         HttpResponseException ew = (HttpResponseException) e.getCause().getCause();
         assertEquals(ew.getResponse().getStatusCode(), 401);
         throw ew;
      }
      fail();
   }

   @BeforeClass
   void setupFactory() {
      injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         protected CloudFilesAuthentication provideCloudFilesAuthentication(
                  RestClientFactory factory) {
            return factory.create(URI.create("https://api.mosso.com"),
                     CloudFilesAuthentication.class);
         }
      }, new JaxrsModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule());
   }
}
