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
import org.jclouds.http.functions.config.ParserModule.CDateTimeAdapter;
import org.jclouds.http.functions.config.ParserModule.DateTimeAdapter;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.rimuhosting.miro.RimuHosting;
import org.jclouds.rimuhosting.miro.RimuHostingAsyncClient;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.reference.RimuHostingConstants;

import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URI;

/**
 * Configures the RimuHosting connection, including logging and http transport.
 *
 * @author Adrian Cole
 */
public class RimuHostingContextModule extends AbstractModule {
   @Override
   protected void configure() {
      bind(DateTimeAdapter.class).to(CDateTimeAdapter.class);
   }

   @Provides
   @Singleton
   RestContext<RimuHostingAsyncClient, RimuHostingClient> provideContext(Closer closer, RimuHostingAsyncClient asyncApi,
                                                                         RimuHostingClient syncApi, @RimuHosting URI endPoint, @Named(RimuHostingConstants.PROPERTY_RIMUHOSTING_USER) String account) {
      return new RestContextImpl<RimuHostingAsyncClient, RimuHostingClient>(closer, asyncApi, syncApi, endPoint, account);
   }

}