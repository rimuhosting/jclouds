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
package org.jclouds.aws.handlers;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.logging.Logger;
import org.jclouds.util.Utils;

import com.google.inject.Inject;

/**
 * Handles Retryable responses with error codes in the 3xx range
 * 
 * @author Adrian Cole
 */
public class AWSClientErrorRetryHandler implements HttpRetryHandler {

   @Inject(optional = true)
   @Named(HttpConstants.PROPERTY_HTTP_MAX_RETRIES)
   private int retryCountLimit = 5;

   private final AWSUtils utils;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public AWSClientErrorRetryHandler(AWSUtils utils) {
      this.utils = utils;
   }

   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      if (command.getFailureCount() > retryCountLimit)
         return false;
      if (response.getStatusCode() == 400 || response.getStatusCode() == 403
               || response.getStatusCode() == 409) {
         byte[] content = Utils.closeClientButKeepContentStream(response);
         command.incrementFailureCount();
         try {
            AWSError error = utils.parseAWSErrorFromContent(command, response, new String(content));
            if ("RequestTimeout".equals(error.getCode())
                     || "OperationAborted".equals(error.getCode())
                     || "SignatureDoesNotMatch".equals(error.getCode())) {
               return true;
            }
         } catch (HttpException e) {
            logger.warn(e, "error parsing response: %s", new String(content));
         }
      }
      return false;
   }

}
