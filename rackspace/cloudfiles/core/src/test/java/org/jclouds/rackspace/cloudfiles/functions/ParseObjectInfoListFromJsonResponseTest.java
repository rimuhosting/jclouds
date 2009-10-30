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
package org.jclouds.rackspace.cloudfiles.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;

import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudfiles.domain.ObjectInfo;
import org.jclouds.rackspace.cloudfiles.functions.ParseObjectInfoListFromJsonResponse.ObjectInfoImpl;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.joda.time.DateTime;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseObjectInfoListFromJsonResponseTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudfiles.ParseObjectInfoListFromJsonResponseTest")
public class ParseObjectInfoListFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_container.json");
      List<ObjectInfo> expects = Lists.newArrayList();
      ObjectInfoImpl one = i.getInstance(ObjectInfoImpl.class);
      one.name = "test_obj_1";
      one.hash = "4281c348eaf83e70ddce0e07221c3d28";
      one.bytes  = 14l;
      one.content_type = "application/octet-stream";
      one.last_modified = new DateTime("2009-02-03T05:26:32.612278");
      expects.add(one);
      ObjectInfoImpl two = i.getInstance(ObjectInfoImpl.class);
      two.name = ("test_obj_2");
      two.hash = ("b039efe731ad111bc1b0ef221c3849d0");
      two.bytes = (64l);
      two.content_type = ("application/octet-stream");
      two.last_modified =(new DateTime("2009-02-03T05:26:32.612278"));
      expects.add(two);
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      ListContainerOptions options = new ListContainerOptions();
      expect(request.getArgs()).andReturn(
               new Object[] { "containter", new ListContainerOptions[] { options } }).atLeastOnce();
      replay(request);
      ParseObjectInfoListFromJsonResponse parser = i
               .getInstance(ParseObjectInfoListFromJsonResponse.class);
      parser.setContext(request);
      assertEquals(parser.apply(is), expects);
   }
}
