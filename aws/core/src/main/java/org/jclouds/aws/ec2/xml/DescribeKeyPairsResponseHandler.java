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
package org.jclouds.aws.ec2.xml;

import java.util.SortedSet;

import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.Sets;

/**
 * Parses: DescribeKeyPairsResponse xmlns="http://ec2.amazonaws.com/doc/2009-08-15/"
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeKeyPairs.html"
 *      />
 * @author Adrian Cole
 */
public class DescribeKeyPairsResponseHandler extends ParseSax.HandlerWithResult<SortedSet<KeyPair>> {

   private StringBuilder currentText = new StringBuilder();
   private SortedSet<KeyPair> keyPairs = Sets.newTreeSet();
   private String keyFingerprint;
   private String keyName;

   public SortedSet<KeyPair> getResult() {
      return keyPairs;
   }

   public void endElement(String uri, String name, String qName) {

      if (qName.equals("keyFingerprint")) {
         this.keyFingerprint = currentText.toString().trim();
      } else if (qName.equals("item")) {
         keyPairs.add(new KeyPair(keyName, keyFingerprint, null));
      } else if (qName.equals("keyName")) {
         this.keyName = currentText.toString().trim();
      }

      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
