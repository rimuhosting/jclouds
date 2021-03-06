#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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
package ${package};

import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import ${package}.domain.Status;

/**
 * Provides synchronous access to ${clientName}.
 * <p/>
 * 
 * @see ${clientName}AsyncClient
 * @see <a href="TODO: insert URL of client documentation" />
 * @author ${author}
 */
@Timeout(duration = 4, timeUnit = TimeUnit.SECONDS)
public interface ${clientName}Client {

   SortedSet<Status> getMyMentions();

}
