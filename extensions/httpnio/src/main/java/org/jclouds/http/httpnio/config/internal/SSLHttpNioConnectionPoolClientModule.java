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
package org.jclouds.http.httpnio.config.internal;

import org.apache.http.impl.nio.SSLClientIOEventDispatch;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.protocol.AsyncNHttpClientHandler;
import org.apache.http.params.HttpParams;
import org.jclouds.http.httpnio.config.internal.BaseHttpNioConnectionPoolClientModule;

import javax.net.ssl.SSLContext;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public class SSLHttpNioConnectionPoolClientModule extends BaseHttpNioConnectionPoolClientModule {

    protected void configure() {
        super.configure();
    }

    // note until a bug is fixed, you cannot annotate overriding methods with google annotations
    // http://code.google.com/p/google-guice/issues/detail?id=347
    @Override
    public IOEventDispatch provideClientEventDispatch(AsyncNHttpClientHandler handler, HttpParams params) throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        return new SSLClientIOEventDispatch(
                handler,
                context,
                params);
    }
}