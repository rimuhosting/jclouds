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
package org.jclouds.vcloud.util;

import java.net.URI;
import java.util.Map;

import org.jclouds.vcloud.domain.Link;
import org.jclouds.vcloud.domain.internal.LinkImpl;
import org.xml.sax.Attributes;

/**
 * 
 * @author Adrian Cole
 */
public class Utils {

   public static void putLink(Map<String, Link> map, Attributes attributes) {
      map.put(attributes.getValue(attributes.getIndex("name")), newLink(attributes));
   }

   public static Link newLink(Attributes attributes) {
      return new LinkImpl(attributes.getValue(attributes.getIndex("name")), attributes
               .getValue(attributes.getIndex("type")), URI.create(attributes.getValue(attributes
               .getIndex("href"))));
   }
}
