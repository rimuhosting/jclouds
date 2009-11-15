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
package org.jclouds.nirvanix.sdn;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.jclouds.nirvanix.sdn.config.RestSDNAuthenticationModule;
import org.jclouds.nirvanix.sdn.config.SDNRestClientModule;
import org.jclouds.nirvanix.sdn.config.SDNContextModule;
import org.jclouds.rest.RestContextBuilder;

import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class SDNContextBuilder extends RestContextBuilder<SDNAsyncClient, SDNClient> {

   public SDNContextBuilder(Properties props) {
      super(new TypeLiteral<SDNAsyncClient>() {
      }, new TypeLiteral<SDNClient>() {
      }, props);
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new RestSDNAuthenticationModule());
      modules.add(new SDNRestClientModule());
   }

   @Override
   public SDNContextBuilder withExecutorService(ExecutorService service) {
      return (SDNContextBuilder) super.withExecutorService(service);
   }

   @Override
   public SDNContextBuilder withModules(Module... modules) {
      return (SDNContextBuilder) super.withModules(modules);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new SDNContextModule());
   }

}
