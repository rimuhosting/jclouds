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
import java.util.SortedSet;

import javax.annotation.Resource;

import org.jclouds.aws.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.logging.Logger;

import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
public class DescribeAddressesResponseHandler extends
         HandlerWithResult<SortedSet<PublicIpInstanceIdPair>> {

   @Resource
   protected Logger logger = Logger.NULL;
   private SortedSet<PublicIpInstanceIdPair> pairs = Sets.newTreeSet();
   private InetAddress ipAddress;
   private StringBuilder currentText = new StringBuilder();

   private String instanceId;

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("publicIp")) {
         ipAddress = parseInetAddress(currentOrNull());
      } else if (qName.equals("instanceId")) {
         instanceId = currentOrNull();
      } else if (qName.equals("item")) {
         pairs.add(new PublicIpInstanceIdPair(ipAddress, instanceId));
         ipAddress = null;
         instanceId = null;
      }
      currentText = new StringBuilder();
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
         throw new RuntimeException(e);
      }
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   @Override
   public SortedSet<PublicIpInstanceIdPair> getResult() {
      return pairs;
   }

}