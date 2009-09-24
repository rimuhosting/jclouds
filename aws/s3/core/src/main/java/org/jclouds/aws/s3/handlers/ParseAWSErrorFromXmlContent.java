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
package org.jclouds.aws.s3.handlers;

import javax.annotation.Resource;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.s3.util.S3Utils;
import org.jclouds.aws.s3.xml.S3ParserFactory;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.util.Utils;

import javax.inject.Inject;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @see AWSError
 * @author Adrian Cole
 * 
 */
public class ParseAWSErrorFromXmlContent implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   private final S3ParserFactory parserFactory;
   private final S3Utils utils;

   @Inject
   public ParseAWSErrorFromXmlContent(S3Utils utils, S3ParserFactory parserFactory) {
      this.utils = utils;
      this.parserFactory = parserFactory;
   }

   public void handleError(HttpCommand command, HttpResponse response) {
      String content;
      try {
         content = response.getContent() != null ? Utils.toStringAndClose(response.getContent())
                  : null;
         if (content != null) {
            try {
               if (content.indexOf('<') >= 0) {
                  AWSError error = utils.parseAWSErrorFromContent(parserFactory, command, response,
                           content);
                  command.setException(new AWSResponseException(command, response, error));
               } else {
                  command.setException(new HttpResponseException(command, response, content));
               }
            } catch (Exception he) {
               command.setException(new HttpResponseException(command, response, content));
               Utils.rethrowIfRuntime(he);
            }
         } else {
            command.setException(new HttpResponseException(command, response));
         }
      } catch (Exception e) {
         command.setException(new HttpResponseException(command, response));
         Utils.rethrowIfRuntime(e);
      }
   }

}