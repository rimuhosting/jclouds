/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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
package org.jclouds.aws.s3.domain.acl;

/**
 * Description from Amazon's documentation:
 * 
 * <p />
 * Because of restrictions in what can be sent via http headers, Amazon S3
 * supports the concept of canned access policies for REST. A canned access
 * policy can be included with the x-amz-acl header as part of a PUT operation
 * to provide shorthand representation of a full access policy. When Amazon S3
 * sees the x-amz-acl header as part of a PUT operation, it will assign the
 * respective access policy to the resource created as a result of the PUT. If
 * no x-amz-acl header is included with a PUT request, then the bucket or object
 * is written with the private access control policy (even if, in the case of an
 * object, the object already exists with some other pre-existing access control
 * policy).
 * 
 * @see http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?
 *      RESTAccessPolicy.html
 * @author Adrian Cole
 * 
 */
public enum CannedAccessPolicy {

    /**
     * Owner gets FULL_CONTROL. No one else has access rights (default).
     */
    PRIVATE("private"),
    /**
     * Owner gets FULL_CONTROL and the anonymous principal is granted READ
     * access. If this policy is used on an object, it can be read from a
     * browser with no authentication.
     */
    PUBLIC_READ("public-read"),
    /**
     * Owner gets FULL_CONTROL, the anonymous principal is granted READ and
     * WRITE access. This can be a useful policy to apply to a bucket, but is
     * generally not recommended.
     */
    PUBLIC_READ_WRITE("public-read-write"),
    /**
     * Owner gets FULL_CONTROL, and any principal authenticated as a registered
     * Amazon S3 user is granted READ access.
     */
    AUTHENTICATED_READ("authenticated-read");

    private String policyName;

    CannedAccessPolicy(String policyName) {
	this.policyName = policyName;
    }

    @Override
    public String toString() {
	return policyName;
    }
}
