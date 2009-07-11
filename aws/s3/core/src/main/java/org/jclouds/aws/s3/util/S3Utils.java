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
package org.jclouds.aws.s3.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.filters.RequestAuthorizeSignature;
import org.jclouds.aws.s3.reference.S3Headers;
import org.jclouds.aws.s3.xml.S3ParserFactory;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.util.Utils;

import com.google.inject.Inject;

/**
 * Encryption, Hashing, and IO Utilities needed to sign and verify S3 requests and responses.
 * 
 * @author Adrian Cole
 */
public class S3Utils {

   @Inject
   RequestAuthorizeSignature signer;

   public AWSError parseAWSErrorFromContent(S3ParserFactory parserFactory, HttpCommand command,
            HttpResponse response, InputStream content) throws HttpException {
      AWSError error = parserFactory.createErrorParser().parse(content);
      error.setRequestId(response.getFirstHeaderOrNull(S3Headers.REQUEST_ID));
      error.setRequestToken(response.getFirstHeaderOrNull(S3Headers.REQUEST_TOKEN));
      if ("SignatureDoesNotMatch".equals(error.getCode()))
         error.setStringSigned(signer.createStringToSign(command.getRequest()));
      return error;

   }

   public AWSError parseAWSErrorFromContent(S3ParserFactory parserFactory, HttpCommand command,
            HttpResponse response, String content) throws HttpException {
      return parseAWSErrorFromContent(parserFactory, command, response, new ByteArrayInputStream(
               content.getBytes()));
   }

   public static String validateBucketName(String bucketName) {
      checkNotNull(bucketName, "bucketName");
      checkArgument(bucketName.matches("^[a-z0-9].*"),
               "bucketName name must start with a number or letter");
      checkArgument(
               bucketName.matches("^[-_.a-z0-9]+"),
               "bucketName name can only contain lowercase letters, numbers, periods (.), underscores (_), and dashes (-)");
      checkArgument(bucketName.length() > 2 && bucketName.length() < 256,
               "bucketName name must be between 3 and 255 characters long");
      checkArgument(!HttpUtils.IP_PATTERN.matcher(bucketName).matches(),
               "bucketName name cannot be ip address style");
      return bucketName;
   }

   public static String getContentAsStringAndClose(S3Object object) throws IOException {
      checkNotNull(object, "s3Object");
      checkNotNull(object.getData(), "s3Object.content");
      Object o = object.getData();

      if (o instanceof InputStream) {
         String returnVal = Utils.toStringAndClose((InputStream) o);
         if (object.getMetadata().getContentType().indexOf("xml") >= 0) {

         }
         return returnVal;
      } else {
         throw new IllegalArgumentException("Object type not supported: " + o.getClass().getName());
      }
   }
}