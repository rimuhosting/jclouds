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
package org.jclouds.rackspace.cloudfiles.options;

import static org.jclouds.rackspace.cloudfiles.options.ListContainerOptions.Builder.afterMarker;
import static org.jclouds.rackspace.cloudfiles.options.ListContainerOptions.Builder.underPath;
import static org.jclouds.rackspace.cloudfiles.options.ListContainerOptions.Builder.maxResults;
import static org.jclouds.rackspace.cloudfiles.options.ListContainerOptions.Builder.withPrefix;
import static org.testng.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

import org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants;
import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

import com.google.common.collect.Multimap;

/**
 * Tests possible uses of ListContainerOptions and ListContainerOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class ListContainerOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(ListContainerOptions.class);
      assert !String.class.isAssignableFrom(ListContainerOptions.class);
   }

   @Test
   public void testPrefix() throws UnsupportedEncodingException {
      ListContainerOptions options = new ListContainerOptions();
      options.withPrefix("test");
      assertEquals(options.buildQueryParameters().get(CloudFilesConstants.PREFIX), Collections
               .singletonList("test"));
   }

   @Test
   public void testNoOptionsQueryString() {
      HttpRequestOptions options = new ListContainerOptions();
      assertEquals(options.buildQueryParameters().size(), 0);
   }

   @Test
   public void testOneOptionQueryString() throws UnsupportedEncodingException {
      ListContainerOptions options = new ListContainerOptions();
      options.withPrefix("test");
      Multimap<String, String> map = options.buildQueryParameters();
      assertEquals(map.size(), 1);
      assertEquals(map.get("prefix"), Collections.singletonList("test"));
   }

   @Test
   public void testTwoOptionQueryString() throws UnsupportedEncodingException {
      ListContainerOptions options = new ListContainerOptions();
      options.withPrefix("test").maxResults(1);
      Multimap<String, String> map = options.buildQueryParameters();
      assertEquals(map.size(), 2);
      assertEquals(map.get("prefix"), Collections.singletonList("test"));
      assertEquals(map.get("limit"), Collections.singletonList("1"));
   }

   @Test
   public void testPrefixAndPathUrlEncodingQueryString() throws UnsupportedEncodingException {
      ListContainerOptions options = new ListContainerOptions();
      options.withPrefix("/cloudfiles/test").underPath("/");
      Multimap<String, String> map = options.buildQueryParameters();
      assertEquals(map.size(), 2);
      assertEquals(map.get("prefix"), Collections.singletonList("/cloudfiles/test"));
      assertEquals(map.get("path"), Collections.singletonList("/"));

   }

   @Test
   public void testNullPrefix() {
      ListContainerOptions options = new ListContainerOptions();
      assertEquals(options.buildQueryParameters().get(CloudFilesConstants.PREFIX), Collections.EMPTY_LIST);
   }

   @Test
   public void testPrefixStatic() throws UnsupportedEncodingException {
      ListContainerOptions options = withPrefix("test");
      assertEquals(options.buildQueryParameters().get(CloudFilesConstants.PREFIX), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testPrefixNPE() throws UnsupportedEncodingException {
      withPrefix(null);
   }

   @Test
   public void testMarker() throws UnsupportedEncodingException {
      ListContainerOptions options = new ListContainerOptions();
      options.afterMarker("test");
      assertEquals(options.buildQueryParameters().get(CloudFilesConstants.MARKER), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullMarker() {
      ListContainerOptions options = new ListContainerOptions();
      assertEquals(options.buildQueryParameters().get(CloudFilesConstants.MARKER), Collections.EMPTY_LIST);
   }

   @Test
   public void testMarkerStatic() throws UnsupportedEncodingException {
      ListContainerOptions options = afterMarker("test");
      assertEquals(options.buildQueryParameters().get(CloudFilesConstants.MARKER), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testMarkerNPE() throws UnsupportedEncodingException {
      afterMarker(null);
   }

   @Test
   public void testMaxKeys() {
      ListContainerOptions options = new ListContainerOptions();
      options.maxResults(1000);
      assertEquals(options.buildQueryParameters().get(CloudFilesConstants.LIMIT), Collections
               .singletonList("1000"));
   }

   @Test
   public void testNullMaxKeys() {
      ListContainerOptions options = new ListContainerOptions();
      assertEquals(options.buildQueryParameters().get(CloudFilesConstants.LIMIT), Collections.EMPTY_LIST);
   }

   @Test
   public void testMaxKeysStatic() {
      ListContainerOptions options = maxResults(1000);
      assertEquals(options.buildQueryParameters().get(CloudFilesConstants.LIMIT), Collections
               .singletonList("1000"));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testMaxKeysNegative() {
      maxResults(-1);
   }

   @Test
   public void testPath() throws UnsupportedEncodingException {
      ListContainerOptions options = new ListContainerOptions();
      options.underPath("test");
      assertEquals(options.buildQueryParameters().get(CloudFilesConstants.PATH), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullPath() {
      ListContainerOptions options = new ListContainerOptions();
      assertEquals(options.buildQueryParameters().get(CloudFilesConstants.PATH),
               Collections.EMPTY_LIST);
   }

   @Test
   public void testPathStatic() throws UnsupportedEncodingException {
      ListContainerOptions options = underPath("test");
      assertEquals(options.buildQueryParameters().get(CloudFilesConstants.PATH), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testPathNPE() throws UnsupportedEncodingException {
      underPath(null);
   }
}
