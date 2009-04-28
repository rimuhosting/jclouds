/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
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
package org.jclouds.aws.s3;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.Scopes;
import org.jclouds.aws.s3.commands.config.S3CommandsModule;
import org.jclouds.aws.s3.filters.RemoveTransferEncodingHeader;
import org.jclouds.aws.s3.filters.RequestAuthorizeSignature;
import org.jclouds.http.HttpRequestFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public class S3ConnectionModule extends AbstractModule {

    protected void configure() {
        install(new S3CommandsModule());
        bind(S3Connection.class).to(LiveS3Connection.class).in(Scopes.SINGLETON);
    }

    @Provides
    @Singleton
    List<HttpRequestFilter> provideRequestFilters(RemoveTransferEncodingHeader removTransferEncodingHeader, RequestAuthorizeSignature requestAuthorizeSignature) {
        List<HttpRequestFilter> filters = new ArrayList<HttpRequestFilter>();
        filters.add(removTransferEncodingHeader);
        filters.add(requestAuthorizeSignature);
        return filters;
    }
}

