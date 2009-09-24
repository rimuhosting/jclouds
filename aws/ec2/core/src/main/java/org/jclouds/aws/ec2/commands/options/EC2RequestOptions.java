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
package org.jclouds.aws.ec2.commands.options;

import org.jclouds.aws.ec2.reference.EC2Constants;
import org.jclouds.http.options.HttpRequestOptions;
import org.joda.time.DateTime;

import javax.inject.Named;

/**
 * 
 * defines the interface needed to properly sign EC2 QUERY requests.
 * 
 * @author Adrian Cole
 */
public interface EC2RequestOptions extends HttpRequestOptions {

   /**
    * @see org.jclouds.aws.ec2.reference.CommonEC2Parameters#ACTION
    */
   String getAction();

   /**
    * @see org.jclouds.aws.ec2.reference.CommonEC2Parameters#AWS_ACCESS_KEY_ID
    * @see org.jclouds.aws.ec2.reference.CommonEC2Parameters#SIGNATURE
    */
   EC2RequestOptions signWith(@Named(EC2Constants.PROPERTY_AWS_ACCESSKEYID) String accessKey,
            @Named(EC2Constants.PROPERTY_AWS_SECRETACCESSKEY) String secretKey);

   /**
    * @see org.jclouds.aws.ec2.reference.CommonEC2Parameters#EXPIRES
    */
   EC2RequestOptions expireAt(DateTime time);

   /**
    * @see org.jclouds.aws.ec2.reference.CommonEC2Parameters#TIMESTAMP
    */
   EC2RequestOptions timeStamp();

   /**
    * @see org.jclouds.http.HttpHeaders#HOST
    */
   EC2RequestOptions usingHost(String hostname);

}
