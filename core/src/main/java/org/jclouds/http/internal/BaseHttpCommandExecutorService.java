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
package org.jclouds.http.internal;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.logging.Logger;

public abstract class BaseHttpCommandExecutorService<Q> implements HttpCommandExecutorService {

   private final DelegatingRetryHandler retryHandler;
   private final DelegatingErrorHandler errorHandler;
   private final ExecutorService executorService;

   @Resource
   protected Logger logger = Logger.NULL;

   protected BaseHttpCommandExecutorService(ExecutorService executorService,
            DelegatingRetryHandler retryHandler, DelegatingErrorHandler errorHandler) {
      this.retryHandler = retryHandler;
      this.errorHandler = errorHandler;
      this.executorService = executorService;
   }

   public Future<HttpResponse> submit(HttpCommand command) {
      return executorService.submit(new HttpResponseCallable(command));
   }

   public class HttpResponseCallable implements Callable<HttpResponse> {
      private final HttpCommand command;

      public HttpResponseCallable(HttpCommand command) {
         this.command = command;
      }

      public HttpResponse call() throws Exception {

         HttpResponse response = null;
         for (;;) {
            HttpRequest request = command.getRequest();
            Q nativeRequest = null;
            try {
               logger.trace("%s - converting request %s", request.getEndpoint(), request);
               for (HttpRequestFilter filter : request.getFilters()) {
                  filter.filter(request);
               }
               nativeRequest = convert(request);
               response = invoke(nativeRequest);
               int statusCode = response.getStatusCode();
               if (statusCode >= 300) {
                  if (retryHandler.shouldRetryRequest(command, response)) {
                     continue;
                  } else {
                     errorHandler.handleError(command, response);
                     break;
                  }
               } else {
                  break;
               }
            } finally {
               cleanup(nativeRequest);
            }
         }
         if (command.getException() != null)
            throw command.getException();
         return response;
      }
   }

   protected abstract Q convert(HttpRequest request) throws IOException;

   protected abstract HttpResponse invoke(Q nativeRequest) throws IOException;

   protected abstract void cleanup(Q nativeResponse);

}