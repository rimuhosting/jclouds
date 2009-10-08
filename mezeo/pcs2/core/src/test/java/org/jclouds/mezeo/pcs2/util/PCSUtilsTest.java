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
package org.jclouds.mezeo.pcs2.util;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.HttpUtils;
import org.jclouds.mezeo.pcs2.functions.Key;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code PCSUtils}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.PCSUtilsTest")
public class PCSUtilsTest {
   public void testGetEtag() {
      byte[] expected = HttpUtils.fromHexString("7F143552AAF511DEBBB00BC388ED913B");

      byte[] eTag = PCSUtils.getETag(URI
               .create("http://localhost/contents/7F143552-AAF5-11DE-BBB0-0BC388ED913B"));
      assertEquals(eTag, expected);
   }

   public void testParseKey() {
      Key key = PCSUtils.parseKey(new Key("container", "key"));
      assertEquals(key.getContainer(), "container");
      assertEquals(key.getKey(), "key");
      key = PCSUtils.parseKey(new Key("container", "container/key"));
      assertEquals(key.getContainer(), "container/container");
      assertEquals(key.getKey(), "key");
      key = PCSUtils.parseKey(new Key("container", "/container/key"));
      assertEquals(key.getContainer(), "container/container");
      assertEquals(key.getKey(), "key");

   }
}
