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
package org.jclouds.azure.storage.queue;

import static org.jclouds.azure.storage.options.CreateOptions.Builder.withMetadata;
import static org.jclouds.azure.storage.options.ListOptions.Builder.maxResults;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;

import javax.ws.rs.HttpMethod;

import org.jclouds.azure.storage.AzureQueue;
import org.jclouds.azure.storage.options.CreateOptions;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.reference.AzureStorageConstants;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AzureQueueConnection}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudservers.AzureQueueConnectionTest")
public class AzureQueueConnectionTest {
   private static final Class<? extends ListOptions[]> listOptionsVarargsClass = new ListOptions[] {}
            .getClass();
   private static final Class<? extends CreateOptions[]> createOptionsVarargsClass = new CreateOptions[] {}
            .getClass();

   public void testListQueues() throws SecurityException, NoSuchMethodException {
      Method method = AzureQueueConnection.class.getMethod("listQueues", listOptionsVarargsClass);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] {});
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/");
      assertEquals(httpMethod.getEndpoint().getQuery(), "comp=list");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testListQueuesOptions() throws SecurityException, NoSuchMethodException {
      Method method = AzureQueueConnection.class.getMethod("listQueues", listOptionsVarargsClass);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { maxResults(1).marker(
               "marker").prefix("prefix") });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/");
      assert httpMethod.getEndpoint().getQuery().contains("comp=list");
      assert httpMethod.getEndpoint().getQuery().contains("marker=marker");
      assert httpMethod.getEndpoint().getQuery().contains("maxresults=1");
      assert httpMethod.getEndpoint().getQuery().contains("prefix=prefix");
      assertEquals(httpMethod.getMethod(), HttpMethod.GET);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testCreateQueue() throws SecurityException, NoSuchMethodException {
      Method method = AzureQueueConnection.class.getMethod("createQueue", String.class,
               createOptionsVarargsClass);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "queue" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/queue");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=queue");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 2);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(httpMethod.getHeaders().get("Content-Length"), Collections.singletonList("0"));
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testDeleteQueue() throws SecurityException, NoSuchMethodException {
      Method method = AzureQueueConnection.class.getMethod("deleteQueue", String.class);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "queue" });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/queue");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=queue");
      assertEquals(httpMethod.getMethod(), HttpMethod.DELETE);
      assertEquals(httpMethod.getHeaders().size(), 1);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   public void testCreateQueueOptions() throws SecurityException, NoSuchMethodException {
      Method method = AzureQueueConnection.class.getMethod("createQueue", String.class,
               createOptionsVarargsClass);

      HttpRequest httpMethod = processor.createRequest(method, new Object[] { "queue",
               withMetadata(ImmutableMultimap.of("foo", "bar")) });
      assertEquals(httpMethod.getEndpoint().getHost(), "localhost");
      assertEquals(httpMethod.getEndpoint().getPath(), "/queue");
      assertEquals(httpMethod.getEndpoint().getQuery(), "restype=queue");
      assertEquals(httpMethod.getMethod(), HttpMethod.PUT);
      assertEquals(httpMethod.getHeaders().size(), 3);
      assertEquals(httpMethod.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-07-17"));
      assertEquals(httpMethod.getHeaders().get("x-ms-meta-foo"), Collections.singletonList("bar"));
      assertEquals(httpMethod.getHeaders().get("Content-Length"), Collections.singletonList("0"));
      assertEquals(processor.createResponseParser(method, httpMethod, null).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
   }

   @BeforeClass
   void setupFactory() {
      Injector injector = Guice.createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(AzureQueue.class).toInstance(
                     URI.create("http://localhost:8080"));
            bindConstant().annotatedWith(
                     Jsr330.named(AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT)).to("user");
            bindConstant().annotatedWith(
                     Jsr330.named(AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY)).to(
                     HttpUtils.toBase64String("key".getBytes()));
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }
      }, new RestModule(), new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JavaUrlHttpCommandExecutorServiceModule());
      processor = injector.getInstance(Key
               .get(new TypeLiteral<RestAnnotationProcessor<AzureQueueConnection>>() {
               }));
   }

   RestAnnotationProcessor<AzureQueueConnection> processor;
}
