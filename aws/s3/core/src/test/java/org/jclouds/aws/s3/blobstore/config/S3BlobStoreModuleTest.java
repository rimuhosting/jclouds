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
package org.jclouds.aws.s3.blobstore.config;

import static org.testng.Assert.assertEquals;

import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.config.S3StubClientModule;
import org.jclouds.aws.s3.reference.S3Constants;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.internal.BlobStoreContextImpl;
import org.jclouds.concurrent.WithinThreadExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.S3BlobStoreModuleTest")
public class S3BlobStoreModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new ExecutorServiceModule(new WithinThreadExecutorService()),
               new JDKLoggingModule(), new S3StubClientModule(), new S3BlobStoreContextModule() {
                  @Override
                  protected void configure() {
                     bindConstant().annotatedWith(
                              Jsr330.named(S3Constants.PROPERTY_AWS_ACCESSKEYID)).to("user");
                     bindConstant().annotatedWith(
                              Jsr330.named(S3Constants.PROPERTY_AWS_SECRETACCESSKEY)).to("key");
                     bindConstant().annotatedWith(Jsr330.named(S3Constants.PROPERTY_S3_ENDPOINT))
                              .to("http://localhost");
                     super.configure();
                  }
               });
   }

   @Test
   void testContextImpl() {
      BlobStoreContext<S3Client> context = createInjector().getInstance(
               Key.get(new TypeLiteral<BlobStoreContext<S3Client>>() {
               }));
      assertEquals(context.getClass(), BlobStoreContextImpl.class);
   }

}