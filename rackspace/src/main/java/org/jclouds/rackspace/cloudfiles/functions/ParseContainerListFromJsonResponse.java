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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.SortedSet;

import javax.inject.Inject;

import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This parses {@link ResourceMetadata} from a gson string.
 * 
 * @author Adrian Cole
 */
public class ParseContainerListFromJsonResponse extends ParseJson<SortedSet<ContainerMetadata>> {

   @Inject
   public ParseContainerListFromJsonResponse(Gson gson) {
      super(gson);
   }

   public SortedSet<ContainerMetadata> apply(InputStream stream) {
      Type listType = new TypeToken<SortedSet<ContainerMetadata>>() {
      }.getType();
      try {
         return gson.fromJson(new InputStreamReader(stream, "UTF-8"), listType);
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}