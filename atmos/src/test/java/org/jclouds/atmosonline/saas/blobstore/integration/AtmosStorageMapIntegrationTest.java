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
package org.jclouds.atmosonline.saas.blobstore.integration;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.blobstore.integration.internal.BaseBlobMapIntegrationTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, testName = "emcsaas.AtmosStorageMapIntegrationTest")
public class AtmosStorageMapIntegrationTest extends
         BaseBlobMapIntegrationTest<AtmosStorageAsyncClient, AtmosStorageClient> {

   @Override
   @Test(enabled = false)
   public void testContains() throws InterruptedException, ExecutionException, TimeoutException {
      // TODO not reliable
   }

   @Override
   @Test(enabled = false)
   public void testValues() {
      // TODO not reliable KeyAlreadyExistsException@AtmosBlobStore.java:213
   }

}