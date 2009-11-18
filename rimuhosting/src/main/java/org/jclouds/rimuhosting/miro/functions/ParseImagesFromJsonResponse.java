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
package org.jclouds.rimuhosting.miro.functions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.functions.ParseJson;
import org.jclouds.rimuhosting.miro.domain.Image;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This parses {@link org.jclouds.rimuhosting.miro.domain.Status} from a json string.
 *
 * @author Adrian Cole
 */
@Singleton
public class ParseImagesFromJsonResponse extends ParseJson< SortedSet<Image>> {
   public static class RimuHostingEnvelope {
       public RimuHostingResponse getGetDistrosResponse() {
           return get_distros_response;
       }

       public  void setGetDistrosResponse(RimuHostingResponse get_distros_response) {
           this.get_distros_response = get_distros_response;
       }

       private RimuHostingResponse get_distros_response;
   }

   public static class RimuHostingResponse {
       String status_message;

       public String getStatus_message() {
           return status_message;
       }

       public void setStatus_message(String status_message) {
           this.status_message = status_message;
       }

       public Integer getStatus_code() {
           return status_code;
       }

       public void setStatus_code(Integer status_code) {
           this.status_code = status_code;
       }

       Integer status_code;

       public SortedSet<Image> getDistro_infos() {
           return distro_infos;
       }

       public void setDistro_infos(SortedSet<Image> distro_infos) {
           this.distro_infos = distro_infos;
       }

       SortedSet<Image> distro_infos;
   }
   @Inject
   public ParseImagesFromJsonResponse(Gson gson) {
      super(gson);
   }

    public  SortedSet<Image> apply(InputStream stream) {
       Type setType = new TypeToken<RimuHostingEnvelope>() {
      }.getType();
      try {
            RimuHostingEnvelope t =  gson.fromJson(new InputStreamReader(stream, "UTF-8"), setType);
          return t.getGetDistrosResponse().getDistro_infos();
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
    }
}