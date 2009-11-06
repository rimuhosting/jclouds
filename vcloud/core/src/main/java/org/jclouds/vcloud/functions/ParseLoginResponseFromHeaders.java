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
package org.jclouds.vcloud.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.vcloud.VCloudToken;
import org.jclouds.vcloud.VCloudLogin.VCloudSession;
import org.jclouds.vcloud.domain.Link;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.xml.OrgListHandler;

import com.google.common.base.Function;

/**
 * This parses {@link VCloudSession} from HTTP headers.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseLoginResponseFromHeaders implements Function<HttpResponse, VCloudSession> {
   static final Pattern pattern = Pattern.compile("vcloud-token=(.*); path=.*");

   private final ParseSax.Factory factory;
   private final Provider<OrgListHandler> orgHandlerProvider;

   @Inject
   private ParseLoginResponseFromHeaders(Factory factory,
            Provider<OrgListHandler> orgHandlerProvider) {
      this.factory = factory;
      this.orgHandlerProvider = orgHandlerProvider;
   }

   /**
    * parses the http response headers to create a new {@link VCloudSession} object.
    */
   public VCloudSession apply(HttpResponse from) {
      String cookieHeader = checkNotNull(from.getFirstHeaderOrNull(HttpHeaders.SET_COOKIE),
               HttpHeaders.SET_COOKIE);

      final Matcher matcher = pattern.matcher(cookieHeader);
      boolean matchFound = matcher.find();

      if (matchFound) {
         final Map<String, Link> org = factory.create(orgHandlerProvider.get()).parse(
                  from.getContent());

         return new VCloudSession() {
            @VCloudToken
            public String getVCloudToken() {
               return matcher.group(1);
            }

            @Org
            public Map<String, Link> getOrgs() {
               return org;
            }
         };

      } else {
         throw new HttpResponseException("vcloud token not found in response ", null, from);
      }
   }
}
