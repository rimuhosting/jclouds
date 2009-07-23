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
package org.jclouds.rackspace.cloudservers.options;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.binders.JsonBinder;

import com.google.common.collect.ImmutableMap;
import com.google.inject.internal.Maps;

/**
 * 
 * 
 * @author Adrian Cole
 * 
 */
public class RebuildServerOptions extends JsonBinder {
   Integer imageId;

   @Override
   public void addEntityToRequest(Map<String, String> postParams, HttpRequest request) {
      Map<String, Integer> image = Maps.newHashMap();
      if (imageId != null)
         image.put("imageId", imageId);
      super.addEntityToRequest(ImmutableMap.of("rebuild", image), request);
   }

   @Override
   public void addEntityToRequest(Object toBind, HttpRequest request) {
      throw new IllegalStateException("RebuildServer is a POST operation");
   }

   /**
    * 
    * @param id
    *           of the image to rebuild the server with.
    */
   public RebuildServerOptions withImage(int id) {
      checkArgument(id > 0, "server id must be a positive number");
      this.imageId = id;
      return this;
   }

   public static class Builder {

      /**
       * @see RebuildServerOptions#withImage(int)
       */
      public static RebuildServerOptions withImage(int id) {
         RebuildServerOptions options = new RebuildServerOptions();
         return options.withImage(id);
      }
   }
}
