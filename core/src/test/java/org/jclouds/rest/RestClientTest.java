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
package org.jclouds.rest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.apache.commons.io.IOUtils;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Nullable;

public abstract class RestClientTest<T> {

   protected RestAnnotationProcessor<T> processor;
   protected Injector injector;

   protected abstract Module createModule();

   protected abstract void checkFilters(GeneratedHttpRequest<T> httpMethod);
   protected abstract TypeLiteral<RestAnnotationProcessor<T>> createTypeLiteral();

   @BeforeClass
   protected void setupFactory() {

      injector = Guice.createInjector(createModule(), new RestModule(), new ExecutorServiceModule(
               new WithinThreadExecutorService()), new JavaUrlHttpCommandExecutorServiceModule());

      processor = injector.getInstance(Key
               .get(createTypeLiteral()));
   }

   protected void assertEntityEquals(GeneratedHttpRequest<T> httpMethod, String toMatch)
            throws IOException {
      if (httpMethod.getEntity() == null) {
         assertNull(toMatch);
      } else {
         String entity = (httpMethod.getEntity() instanceof String) ? httpMethod.getEntity()
                  .toString() : IOUtils.toString((InputStream) httpMethod.getEntity());
         assertEquals(entity, toMatch);
      }
   }

   protected void assertHeadersEqual(GeneratedHttpRequest<T> httpMethod, String toMatch) {
      assertEquals(HttpUtils.sortAndConcatHeadersIntoString(httpMethod.getHeaders()), toMatch);
   }

   protected void assertRequestLineEquals(GeneratedHttpRequest<T> httpMethod,
            String toMatch) {
      assertEquals(httpMethod.getRequestLine(), toMatch);
   }

   protected void assertExceptionParserClassEquals(Method method, @Nullable Class<?> parserClass) {
      if (parserClass == null)
         assertEquals(processor.createExceptionParserOrNullIfNotFound(method), null);
      else
         assertEquals(processor.createExceptionParserOrNullIfNotFound(method).getClass(),
                  parserClass);
   }

   protected void assertSaxResponseParserClassEquals(Method method, @Nullable Class<?> parserClass) {
      assertEquals(RestAnnotationProcessor.getSaxResponseParserClassOrNull(method), parserClass);
   }

   protected void assertResponseParserClassEquals(Method method,
            GeneratedHttpRequest<T> httpMethod, @Nullable Class<?> parserClass) {
      assertEquals(processor.createResponseParser(method, httpMethod).getClass(), parserClass);
   }

}