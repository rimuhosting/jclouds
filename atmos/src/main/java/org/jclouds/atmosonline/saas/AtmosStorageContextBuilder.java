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
package org.jclouds.atmosonline.saas;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.atmosonline.saas.config.AtmosStorageContextModule;
import org.jclouds.atmosonline.saas.config.AtmosStorageRestClientModule;
import org.jclouds.rest.RestContextBuilder;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class AtmosStorageContextBuilder extends RestContextBuilder<AtmosStorageClient> {

   public AtmosStorageContextBuilder(Properties props) {
      super(new TypeLiteral<AtmosStorageClient>() {
      }, props);
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new AtmosStorageRestClientModule());
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new AtmosStorageContextModule());
   }

   @Override
   public AtmosStorageContextBuilder withExecutorService(ExecutorService service) {
      return (AtmosStorageContextBuilder) super.withExecutorService(service);
   }

   @Override
   public AtmosStorageContextBuilder withModules(Module... modules) {
      return (AtmosStorageContextBuilder) super.withModules(modules);
   }

}
