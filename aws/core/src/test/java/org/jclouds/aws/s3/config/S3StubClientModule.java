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
package org.jclouds.aws.s3.config;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.aws.s3.S3;
import org.jclouds.aws.s3.S3AsyncClient;
import org.jclouds.aws.s3.S3Client;
import org.jclouds.aws.s3.internal.StubS3AsyncClient;
import org.jclouds.blobstore.integration.config.StubBlobStoreModule;
import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * adds a stub alternative to invoking S3
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class S3StubClientModule extends AbstractModule {

   protected void configure() {
      install(new ParserModule());
      install(new StubBlobStoreModule());
      bind(S3AsyncClient.class).to(StubS3AsyncClient.class).asEagerSingleton();
      bind(URI.class).annotatedWith(S3.class).toInstance(URI.create("https://localhost/s3stub"));
   }

   @Provides
   @Singleton
   public S3Client provideClient(S3AsyncClient client) throws IllegalArgumentException,
            SecurityException, NoSuchMethodException {
      return SyncProxy.create(S3Client.class, client);
   }
}
