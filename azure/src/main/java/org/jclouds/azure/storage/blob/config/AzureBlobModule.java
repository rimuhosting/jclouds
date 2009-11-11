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
package org.jclouds.azure.storage.blob.config;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.azure.storage.blob.domain.AzureBlob;
import org.jclouds.azure.storage.blob.domain.MutableBlobProperties;
import org.jclouds.azure.storage.blob.domain.internal.AzureBlobImpl;
import org.jclouds.blobstore.functions.CalculateSize;
import org.jclouds.blobstore.functions.GenerateMD5;
import org.jclouds.blobstore.functions.GenerateMD5Result;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the domain object mappings needed for all Azure Blob implementations
 * 
 * @author Adrian Cole
 */
public class AzureBlobModule extends AbstractModule {


   /**
    * explicit factories are created here as it has been shown that Assisted Inject is extremely
    * inefficient. http://code.google.com/p/google-guice/issues/detail?id=435
    */
   @Override
   protected void configure() {
      bind(AzureBlob.Factory.class).to(AzureBlobFactory.class).in(Scopes.SINGLETON);
   }

   private static class AzureBlobFactory implements AzureBlob.Factory {
      @Inject
      GenerateMD5Result generateMD5Result;
      @Inject
      GenerateMD5 generateMD5;
      @Inject
      CalculateSize calculateSize;
      @Inject
      Provider<MutableBlobProperties> metadataProvider;

      public AzureBlob create(MutableBlobProperties metadata) {
         return new AzureBlobImpl(generateMD5Result, generateMD5, calculateSize,
                  metadata != null ? metadata : metadataProvider.get());
      }
   }

   @Provides
   AzureBlob provideAzureBlob(AzureBlob.Factory factory) {
      return factory.create(null);
   }

}