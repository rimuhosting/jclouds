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
package org.jclouds.aws.ec2.xml;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Resource;

import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.logging.Logger;
import org.jclouds.util.DateService;
import org.joda.time.DateTime;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;

public abstract class BaseReservationHandler<T> extends HandlerWithResult<T> {

   protected final DateService dateService;

   public BaseReservationHandler(DateService dateService) {
      this.dateService = dateService;
   }

   @Resource
   protected Logger logger = Logger.NULL;
   private StringBuilder currentText = new StringBuilder();
   private SortedSet<String> groupIds = Sets.newTreeSet();
   private SortedSet<RunningInstance> instances = Sets.newTreeSet();
   private String ownerId;
   private String requesterId;
   private String reservationId;
   private String amiLaunchIndex;
   private String dnsName;
   private String imageId;
   private String instanceId;
   private InstanceState instanceState;
   private InstanceType instanceType;
   private InetAddress ipAddress;
   private String kernelId;
   private String keyName;
   private DateTime launchTime;
   private boolean monitoring;
   private String availabilityZone;
   private String platform;
   private String privateDnsName;
   private InetAddress privateIpAddress;
   private Set<String> productCodes = Sets.newHashSet();
   private String ramdiskId;
   private String reason;
   private String subnetId;
   private String vpcId;
   protected boolean inInstances;
   protected boolean inProductCodes;
   protected boolean inGroups;

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (qName.equals("instancesSet")) {
         inInstances = true;
      } else if (qName.equals("productCodesSet")) {
         inProductCodes = true;
      } else if (qName.equals("groupSet")) {
         inGroups = true;
      }
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("groupId")) {
         groupIds.add(currentOrNull());
      } else if (qName.equals("ownerId")) {
         ownerId = currentOrNull();
      } else if (qName.equals("requesterId")) {
         requesterId = currentOrNull();
      } else if (qName.equals("reservationId")) {
         reservationId = currentOrNull();
      } else if (qName.equals("amiLaunchIndex")) {
         amiLaunchIndex = currentOrNull();
      } else if (qName.equals("dnsName")) {
         dnsName = currentOrNull();
      } else if (qName.equals("imageId")) {
         imageId = currentOrNull();
      } else if (qName.equals("instanceId")) {
         instanceId = currentOrNull();
      } else if (qName.equals("name")) {
         instanceState = InstanceState.fromValue(currentOrNull());
      } else if (qName.equals("instanceType")) {
         instanceType = InstanceType.fromValue(currentOrNull());
      } else if (qName.equals("ipAddress")) {
         ipAddress = parseInetAddress(currentOrNull());
      } else if (qName.equals("kernelId")) {
         kernelId = currentOrNull();
      } else if (qName.equals("keyName")) {
         keyName = currentOrNull();
      } else if (qName.equals("launchTime")) {
         launchTime = dateService.iso8601DateParse(currentOrNull());
      } else if (qName.equals("enabled")) {
         monitoring = Boolean.parseBoolean(currentOrNull());
      } else if (qName.equals("availabilityZone")) {
         availabilityZone = currentOrNull();
      } else if (qName.equals("platform")) {
         platform = currentOrNull();
      } else if (qName.equals("privateDnsName")) {
         privateDnsName = currentOrNull();
      } else if (qName.equals("privateIpAddress")) {
         privateIpAddress = parseInetAddress(currentOrNull());
      } else if (qName.equals("ramdiskId")) {
         ramdiskId = currentOrNull();
      } else if (qName.equals("reason")) {
         reason = currentOrNull();
      } else if (qName.equals("subnetId")) {
         subnetId = currentOrNull();
      } else if (qName.equals("vpcId")) {
         vpcId = currentOrNull();
      } else if (qName.equals("productCode")) {
         productCodes.add(currentOrNull());
      } else if (qName.equals("productCodesSet")) {
         inProductCodes = false;
      } else if (qName.equals("instancesSet")) {
         inInstances = false;
      } else if (qName.equals("groupSet")) {
         inGroups = false;
      } else if (qName.equals("item")) {
         inItem();
      }
      currentText = new StringBuilder();
   }

   protected void inItem() {
      if (inInstances && !inProductCodes) {
         instances.add(new RunningInstance(amiLaunchIndex, dnsName, imageId, instanceId,
                  instanceState, instanceType, ipAddress, kernelId, keyName, launchTime,
                  monitoring, availabilityZone, platform, privateDnsName, privateIpAddress,
                  productCodes, ramdiskId, reason, subnetId, vpcId));
         this.amiLaunchIndex = null;
         this.dnsName = null;
         this.imageId = null;
         this.instanceId = null;
         this.instanceState = null;
         this.instanceType = null;
         this.ipAddress = null;
         this.kernelId = null;
         this.keyName = null;
         this.launchTime = null;
         this.monitoring = false;
         this.availabilityZone = null;
         this.platform = null;
         this.privateDnsName = null;
         this.privateIpAddress = null;
         this.productCodes = Sets.newHashSet();
         this.ramdiskId = null;
         this.reason = null;
         this.subnetId = null;
         this.vpcId = null;
      }
   }

   private InetAddress parseInetAddress(String string) {
      String[] byteStrings = string.split("\\.");
      byte[] bytes = new byte[4];
      for (int i = 0; i < 4; i++) {
         bytes[i] = (byte) Integer.parseInt(byteStrings[i]);
      }
      try {
         return InetAddress.getByAddress(bytes);
      } catch (UnknownHostException e) {
         logger.warn(e, "error parsing ipAddress", currentText);
      }
      return null;
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   protected Reservation newReservation() {
      Reservation info = new Reservation(groupIds, instances, ownerId, requesterId, reservationId);
      this.groupIds = Sets.newTreeSet();
      this.instances = Sets.newTreeSet();
      this.ownerId = null;
      this.requesterId = null;
      this.reservationId = null;
      return info;
   }

}