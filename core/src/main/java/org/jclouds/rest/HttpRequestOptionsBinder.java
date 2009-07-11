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
package org.jclouds.rest;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.options.HttpRequestOptions;

import com.google.inject.Singleton;

/**
 * Adds an entity to a request.
 * 
 * @author Adrian Cole
 */
@Singleton
public class HttpRequestOptionsBinder implements EntityBinder {
   public void addEntityToRequest(Object entity, HttpRequest request) {
      HttpRequestOptions options = (HttpRequestOptions) entity;
      String stringEntity = options.buildStringEntity();
      if (stringEntity != null) {
         request.getHeaders().put(HttpHeaders.CONTENT_TYPE, "application/unknown");
         request.getHeaders().put(HttpHeaders.CONTENT_LENGTH, stringEntity.getBytes().length + "");
         request.setEntity(stringEntity);
      }
   }
}
