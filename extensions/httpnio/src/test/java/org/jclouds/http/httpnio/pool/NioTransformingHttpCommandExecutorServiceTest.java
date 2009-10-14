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
package org.jclouds.http.httpnio.pool;

import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.http.BaseHttpCommandExecutorServiceTest;
import org.jclouds.http.httpnio.config.NioTransformingHttpCommandExecutorServiceModule;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * Tests for {@link HttpNioConnectionPoolFutureCommandClient}.
 * 
 * @author Adrian Cole
 */
@Test
public class NioTransformingHttpCommandExecutorServiceTest extends
         BaseHttpCommandExecutorServiceTest {

   @Override
   @Test(enabled = false)
   public void testPostAsInputStream() throws MalformedURLException, ExecutionException,
            InterruptedException, TimeoutException {
      // TODO when these fail, we hang
   }

   @Override
   @Test(enabled = false)
   public void testPostResults() {
      // see above
   }

   protected Module createClientModule() {
      return new NioTransformingHttpCommandExecutorServiceModule();
   }

   @Override
   protected void addConnectionProperties(Properties props) {

   }

}