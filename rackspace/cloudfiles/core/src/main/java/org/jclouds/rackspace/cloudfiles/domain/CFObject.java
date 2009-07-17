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
package org.jclouds.rackspace.cloudfiles.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpUtils;
import org.jclouds.http.HttpUtils.ETagInputStreamResult;
import org.joda.time.DateTime;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Rackspace Cloud Files is designed to store objects. Objects are stored in 
 * {@link ContainerMetadata containers} and consist of a 
 * {@link CFObject#getData() value}, a {@link CFObject#getKey() key}, and
 * {@link CFObject.Metadata#getUserMetadata() metadata}.
 * 
 * @author Adrian Cole
 * @author James Murty
 */
public class CFObject {
   public static final CFObject NOT_FOUND = new CFObject(Metadata.NOT_FOUND);

   private Object data;
   private final Metadata metadata;
   private long contentLength = -1;
   private String contentRange;

   public CFObject(String key) {
      this(new Metadata(key));
   }

   public CFObject(Metadata metadata) {
      this.metadata = metadata;
   }

   public CFObject(Metadata metadata, Object data) {
      this(metadata);
      setData(data);
   }

   public CFObject(String key, Object data) {
      this(key);
      setData(data);
   }

   /**
    * System and user Metadata for the {@link CFObject}.
    * 
    * @author Adrian Cole
    */
   public static class Metadata implements Comparable<Metadata> {
      public static final Metadata NOT_FOUND = new Metadata("NOT_FOUND");

      // parsed during list, head, or get
      private String key;
      private byte[] eTag;
      private volatile long size = -1;

      // only parsed during head or get
      private Multimap<String, String> allHeaders = HashMultimap.create();
      private Multimap<String, String> userMetadata = HashMultimap.create();
      private DateTime lastModified;
      private String dataType = MediaType.APPLICATION_OCTET_STREAM;
      private String cacheControl;
      private String dataDisposition;
      private String dataEncoding;

      public Metadata() {
         super();
      }

      /**
       * @param key
       * @see #getKey()
       */
      public Metadata(String key) {
         setKey(key);
      }

      public void setKey(String key) {
         checkNotNull(key, "key");
         checkArgument(!key.startsWith("/"), "keys cannot start with /");
         this.key = key;
      }

      @Override
      public String toString() {
         final StringBuilder sb = new StringBuilder();
         sb.append("Metadata");
         sb.append("{key='").append(key).append('\'');
         sb.append(", lastModified=").append(lastModified);
         sb.append(", eTag=").append(
                  getETag() == null ? "null" : Arrays.asList(getETag()).toString());
         sb.append(", size=").append(size);
         sb.append(", dataType='").append(dataType).append('\'');
         sb.append('}');
         return sb.toString();
      }

      @Override
      public boolean equals(Object o) {
         if (this == o)
            return true;
         if (!(o instanceof Metadata))
            return false;

         Metadata metadata = (Metadata) o;

         if (size != metadata.size)
            return false;
         if (dataType != null ? !dataType.equals(metadata.dataType) : metadata.dataType != null)
            return false;
         if (!key.equals(metadata.key))
            return false;
         if (lastModified != null ? !lastModified.equals(metadata.lastModified)
                  : metadata.lastModified != null)
            return false;
         if (!Arrays.equals(getETag(), metadata.getETag()))
            return false;
         return true;
      }

      @Override
      public int hashCode() {
         int result = key.hashCode();
         result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
         result = 31 * result + (getETag() != null ? Arrays.hashCode(getETag()) : 0);
         result = 31 * result + (int) (size ^ (size >>> 32));
         result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
         return result;
      }

      /**
       * The key is the handle that you assign to an object that allows you retrieve it later. A key
       * is a sequence of Unicode characters whose UTF-8 encoding is at most 1024 bytes long. Each
       * object in a bucket must have a unique key.
       */
      public String getKey() {
         return key;
      }

      public DateTime getLastModified() {
         return lastModified;
      }

      public void setLastModified(DateTime lastModified) {
         this.lastModified = lastModified;
      }

      /**
       * The size of the object, in bytes.
       * 
       * @see <a href= "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.13." />
       */
      public long getSize() {
         return size;
      }

      public void setSize(long size) {
         this.size = size;
      }

      /**
       * A standard MIME type describing the format of the contents. If none is provided, the
       * default is binary/octet-stream.
       * 
       * @see <a href= "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.17." />
       */
      public String getContentType() {
         return dataType;
      }

      public void setContentType(String dataType) {
         this.dataType = dataType;
      }

      public void setETag(byte[] eTag) {
         this.eTag = new byte[eTag.length];
         System.arraycopy(eTag, 0, this.eTag, 0, eTag.length);
      }

      /**
       * @return the eTag value stored in the Etag header returned by the service.
       */
      public byte[] getETag() {
         if (eTag != null) {
            byte[] retval = new byte[eTag.length];
            System.arraycopy(this.eTag, 0, retval, 0, eTag.length);
            return retval;
         } else {
            return null;
         }
      }

      public void setUserMetadata(Multimap<String, String> userMetadata) {
         this.userMetadata = userMetadata;
      }

      /**
       * Any header starting with <code>X-Object-Meta-</code> is considered user metadata. It will 
       * be stored with the object and returned when you retrieve the object. The total size of the
       * HTTP request, not including the body, must be less than 8 KB.
       */
      public Multimap<String, String> getUserMetadata() {
         return userMetadata;
      }

      public void setCacheControl(String cacheControl) {
         this.cacheControl = cacheControl;
      }

      /**
       * Can be used to specify caching behavior along the request/reply chain.
       * 
       * @link http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.9.
       */
      public String getCacheControl() {
         return cacheControl;
      }

      public void setContentDisposition(String dataDisposition) {
         this.dataDisposition = dataDisposition;
      }

      /**
       * Specifies presentational information for the object.
       * 
       * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html?sec19.5.1."/>
       */
      public String getContentDisposition() {
         return dataDisposition;
      }

      public void setContentEncoding(String dataEncoding) {
         this.dataEncoding = dataEncoding;
      }

      /**
       * Specifies what content encodings have been applied to the object and thus what decoding
       * mechanisms must be applied in order to obtain the media-type referenced by the Content-Type
       * header field.
       * 
       * @see <a href= "http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html?sec14.11" />
       */
      public String getContentEncoding() {
         return dataEncoding;
      }

      public void setAllHeaders(Multimap<String, String> allHeaders) {
         this.allHeaders = allHeaders;
      }

      /**
       * @return all http response headers associated with this object
       */
      public Multimap<String, String> getAllHeaders() {
         return allHeaders;
      }

      public int compareTo(Metadata o) {
         return (this == o) ? 0 : getKey().compareTo(o.getKey());
      }
   }

   /**
    * @see Metadata#getKey()
    */
   public String getKey() {
      return metadata.getKey();
   }

   /**
    * Sets entity for the request or the content from the response.
    * 
    * @param data
    *           typically InputStream for downloads, or File, byte [], String, or InputStream for
    *           uploads.
    */
   public void setData(Object data) {
      this.data = checkNotNull(data, "data");
   }

   /**
    * generate an MD5 Hash for the current data.
    * <p/>
    * <h2>Note</h2>
    * <p/>
    * If this is an InputStream, it will be converted to a byte array first.
    * 
    * @throws IOException
    *            if there is a problem generating the hash.
    */
   public void generateETag() throws IOException {
      checkState(data != null, "data");
      if (data instanceof InputStream) {
         ETagInputStreamResult result = HttpUtils.generateETagResult((InputStream) data);
         getMetadata().setSize(result.length);
         getMetadata().setETag(result.eTag);
         setData(result.data);
      } else {
         getMetadata().setETag(HttpUtils.eTag(data));
      }
   }

   /**
    * @return InputStream, if downloading, or whatever was set during {@link #setData(Object)}
    */
   public Object getData() {
      return data;
   }

   /**
    * @return System and User metadata relevant to this object.
    */
   public Metadata getMetadata() {
      return metadata;
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("CFObject");
      sb.append("{metadata=").append(metadata);
      sb.append('}');
      return sb.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (!(o instanceof CFObject))
         return false;

      CFObject cfObject = (CFObject) o;

      if (data != null ? !data.equals(cfObject.data) : cfObject.data != null)
         return false;
      if (!metadata.equals(cfObject.metadata))
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = data != null ? data.hashCode() : 0;
      result = 31 * result + metadata.hashCode();
      return result;
   }

   public void setContentLength(long contentLength) {
      this.contentLength = contentLength;
   }

   /**
    * Returns the total size of the downloaded object, or the chunk that's available.
    * <p/>
    * Chunking is only used when
    * TODO: Does Cloud Files support content ranges?
    * is called with options like tail, range, or startAt.
    * 
    * @return the length in bytes that can be be obtained from {@link #getData()}
    * @see org.jclouds.http.HttpHeaders#CONTENT_LENGTH
    * @see GetObjectOptions
    */
   public long getContentLength() {
      return contentLength;
   }

   public void setContentRange(String contentRange) {
      this.contentRange = contentRange;
   }

   /**
    * If this is not-null, {@link #getContentLength() } will the size of chunk of the object
    * available via {@link #getData()}
    * 
    * @see org.jclouds.http.HttpHeaders#CONTENT_RANGE
    * @see GetObjectOptions
    */
   public String getContentRange() {
      return contentRange;
   }

}
