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
package org.jclouds.gae.config;

import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.gae.GaeHttpCommandExecutorService;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandExecutorServiceImpl;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures {@link GaeHttpCommandExecutorService}.
 * 
 * @author Adrian Cole
 */
@ConfiguresHttpCommandExecutorService
@SingleThreaded
public class GaeHttpCommandExecutorServiceModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(HttpCommandExecutorService.class).to(GaeHttpCommandExecutorService.class);
      bind(TransformingHttpCommandExecutorService.class).to(
               TransformingHttpCommandExecutorServiceImpl.class);
   }

   @Provides
   URLFetchService provideURLFetchService() {
      return URLFetchServiceFactory.getURLFetchService();
   }

}