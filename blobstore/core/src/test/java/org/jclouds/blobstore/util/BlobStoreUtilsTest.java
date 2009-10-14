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
package org.jclouds.blobstore.util;

import static org.testng.Assert.assertEquals;

import org.jclouds.blobstore.domain.Key;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code BlobStoreUtils}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "blobstore.BlobStoreUtilsTest")
public class BlobStoreUtilsTest {

   public void testParseKey() {
      Key key = BlobStoreUtils.parseKey(new Key("container", "key"));
      assertEquals(key.getContainer(), "container");
      assertEquals(key.getKey(), "key");
      key = BlobStoreUtils.parseKey(new Key("container", "container/key"));
      assertEquals(key.getContainer(), "container/container");
      assertEquals(key.getKey(), "key");
      key = BlobStoreUtils.parseKey(new Key("container", "/container/key"));
      assertEquals(key.getContainer(), "container/container");
      assertEquals(key.getKey(), "key");

   }
}
