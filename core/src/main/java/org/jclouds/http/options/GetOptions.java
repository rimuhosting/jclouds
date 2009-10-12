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
package org.jclouds.http.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.util.DateService;
import org.joda.time.DateTime;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;

/**
 * Contains options supported for HTTP GET operations. <h2>
 * Usage</h2> The recommended way to instantiate a GetObjectOptions object is to statically import
 * GetObjectOptions.Builder.* and invoke a static creation method followed by an instance mutator
 * (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.http.options.GetOptions.Builder.*
 * 
 * 
 * // this will get the first megabyte of an object, provided it wasn't modified since yesterday
 * Future<S3Object> object = client.get("objectName",range(0,1024).ifUnmodifiedSince(new DateTime().minusDays(1)));
 * <code>
 * 
 * @author Adrian Cole
 * 
 */
public class GetOptions extends BaseHttpRequestOptions {
   private final static DateService dateService = new DateService();
   public static final GetOptions NONE = new GetOptions();
   private final List<String> ranges = new ArrayList<String>();

   @Override
   public Multimap<String, String> buildRequestHeaders() {
      Multimap<String, String> headers = super.buildRequestHeaders();
      String range = getRange();
      if (range != null)
         headers.put("Range", this.getRange());
      return headers;
   }

   /**
    * download the specified range of the object.
    */
   public GetOptions range(long start, long end) {
      checkArgument(start >= 0, "start must be >= 0");
      checkArgument(end >= 0, "end must be >= 0");
      ranges.add(String.format("%d-%d", start, end));
      return this;
   }

   /**
    * download the object offset at <code>start</code>
    */
   public GetOptions startAt(long start) {
      checkArgument(start >= 0, "start must be >= 0");
      ranges.add(String.format("%d-", start));
      return this;
   }

   /**
    * download the last <code>count</code> bytes of the object
    */
   public GetOptions tail(long count) {
      checkArgument(count > 0, "count must be > 0");
      ranges.add(String.format("-%d", count));
      return this;
   }

   /**
    * For use in the header Range
    * <p />
    * 
    * @see GetOptions#range(long, long)
    */
   public String getRange() {
      return (ranges.size() > 0) ? String.format("bytes=%s", Joiner.on(",").join(ranges)) : null;
   }

   /**
    * Only return the object if it has changed since this time.
    * <p />
    * Not compatible with {@link #ifETagMatches(byte[])} or {@link #ifUnmodifiedSince(DateTime)}
    */
   public GetOptions ifModifiedSince(DateTime ifModifiedSince) {
      checkArgument(getIfMatch() == null,
               "ifETagMatches() is not compatible with ifModifiedSince()");
      checkArgument(getIfUnmodifiedSince() == null,
               "ifUnmodifiedSince() is not compatible with ifModifiedSince()");
      this.headers.put(HttpHeaders.IF_MODIFIED_SINCE, dateService.rfc822DateFormat(checkNotNull(
               ifModifiedSince, "ifModifiedSince")));
      return this;
   }

   /**
    * For use in the header If-Modified-Since
    * <p />
    * Return the object only if it has been modified since the specified time, otherwise return a
    * 304 (not modified).
    * 
    * @see GetOptions#ifModifiedSince(DateTime)
    */
   public String getIfModifiedSince() {
      return this.getFirstHeaderOrNull(HttpHeaders.IF_MODIFIED_SINCE);
   }

   /**
    * Only return the object if it hasn't changed since this time.
    * <p />
    * Not compatible with {@link #ifETagDoesntMatch(byte[])} or {@link #ifModifiedSince(DateTime)}
    */
   public GetOptions ifUnmodifiedSince(DateTime ifUnmodifiedSince) {
      checkArgument(getIfNoneMatch() == null,
               "ifETagDoesntMatch() is not compatible with ifUnmodifiedSince()");
      checkArgument(getIfModifiedSince() == null,
               "ifModifiedSince() is not compatible with ifUnmodifiedSince()");
      this.headers.put(HttpHeaders.IF_UNMODIFIED_SINCE, dateService.rfc822DateFormat(checkNotNull(
               ifUnmodifiedSince, "ifUnmodifiedSince")));
      return this;
   }

   /**
    * For use in the header If-Unmodified-Since
    * <p />
    * Return the object only if it has not been modified since the specified time, otherwise return
    * a 412 (precondition failed).
    * 
    * @see GetOptions#ifUnmodifiedSince(DateTime)
    */
   public String getIfUnmodifiedSince() {
      return this.getFirstHeaderOrNull(HttpHeaders.IF_UNMODIFIED_SINCE);
   }

