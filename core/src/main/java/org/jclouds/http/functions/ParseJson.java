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
package org.jclouds.http.functions;

import java.io.InputStream;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.gson.Gson;

/**
 * This object will parse the body of an HttpResponse and return the result of type <T> back to the
 * caller.
 * 
 * @author Adrian Cole
 */
@Singleton
public abstract class ParseJson<T> implements Function<HttpResponse, T> {

   @Resource
   protected Logger logger = Logger.NULL;
   protected final Gson gson;

   public ParseJson(Gson gson) {
      this.gson = gson;
   }

   /**
    * parses the http response body to create a new {@code <T>}.
    */
   public T apply(HttpResponse from) {
      InputStream gson = from.getContent();
      try {
         return apply(gson);
      } catch (Exception e) {
         StringBuilder message = new StringBuilder();
         message.append("Error parsing input");
         logger.error(e, message.toString());
         throw new HttpResponseException(message.toString() + "\n" + from, null, from, e);
      } finally {
         IOUtils.closeQuietly(gson);
      }

   }

   protected abstract T apply(InputStream stream);
}