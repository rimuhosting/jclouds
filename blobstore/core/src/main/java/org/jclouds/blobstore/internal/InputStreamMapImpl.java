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
package org.jclouds.blobstore.internal;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.InputStreamMap;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.ListOptions;
import org.jclouds.blobstore.strategy.ClearListStrategy;
import org.jclouds.blobstore.strategy.ContainsValueInListStrategy;
import org.jclouds.blobstore.strategy.CountListStrategy;
import org.jclouds.blobstore.strategy.GetBlobsInListStrategy;
import org.jclouds.blobstore.strategy.ListBlobMetadataStrategy;
import org.jclouds.util.Utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

/**
 * Map representation of a live connection to S3. All put operations will result in ETag
 * calculation. If this is not desired, use {@link LiveBMap} instead.
 * 
 * @author Adrian Cole
 * @see BlobStore
 * @see InputStreamMap
 * @see BaseBlobMap
 */
public class InputStreamMapImpl extends BaseBlobMap<InputStream> implements InputStreamMap {

   @Inject
   public InputStreamMapImpl(BlobStore connection, Blob.Factory blobFactory,
            GetBlobsInListStrategy getAllBlobs, ListBlobMetadataStrategy getAllBlobMetadata,
            ContainsValueInListStrategy containsValueStrategy,
            ClearListStrategy clearContainerStrategy, CountListStrategy containerCountStrategy,
            String containerName, ListOptions listOptions) {
      super(connection, getAllBlobs, getAllBlobMetadata, containsValueStrategy,
               clearContainerStrategy, containerCountStrategy, containerName, listOptions);
   }

   /**
    * {@inheritDoc}
    * 
    * @see S3Client#getBlob(String, String)
    */
   public InputStream get(Object o) {
      String realKey = prefixer.apply(o.toString());
      try {
         return (InputStream) (connection.getBlob(containerName, realKey).get(
                  requestTimeoutMilliseconds, TimeUnit.MILLISECONDS)).getData();
      } catch (KeyNotFoundException e) {
         return null;
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException(String.format("Error geting object %1$s:%2$s",
                  containerName, realKey), e);
      }
   }

   /**
    * {@inheritDoc}
    * 
    * @see S3Client#removeBlob(String, String)
    */
   public InputStream remove(Object o) {
      InputStream old = getLastValue(o);
      String realKey = prefixer.apply(o.toString());
      try {
         connection.removeBlob(containerName, realKey).get(requestTimeoutMilliseconds,
                  TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException(String.format("Error removing object %1$s:%2$s",
                  containerName, realKey), e);
      }
      return old;
   }

   private InputStream getLastValue(Object o) {
      InputStream old;
      try {
         old = get(o);
      } catch (KeyNotFoundException e) {
         old = null;
      }
      return old;
   }

   /**
    * {@inheritDoc}
    * 
    * @see #getAllObjects()
    */
   public Collection<InputStream> values() {
      return Collections2.transform(getAllBlobs.execute(containerName, options),
               new Function<Blob, InputStream>() {
                  public InputStream apply(Blob from) {
                     return (InputStream) from.getData();
                  }
               });
   }

   /**
    * {@inheritDoc}
    * 
    * @see #getAllObjects()
    */
   public Set<Map.Entry<String, InputStream>> entrySet() {
      Set<Map.Entry<String, InputStream>> entrySet = new HashSet<Map.Entry<String, InputStream>>();
      for (Blob object : this.getAllBlobs.execute(containerName, options)) {
         entrySet.add(new Entry(pathStripper.apply(object.getMetadata().getName()),
                  (InputStream) object.getData()));
      }
      return entrySet;
   }

   public class Entry implements java.util.Map.Entry<String, InputStream> {

      private InputStream value;
      private String key;

      Entry(String key, InputStream value) {
         this.key = key;
         this.value = value;
      }

      public String getKey() {
         return key;
      }

      public InputStream getValue() {
         return value;
      }

      /**
       * {@inheritDoc}
       * 
       * @see InputStreamMapImpl#put(String, InputStream)
       */
      public InputStream setValue(InputStream value) {
         return put(key, value);
      }

   }

   /**
    * {@inheritDoc}
    * 
    * @see #putAllInternal(Map)
    */
   public void putAll(Map<? extends String, ? extends InputStream> map) {
      putAllInternal(map);
   }

   /**
    * {@inheritDoc}
    * 
    * @see #putAllInternal(Map)
    */
   public void putAllBytes(Map<? extends String, ? extends byte[]> map) {
      putAllInternal(map);
   }

   /**
    * {@inheritDoc}
    * 
    * @see #putAllInternal(Map)
    */
   public void putAllFiles(Map<? extends String, ? extends File> map) {
      putAllInternal(map);
   }

   /**
    * {@inheritDoc}
    * 
    * @see #putAllInternal(Map)
    */
   public void putAllStrings(Map<? extends String, ? extends String> map) {
      putAllInternal(map);
   }

   /**
    * submits requests to add all objects and collects the results later. All values will have eTag
    * calculated first. As a side-effect of this, the content will be copied into a byte [].
    * 
    * @see S3Client#put(String, Blob)
    */
   @VisibleForTesting
   void putAllInternal(Map<? extends String, ? extends Object> map) {
      try {
         Set<Future<String>> puts = Sets.newHashSet();
         for (Map.Entry<? extends String, ? extends Object> entry : map.entrySet()) {
            Blob object = connection.newBlob();
            object.getMetadata().setName(prefixer.apply(entry.getKey()));
            object.setData(entry.getValue());
            object.generateMD5();
            puts.add(connection.putBlob(containerName, object));
            // / ParamExtractor Funcion<?,String>
            // / response transformer set key on the way out.
            // / ExceptionHandler convert 404 to NOT_FOUND
         }
         for (Future<String> put : puts)
            // this will throw an exception if there was a problem
            put.get(requestTimeoutMilliseconds, TimeUnit.MILLISECONDS);
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException("Error putting into containerName" + containerName, e);
      }
   }

   /**
    * {@inheritDoc}
    * 
    * @see #putInternal(String, Object)
    */
   public InputStream putString(String key, String value) {
      return putInternal(key, value);
   }

   /**
    * {@inheritDoc}
    * 
    * @see #putInternal(String, Object)
    */
   public InputStream putFile(String key, File value) {
      return putInternal(key, value);
   }

   /**
    * {@inheritDoc}
    * 
    * @see #putInternal(String, Object)
    */
   public InputStream putBytes(String key, byte[] value) {
      return putInternal(key, value);
   }

   /**
    * {@inheritDoc}
    * 
    * @see #putInternal(String, Object)
    */
   public InputStream put(String key, InputStream value) {
      return putInternal(key, value);
   }

   /**
    * calculates eTag before adding the object to s3. As a side-effect of this, the content will be
    * copied into a byte []. *
    * 
    * @see S3Client#put(String, Blob)
    */
   @VisibleForTesting
   InputStream putInternal(String s, Object o) {
      Blob object = connection.newBlob();
      object.getMetadata().setName(prefixer.apply(s));
      try {
         InputStream returnVal = containsKey(s) ? get(s) : null;
         object.setData(o);
         object.generateMD5();
         connection.putBlob(containerName, object).get(requestTimeoutMilliseconds,
                  TimeUnit.MILLISECONDS);
         return returnVal;
      } catch (Exception e) {
         Utils.<BlobRuntimeException> rethrowIfRuntimeOrSameType(e);
         throw new BlobRuntimeException(String.format("Error adding object %1$s:%2$s",
                  containerName, object), e);
      }
   }

}
