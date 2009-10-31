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
package org.jclouds.twitter;

import java.util.SortedSet;
import java.util.concurrent.Future;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.twitter.domain.Status;
import org.jclouds.twitter.functions.ParseStatusesFromJsonResponse;

/**
 * Provides access to Twitter via their REST API.
 * <p/>
 * 
 * @see <a href= "http://apiwiki.twitter.com/Twitter-REST-API-Method" />
 * @author Adrian Cole
 */
@Endpoint(Twitter.class)
@RequestFilters(BasicAuthentication.class)
public interface TwitterClient {

   @GET
   @ResponseParser(ParseStatusesFromJsonResponse.class)
   @Path("/statuses/mentions.json")
   Future<SortedSet<Status>> getMyMentions();

}
