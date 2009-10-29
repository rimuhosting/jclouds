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
package org.jclouds.mezeo.pcs2;

import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.mezeo.pcs2.xml.CloudXlinkHandler;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code PCSClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.PCSCloudTest")
public class PCSCloudTest {

   public void testAuthenticate() throws SecurityException, NoSuchMethodException {
      Method method = PCSCloud.class.getMethod("authenticate");
      GeneratedHttpRequest<PCSCloud> httpMethod = processor.createRequest(method);
      assertEquals(httpMethod.getRequestLine(), "GET http://localhost:8080/ HTTP/1.1");
      assertEquals(httpMethod.getHeaders().size(), 0);
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), ParseSax.class);
      assertEquals(RestAnnotationProcessor.getSaxResponseParserClassOrNull(method), CloudXlinkHandler.class);
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), BasicAuthentication.class);
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   private RestAnnotationProcessor<PCSCloud> processor;

   @BeforeClass
   void setupFactory() {

      Injector injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(PCS.class)
                     .toInstance(URI.create("http://localhost:8080"));
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         public BasicAuthentication provideBasicAuthentication()
                  throws UnsupportedEncodingException {
            return new BasicAuthentication("foo", "bar");
         }
      }, new RestModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule());

      processor = injector.getInstance(Key
               .get(new TypeLiteral<RestAnnotationProcessor<PCSCloud>>() {
               }));
   }
}
