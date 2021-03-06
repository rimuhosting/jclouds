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
package org.jclouds.mezeo.pcs2.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.blobstore.config.BlobStoreObjectModule;
import org.jclouds.lifecycle.Closer;
import org.jclouds.mezeo.pcs2.PCS;
import org.jclouds.mezeo.pcs2.PCSAsyncClient;
import org.jclouds.mezeo.pcs2.PCSClient;
import org.jclouds.mezeo.pcs2.reference.PCSConstants;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the PCS connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
public class PCSContextModule extends AbstractModule {
   @Override
   protected void configure() {
      install(new BlobStoreObjectModule());
      install(new PCSObjectModule());
   }

   @Provides
   @Singleton
   RestContext<PCSAsyncClient, PCSClient> provideContext(Closer closer, PCSAsyncClient async,
            PCSClient defaultApi, @PCS URI endPoint,
            @Named(PCSConstants.PROPERTY_PCS2_USER) String account) {
      return new RestContextImpl<PCSAsyncClient, PCSClient>(closer, async, defaultApi, endPoint,
               account);
   }

}