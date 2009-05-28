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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.s3.commands.callables.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.aws.s3.commands.options.GetObjectOptions;
import org.jclouds.aws.s3.domain.S3Object;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * Retrieves the S3Object associated with the Key or {@link S3Object#NOT_FOUND} if not available;
 * 
 * <p/>
 * To use GET, you must have READ access to the object. If READ access is granted to the anonymous
 * user, you can request the object without an authorization header.
 * <p />
 * <p/>
 * This command allows you to specify {@link GetObjectOptions} to control delivery of content.
 * 
 * <h2>Note</h2> If you specify any of the below options, you will receive partial content:
 * <ul>
 * <li>{@link GetObjectOptions#range}</li>
 * <li>{@link GetObjectOptions#startAt}</li>
 * <li>{@link GetObjectOptions#tail}</li>
 * </ul>
 * 
 * @see GetObjectOptions
 * @see <a
 *      href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/index.html?RESTObjectGET.html"
 *      />
 * @author Adrian Cole
 */
public class GetObject extends S3FutureCommand<S3Object> {

   @Inject
   public GetObject(@Named("jclouds.http.address") String amazonHost,
            ParseObjectFromHeadersAndHttpContent callable, @Assisted("bucketName") String s3Bucket,
            @Assisted("key") String key, @Assisted GetObjectOptions options) {
      super("GET", "/" + checkNotNull(key), callable, amazonHost, s3Bucket);
      this.getRequest().getHeaders().putAll(options.buildRequestHeaders());
      callable.setKey(key);
   }

   @Override
   public S3Object get() throws InterruptedException, ExecutionException {
      try {
         return super.get();
      } catch (ExecutionException e) {
         return attemptNotFound(e);
      }
   }

   @VisibleForTesting
   S3Object attemptNotFound(ExecutionException e) throws ExecutionException {
      if (e.getCause() != null && e.getCause() instanceof AWSResponseException) {
         AWSResponseException responseException = (AWSResponseException) e.getCause();
         if ("NoSuchKey".equals(responseException.getError().getCode())) {
            return S3Object.NOT_FOUND;
         }
      }
      throw e;
   }

   @Override
   public S3Object get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException,
            TimeoutException {
      try {
         return super.get(l, timeUnit);
      } catch (ExecutionException e) {
         return attemptNotFound(e);
      }
   }
}