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
package org.jclouds.rimuhosting.miro;

import org.jclouds.rest.annotations.*;
import org.jclouds.rimuhosting.miro.binder.RimuHostingJsonBinder;
import org.jclouds.rimuhosting.miro.binder.RimuHostingRebootJsonBinder;
import org.jclouds.rimuhosting.miro.data.NewInstance;
import org.jclouds.rimuhosting.miro.domain.*;
import org.jclouds.rimuhosting.miro.filters.RimuHostingAuthentication;
import org.jclouds.rimuhosting.miro.functions.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.Future;

/**
 * Provides asynchronous access to RimuHosting via their REST API.
 * <p/>
 *
 * @author Ivan Meredith
 * @see RimuHostingClient
 * @see <a href="http://apidocs.rimuhosting.com" />
 */
@RequestFilters(RimuHostingAuthentication.class)
@Endpoint(RimuHosting.class)
public interface RimuHostingAsyncClient {

   @GET @Path("/distributions")
   @ResponseParser(ParseImagesFromJsonResponse.class)
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ParseRimuHostingException.class)
   Future<SortedSet<Image>> getImageList();

   @GET @Path("/orders")
   @ResponseParser(ParseInstancesFromJsonResponse.class)
   @MatrixParams(keys = "include_inactive", values = "N")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ParseRimuHostingException.class)
   Future<SortedSet<Instance>> getInstanceList();

   @GET @Path("/pricing-plans")
   @MatrixParams(keys = "server-type", values = "VPS")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ParseRimuHostingException.class)
   @ResponseParser(ParsePricingPlansFromJsonResponse.class)
   Future<SortedSet<PricingPlan>> getPricingPlanList();

   @POST @Path("/orders/new-vps")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ParseRimuHostingException.class)
   @ResponseParser(ParseNewInstanceResponseFromJsonResponse.class)
   Future<NewInstanceResponse> createInstance(@BinderParam(RimuHostingJsonBinder.class) NewInstance newInstance);


   @GET @Path("/orders/order-{id}-blah/vps")
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseInstanceInfoFromJsonResponse.class)
   Future<InstanceInfo> getInstanceInfo(@PathParam("id") Long id);


   @GET @Path("/orders/order-{id}-blah")
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseInstanceFromJsonResponse.class)
   @ExceptionParser(ParseRimuHostingException.class)
   Future<Instance> getInstance(@PathParam("id") Long id);


   //TODO fix this.
   @PUT @Path("/orders/order-{id}-blah/vps/paramters")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseResizeResponseFromJsonResponse.class)
   @ExceptionParser(ParseRimuHostingException.class)
   Future<ResizeResult> resizeInstance(@PathParam("id") Long id);
   
   @PUT @Path("/orders/order-{id}-blah/vps/running-state")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseInstanceInfoFromJsonResponse.class)
   @MapBinder(RimuHostingRebootJsonBinder.class)
   @ExceptionParser(ParseRimuHostingException.class)
   Future<InstanceInfo> restartInstance(@PathParam("id") Long id);

   @DELETE @Path("/orders/order-{id}-blah/vps")
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseDestroyResponseFromJsonResponse.class)
   @ExceptionParser(ParseRimuHostingException.class)
   Future<List<String>> destroyInstance(@PathParam("id") Long id);
}
