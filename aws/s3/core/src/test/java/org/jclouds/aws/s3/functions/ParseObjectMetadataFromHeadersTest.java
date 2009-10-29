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
package org.jclouds.aws.s3.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.util.Map;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.aws.s3.blobstore.functions.BlobToObjectMetadata;
import org.jclouds.aws.s3.domain.MutableObjectMetadata;
import org.jclouds.aws.s3.domain.ObjectMetadata.StorageClass;
import org.jclouds.aws.s3.domain.internal.MutableObjectMetadataImpl;
import org.jclouds.aws.s3.reference.S3Headers;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.internal.MutableBlobMetadataImpl;
import org.jclouds.blobstore.functions.ParseSystemAndUserMetadataFromHeaders;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(testName = "s3.ParseObjectMetadataFromHeadersTest")
public class ParseObjectMetadataFromHeadersTest {

   @Test
   void testNormal() throws Exception {

      HttpResponse http = new HttpResponse();
      http.getHeaders().put(HttpHeaders.CACHE_CONTROL, "cacheControl");
      http.getHeaders().put("Content-Disposition", "contentDisposition");
      http.getHeaders().put(HttpHeaders.CONTENT_ENCODING, "encoding");
      ParseObjectMetadataFromHeaders parser = new ParseObjectMetadataFromHeaders(blobParser(http,
               "\"abc\""), blobToObjectMetadata);
      MutableObjectMetadata response = parser.apply(http);
      assertEquals(response, expects);
   }

   @Test
   void testAmzEtag() throws Exception {

      HttpResponse http = new HttpResponse();
      http.getHeaders().put(HttpHeaders.CACHE_CONTROL, "cacheControl");
      http.getHeaders().put("Content-Disposition", "contentDisposition");
      http.getHeaders().put(HttpHeaders.CONTENT_ENCODING, "encoding");
      http.getHeaders().put(S3Headers.AMZ_MD5, "\"abc\"");
      ParseObjectMetadataFromHeaders parser = new ParseObjectMetadataFromHeaders(blobParser(http,
               null), blobToObjectMetadata);
      MutableObjectMetadata response = parser.apply(http);
      assertEquals(response, expects);
   }

   DateTime now = new DateTime();
   Map<String, String> userMetadata = ImmutableMap.of("foo", "bar");
   private MutableObjectMetadataImpl expects;
   BlobToObjectMetadata blobToObjectMetadata;

   private ParseSystemAndUserMetadataFromHeaders blobParser(HttpResponse response, String etag) {
      ParseSystemAndUserMetadataFromHeaders parser = createMock(ParseSystemAndUserMetadataFromHeaders.class);
      MutableBlobMetadata md = new MutableBlobMetadataImpl();
      md.setContentType("type");
      md.setETag(etag);
      md.setName("key");
      md.setLastModified(now);
      md.setSize(1025);
      md.setUserMetadata(userMetadata);
      expect(parser.apply(response)).andReturn(md);
      replay(parser);
      return parser;
   }

   @BeforeTest
   void setUp() {
      blobToObjectMetadata = new BlobToObjectMetadata();
      expects = new MutableObjectMetadataImpl();
      expects.setCacheControl("cacheControl");
      expects.setContentDisposition("contentDisposition");
      expects.setContentEncoding("encoding");
      expects.setContentMD5(HttpUtils.fromHexString("abc"));
      expects.setContentType("type");
      expects.setETag("\"abc\"");
      expects.setKey("key");
      expects.setLastModified(now);
      expects.setOwner(null);
      expects.setSize(1025);
      expects.setStorageClass(StorageClass.STANDARD);
      expects.setUserMetadata(userMetadata);
   }
}