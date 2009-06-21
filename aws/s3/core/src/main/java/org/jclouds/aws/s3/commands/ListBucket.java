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
package org.jclouds.aws.s3.commands;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.s3.commands.options.ListBucketOptions;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.xml.ListBucketHandler;
import org.jclouds.http.HttpMethod;
import org.jclouds.http.commands.callables.xml.ParseSax;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * A GET request operation using a bucket URI lists information about the objects in the bucket.
 * <p />
 * To list the keys of a bucket, you must have READ access to the bucket.
 * <p/>
 * List output is controllable via {@link ListBucketOptions}
 * 
 * @see ListBucketOptions
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTBucketGET.html"
 *      />
 * @author Adrian Cole
 * 
 */
public class ListBucket extends S3FutureCommand<S3Bucket> {

   @Inject
   public ListBucket(URI endPoint, ParseSax<S3Bucket> bucketParser, @Assisted String bucket,
            @Assisted ListBucketOptions options) {
      super(endPoint, HttpMethod.GET, "/" + options.buildQueryString(), bucketParser, bucket);
      ListBucketHandler handler = (ListBucketHandler) bucketParser.getHandler();
      handler.setBucketName(bucket);
   }

   @Override
   public S3Bucket get() throws InterruptedException, ExecutionException {
      try {
         return super.get();
      } catch (ExecutionException e) {
         return attemptNotFound(e);
      }
   }

   /**
    * {@code bucketParser} is only enacted when the http status code is 2xx. Amazon treats
    * NoSuchBucket as an exception, while we regard this as a valid response. Accordingly, we check
    * for this {@code NoSuchBucket} message and return {@code S3Bucket#NOT_FOUND} if present.
    * 
    */
   @VisibleForTesting
   S3Bucket attemptNotFound(ExecutionException e) throws ExecutionException {
      if (e.getCause() != null && e.getCause() instanceof AWSResponseException) {
         AWSResponseException responseException = (AWSResponseException) e.getCause();
         if ("NoSuchBucket".equals(responseException.getError().getCode())) {
            return S3Bucket.NOT_FOUND;
         }
      }
      throw e;
   }

   @Override
   public S3Bucket get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException,
            TimeoutException {
      try {
         return super.get(l, timeUnit);
      } catch (ExecutionException e) {
         return attemptNotFound(e);
      }
   }
}