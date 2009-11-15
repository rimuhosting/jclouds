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

import org.jclouds.atmosonline.saas.AtmosStorageAsyncClient;
import org.jclouds.atmosonline.saas.AtmosStorageClient;
import org.jclouds.blobstore.integration.internal.BaseBlobIntegrationTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "integration", "live" }, testName = "emcsaas.AtmosStorageIntegrationTest")
public class AtmosStorageIntegrationTest extends
         BaseBlobIntegrationTest<AtmosStorageAsyncClient, AtmosStorageClient> {

   @DataProvider(name = "delete")
   // no unicode support
   @Override
   public Object[][] createData() {
      return new Object[][] { { "normal" } };
   }

   @Override
   @Test(enabled = false)
   public void testGetIfMatch() {
      // no etag support
   }

   @Override
   @Test(enabled = false)
   public void testGetIfModifiedSince() {
      // not supported
   }

   @Override
   @Test(enabled = false)
   public void testGetIfNoneMatch() {
      // no etag support
   }

   @Override
   @Test(enabled = false)
   public void testGetIfUnmodifiedSince() {
      // not supported
   }

   @Override
   @Test(enabled = false)
   public void testGetTwoRanges() {
      // not supported
   }

   @Test(enabled = false)
   // problem with the stub and md5, live is fine
   public void testMetadata() {
      // TODO
   }

   @Test(enabled = false)
   // problem with the stub and md5, live is fine
   public void testPutObject() throws Exception {
      // TODO
   }

}