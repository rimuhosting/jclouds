/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Parses dates found in XML responses and HTTP response headers.
 * 
 * @author Adrian Cole
 * 
 */
public class DateService {
    private DateTimeFormatter headerDateFormat = DateTimeFormat
	    .forPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'");

    public DateTime dateTimeFromXMLFormat(String toParse) {
	// the format is natively parseable from the DateTime constructor
	return new DateTime(toParse);
    }

    public DateTime dateTimeFromHeaderFormat(String toParse) {
	return headerDateFormat.parseDateTime(toParse);
    }

    public String timestampAsHeaderString() {
	return toHeaderString(new DateTime());
    }

    public String toHeaderString(DateTime date) {
	return headerDateFormat.print(date.withZone(DateTimeZone.UTC));
    }
}
