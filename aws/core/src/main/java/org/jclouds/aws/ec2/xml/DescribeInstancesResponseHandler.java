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
 *   http:
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.xml;

import java.util.SortedSet;

import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.util.DateService;

import com.google.common.collect.Sets;

/**
 * Parses the following XML document:
 * <p/>
 * DescribeImagesResponse xmlns="http:
 * 
 * @author Adrian Cole
 * @see <a href="http: />
 */
public class DescribeInstancesResponseHandler extends
         BaseReservationHandler<SortedSet<Reservation>> {
   private SortedSet<Reservation> reservations = Sets.newTreeSet();

   @Inject
   DescribeInstancesResponseHandler(DateService dateService) {
      super(dateService);
   }

   @Override
   public SortedSet<Reservation> getResult() {
      return reservations;
   }

   @Override
   protected void inItem() {
      if (!inInstances && !inProductCodes && !inGroups) {
         reservations.add(super.newReservation());
      } else {
         super.inItem();
      }
   }

}
