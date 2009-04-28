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
package org.jclouds.samples.googleappengine;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.Provider;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.HttpFutureCommandClient;
import org.jclouds.http.commands.CommandFactory;
import org.jclouds.http.commands.GetString;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
@Singleton
public class GetServlet extends HttpServlet {
    @Inject
    HttpFutureCommandClient client;
    @Inject
    CommandFactory factory;

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        URL url = new URL(httpServletRequest.getParameter("uri"));
        Writer writer = httpServletResponse.getWriter();
        InputStream in = null;
        try {
            in = url.openStream();
            writer.write(IOUtils.toString(in));
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
        writer.flush();
        writer.close();
    }

}