   /**
    * The object's eTag hash should match the parameter <code>eTag</code>.
    * 
    * <p />
    * Not compatible with {@link #ifETagDoesntMatch(byte[])} or {@link #ifModifiedSince(DateTime)}
    * 
    * @param eTag
    *           hash representing the entity
    * @throws UnsupportedEncodingException
    *            if there was a problem converting this into an S3 eTag string
    */
   public GetOptions ifETagMatches(String eTag) throws UnsupportedEncodingException {
      checkArgument(getIfNoneMatch() == null,
               "ifETagDoesntMatch() is not compatible with ifETagMatches()");
      checkArgument(getIfModifiedSince() == null,
               "ifModifiedSince() is not compatible with ifETagMatches()");
      this.headers.put(HttpHeaders.IF_MATCH, String.format("\"%1$s\"", checkNotNull(eTag, "eTag")));
      return this;
   }

   /**
    * For use in the request header: If-Match
    * <p />
    * Return the object only if its entity tag (ETag) is the same as the eTag specified, otherwise
    * return a 412 (precondition failed).
    * 
    * @see GetOptions#ifETagMatches(byte[])
    */
   public String getIfMatch() {
      return this.getFirstHeaderOrNull(HttpHeaders.IF_MATCH);
   }

   /**
    * The object should not have a eTag hash corresponding with the parameter <code>eTag</code>.
    * <p />
    * Not compatible with {@link #ifETagMatches(String)} or {@link #ifUnmodifiedSince(DateTime)}
    * 
    * @param eTag
    *           hash representing the entity
    * @throws UnsupportedEncodingException
    *            if there was a problem converting this into an S3 eTag string
    */
   public GetOptions ifETagDoesntMatch(String eTag) throws UnsupportedEncodingException {
      checkArgument(getIfMatch() == null,
               "ifETagMatches() is not compatible with ifETagDoesntMatch()");
      checkArgument(getIfUnmodifiedSince() == null,
               "ifUnmodifiedSince() is not compatible with ifETagDoesntMatch()");
      this.headers.put(HttpHeaders.IF_NONE_MATCH, String.format("\"%1$s\"", checkNotNull(eTag,
               "ifETagDoesntMatch")));
      return this;
   }

   /**
    * For use in the request header: If-None-Match
    * <p />
    * Return the object only if its entity tag (ETag) is different from the one specified, otherwise
    * return a 304 (not modified).
    * 
    * @see GetOptions#ifETagDoesntMatch(byte[])
    */
   public String getIfNoneMatch() {
      return this.getFirstHeaderOrNull(HttpHeaders.IF_NONE_MATCH);
   }

   public static class Builder {

      /**
       * @see GetOptions#range(long, long)
       */
      public static GetOptions range(long start, long end) {
         GetOptions options = new GetOptions();
         return options.range(start, end);
      }

      /**
       * @see GetOptions#startAt(long)
       */
      public static GetOptions startAt(long start) {
         GetOptions options = new GetOptions();
         return options.startAt(start);
      }

      /**
       * @see GetOptions#tail(long)
       */
      public static GetOptions tail(long count) {
         GetOptions options = new GetOptions();
         return options.tail(count);
      }

      /**
       * @see GetOptions#getIfModifiedSince()
       */
      public static GetOptions ifModifiedSince(DateTime ifModifiedSince) {
         GetOptions options = new GetOptions();
         return options.ifModifiedSince(ifModifiedSince);
      }

      /**
       * @see GetOptions#ifUnmodifiedSince(DateTime)
       */
      public static GetOptions ifUnmodifiedSince(DateTime ifUnmodifiedSince) {
         GetOptions options = new GetOptions();
         return options.ifUnmodifiedSince(ifUnmodifiedSince);
      }

      /**
       * @see GetOptions#ifETagMatches(String)
       */
      public static GetOptions ifETagMatches(String eTag) throws UnsupportedEncodingException {
         GetOptions options = new GetOptions();
         return options.ifETagMatches(eTag);
      }

      /**
       * @see GetOptions#ifETagDoesntMatch(String)
       */
      public static GetOptions ifETagDoesntMatch(String eTag) throws UnsupportedEncodingException {
         GetOptions options = new GetOptions();
         return options.ifETagDoesntMatch(eTag);
      }

   }
}
