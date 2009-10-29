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
package org.jclouds.blobstore.integration.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.attr.ConsistencyModel;
import org.jclouds.blobstore.attr.ConsistencyModels;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.BoundedSortedSet;
import org.jclouds.blobstore.domain.MutableBlobMetadata;
import org.jclouds.blobstore.domain.MutableResourceMetadata;
import org.jclouds.blobstore.domain.ResourceMetadata;
import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.blobstore.domain.internal.BoundedTreeSet;
import org.jclouds.blobstore.domain.internal.MutableResourceMetadataImpl;
import org.jclouds.blobstore.functions.HttpGetOptionsListToGetOptions;
import org.jclouds.blobstore.options.GetOptions;
import org.jclouds.blobstore.options.ListOptions;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.options.HttpRequestOptions;
import org.jclouds.util.DateService;
import org.jclouds.util.Utils;
import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.internal.Nullable;

/**
 * Implementation of {@link S3BlobStore} which keeps all data in a local Map object.
 * 
 * @author Adrian Cole
 * @author James Murty
 */
@ConsistencyModel(ConsistencyModels.STRICT)
public class StubBlobStore implements BlobStore {

   protected final DateService dateService;
   private final ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs;
   protected final Blob.Factory blobProvider;
   protected final HttpGetOptionsListToGetOptions httpGetOptionsConverter;

   @Inject
   protected StubBlobStore(ConcurrentMap<String, ConcurrentMap<String, Blob>> containerToBlobs,
            DateService dateService, Blob.Factory blobProvider,
            HttpGetOptionsListToGetOptions httpGetOptionsConverter) {
      this.dateService = checkNotNull(dateService, "dateService");
      this.containerToBlobs = checkNotNull(containerToBlobs, "containerToBlobs");
      this.blobProvider = checkNotNull(blobProvider, "blobProvider");
      this.httpGetOptionsConverter = checkNotNull(httpGetOptionsConverter,
               "httpGetOptionsConverter");
   }

   /**
    * @throws java.io.IOException
    */
   public static byte[] toByteArray(Object data) throws IOException {
      checkNotNull(data, "data must be set before calling generateETag()");
      byte[] bytes = null;
      if (data == null || data instanceof byte[]) {
         bytes = (byte[]) data;
      } else if (data instanceof String) {
         bytes = ((String) data).getBytes();
      } else if (data instanceof File || data instanceof InputStream) {
         InputStream io = (data instanceof InputStream) ? (InputStream) data : new FileInputStream(
                  (File) data);
         bytes = IOUtils.toByteArray(io);
         IOUtils.closeQuietly(io);
      } else {
         throw new UnsupportedOperationException("Content not supported " + data.getClass());
      }
      return bytes;

   }

   public Future<Blob> getBlob(final String bucketName, final String key) {
      return new FutureBase<Blob>() {
         public Blob get() throws InterruptedException, ExecutionException {
            if (!getContainerToBlobs().containsKey(bucketName))
               throw new ContainerNotFoundException(bucketName);
            Map<String, Blob> realContents = getContainerToBlobs().get(bucketName);
            if (!realContents.containsKey(key))
               throw new KeyNotFoundException(bucketName, key);

            Blob object = realContents.get(key);
            Blob returnVal = blobProvider.create(copy(object.getMetadata()));
            returnVal.setData(new ByteArrayInputStream((byte[]) object.getData()));
            return returnVal;
         }
      };
   }

   public Future<? extends BoundedSortedSet<? extends ResourceMetadata>> list(final String name,
            ListOptions... optionsList) {
      final ListOptions options = (optionsList.length == 0) ? new ListOptions() : optionsList[0];
      return new FutureBase<BoundedSortedSet<ResourceMetadata>>() {
         public BoundedSortedSet<ResourceMetadata> get() throws InterruptedException,
                  ExecutionException {
            final Map<String, Blob> realContents = getContainerToBlobs().get(name);

            if (realContents == null)
               throw new ContainerNotFoundException(name);

            SortedSet<ResourceMetadata> contents = Sets.newTreeSet(Iterables.transform(realContents
                     .keySet(), new Function<String, ResourceMetadata>() {
               public ResourceMetadata apply(String key) {
                  return copy(realContents.get(key).getMetadata());
               }
            }));

            if (options.getMarker() != null) {
               final String finalMarker = options.getMarker();
               ResourceMetadata lastMarkerMetadata = Iterables.find(contents,
                        new Predicate<ResourceMetadata>() {
                           public boolean apply(ResourceMetadata metadata) {
                              return metadata.getName().equals(finalMarker);
                           }
                        });
               contents = contents.tailSet(lastMarkerMetadata);
               contents.remove(lastMarkerMetadata);
            }

            final String prefix = options.getPath();
            if (prefix != null) {
               contents = Sets.newTreeSet(Iterables.filter(contents,
                        new Predicate<ResourceMetadata>() {
                           public boolean apply(ResourceMetadata o) {
                              return (o != null && o.getName().startsWith(prefix));
                           }
                        }));
            }

            int maxResults = contents.size();
            boolean truncated = false;
            String marker = null;
            if (options.getMaxResults() != null) {
               SortedSet<ResourceMetadata> contentsSlice = firstSliceOfSize(contents, options
                        .getMaxResults().intValue());
               maxResults = options.getMaxResults();
               if (!contentsSlice.contains(contents.last())) {
                  // Partial listing
                  truncated = true;
                  marker = contentsSlice.last().getName();
               } else {
                  marker = null;
               }
               contents = contentsSlice;
            }

            final String delimiter = options.getRecursive() ? null : "/";
            if (delimiter != null) {
               SortedSet<String> commonPrefixes = null;
               Iterable<String> iterable = Iterables.transform(contents, new CommonPrefixes(
                        prefix != null ? prefix : null, delimiter));
               commonPrefixes = iterable != null ? Sets.newTreeSet(iterable)
                        : new TreeSet<String>();
               commonPrefixes.remove(CommonPrefixes.NO_PREFIX);

               contents = Sets.newTreeSet(Iterables.filter(contents, new DelimiterFilter(
                        prefix != null ? prefix : null, delimiter)));

               Iterables.<ResourceMetadata> addAll(contents, Iterables.transform(commonPrefixes,
                        new Function<String, ResourceMetadata>() {
                           public ResourceMetadata apply(String o) {
                              MutableResourceMetadata md = new MutableResourceMetadataImpl();
                              md.setType(ResourceType.RELATIVE_PATH);
                              md.setName(o);
                              return md;
                           }
                        }));
            }
            return new BoundedTreeSet<ResourceMetadata>(contents, prefix, marker, maxResults,
                     truncated);
         }
      };
   }

   public MutableBlobMetadata copy(MutableBlobMetadata in) {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      ObjectOutput os;
      try {
         os = new ObjectOutputStream(bout);
         os.writeObject(in);
         ObjectInput is = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
         MutableBlobMetadata metadata = (MutableBlobMetadata) is.readObject();
         convertUserMetadataKeysToLowercase(metadata);
         return metadata;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   private void convertUserMetadataKeysToLowercase(MutableBlobMetadata metadata) {
      Map<String, String> lowerCaseUserMetadata = Maps.newHashMap();
      for (Entry<String, String> entry : metadata.getUserMetadata().entrySet()) {
         lowerCaseUserMetadata.put(entry.getKey().toLowerCase(), entry.getValue());
      }
      metadata.setUserMetadata(lowerCaseUserMetadata);
   }

   public MutableBlobMetadata copy(MutableBlobMetadata in, String newKey) {
      MutableBlobMetadata newMd = copy(in);
      newMd.setName(newKey);
      return newMd;
   }

   public BlobMetadata metadata(final String container, final String key) {
      if (!getContainerToBlobs().containsKey(container))
         throw new ContainerNotFoundException(container);
      Map<String, Blob> realContents = getContainerToBlobs().get(container);
      if (!realContents.containsKey(key))
         throw new KeyNotFoundException(container, key);
      return copy(realContents.get(key).getMetadata());
   }

   public Future<Void> removeBlob(final String container, final String key) {
      return new FutureBase<Void>() {
         public Void get() throws InterruptedException, ExecutionException {
            if (getContainerToBlobs().containsKey(container)) {
               getContainerToBlobs().get(container).remove(key);
            }
            return null;
         }
      };
   }

   public Future<Void> deleteContainer(final String container) {
      return new FutureBase<Void>() {
         public Void get() throws InterruptedException, ExecutionException {
            if (getContainerToBlobs().containsKey(container)) {
               getContainerToBlobs().remove(container);
            }
            return null;
         }
      };
   }

   public Future<Boolean> deleteContainerImpl(final String container) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            if (getContainerToBlobs().containsKey(container)) {
               if (getContainerToBlobs().get(container).size() == 0)
                  getContainerToBlobs().remove(container);
               else
                  return false;
            }
            return true;
         }
      };
   }

   public boolean exists(final String container) {
      return getContainerToBlobs().containsKey(container);
   }

   public static abstract class FutureBase<V> implements Future<V> {
      public boolean cancel(boolean b) {
         return false;
      }

      public boolean isCancelled() {
         return false;
      }

      public boolean isDone() {
         return true;
      }

      public V get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException,
               TimeoutException {
         return get();
      }
   }

   public Future<? extends SortedSet<? extends ResourceMetadata>> list() {
      return new FutureBase<SortedSet<? extends ResourceMetadata>>() {

         public TreeSet<ResourceMetadata> get() throws InterruptedException, ExecutionException {
            return Sets.newTreeSet(Iterables.transform(getContainerToBlobs().keySet(),
                     new Function<String, ResourceMetadata>() {
                        public ResourceMetadata apply(String name) {
                           MutableResourceMetadata cmd = create();
                           cmd.setName(name);
                           cmd.setType(ResourceType.CONTAINER);
                           return cmd;
                        }

                     }));
         }

      };

   }

   protected MutableResourceMetadata create() {
      return new MutableResourceMetadataImpl();
   }

   public Future<Boolean> createContainer(final String name) {
      return new FutureBase<Boolean>() {
         public Boolean get() throws InterruptedException, ExecutionException {
            if (!getContainerToBlobs().containsKey(name)) {
               getContainerToBlobs().put(name, new ConcurrentHashMap<String, Blob>());
            }
            return getContainerToBlobs().containsKey(name);
         }
      };
   }

   public String getFirstQueryOrNull(String string, @Nullable HttpRequestOptions options) {
      if (options == null)
         return null;
      Collection<String> values = options.buildQueryParameters().get(string);
      return (values != null && values.size() >= 1) ? values.iterator().next() : null;
   }

   protected class DelimiterFilter implements Predicate<ResourceMetadata> {
      private final String prefix;
      private final String delimiter;

      public DelimiterFilter(String prefix, String delimiter) {
         this.prefix = prefix;
         this.delimiter = delimiter;
      }

      public boolean apply(ResourceMetadata metadata) {
         if (prefix == null)
            return metadata.getName().indexOf(delimiter) == -1;
         if (metadata.getName().startsWith(prefix))
            return metadata.getName().replaceFirst(prefix, "").indexOf(delimiter) == -1;
         return false;
      }
   }

   protected class CommonPrefixes implements Function<ResourceMetadata, String> {
      private final String prefix;
      private final String delimiter;
      public static final String NO_PREFIX = "NO_PREFIX";

      public CommonPrefixes(String prefix, String delimiter) {
         this.prefix = prefix;
         this.delimiter = delimiter;
      }

      public String apply(ResourceMetadata metadata) {
         String working = metadata.getName();

         if (prefix != null) {
            if (working.startsWith(prefix)) {
               working = working.replaceFirst(prefix, "");
            }
         }
         if (working.contains(delimiter)) {
            return working.substring(0, working.indexOf(delimiter));
         }
         return NO_PREFIX;
      }
   }

   public static <T extends Comparable<?>> SortedSet<T> firstSliceOfSize(Iterable<T> elements,
            int size) {
      List<List<T>> slices = Lists.partition(Lists.newArrayList(elements), size);
      return Sets.newTreeSet(slices.get(0));
   }

   public void throwResponseException(int code) throws ExecutionException {
      HttpResponse response = null;
      response = new HttpResponse(); // TODO: Get real object URL?
      response.setStatusCode(code);
      throw new ExecutionException(new HttpResponseException(new HttpCommand() {

         public int getRedirectCount() {
            return 0;
         }

         public int incrementRedirectCount() {
            return 0;
         }

         public boolean isReplayable() {
            return false;
         }

         public HttpRequest setHostAndPort(String host, int port) {
            return null;
         }

         public HttpRequest setMethod(String method) {
            return null;
         }

         public Exception getException() {
            return null;
         }

         public int getFailureCount() {
            return 0;
         }

         public HttpRequest getRequest() {
            return new HttpRequest("GET", URI.create("http://stub"));
         }

         public int incrementFailureCount() {
            return 0;
         }

         public void setException(Exception exception) {

         }
      }, response));
   }

   public Future<String> putBlob(final String bucketName, final Blob object) {
      Map<String, Blob> container = getContainerToBlobs().get(bucketName);
      if (container == null) {
         new RuntimeException("bucketName not found: " + bucketName);
      }
      try {
         MutableBlobMetadata newMd = copy(object.getMetadata());
         newMd.setLastModified(new DateTime());
         byte[] data = toByteArray(object.getData());
         final byte[] md5 = HttpUtils.md5(data);
         final String eTag = HttpUtils.toHexString(md5);
         newMd.setETag(eTag);
         newMd.setContentMD5(md5);
         newMd.setContentType(object.getMetadata().getContentType());

         Blob blob = blobProvider.create(newMd);
         blob.setData(data);
         container.put(blob.getMetadata().getName(), blob);

         // Set HTTP headers to match metadata
         blob.getAllHeaders().put(HttpHeaders.LAST_MODIFIED,
                  dateService.rfc822DateFormat(newMd.getLastModified()));
         blob.getAllHeaders().put(HttpHeaders.ETAG, eTag);
         blob.getAllHeaders().put(HttpHeaders.CONTENT_TYPE, newMd.getContentType());
         blob.getAllHeaders().put(HttpHeaders.CONTENT_LENGTH, newMd.getSize() + "");
         for (Entry<String, String> userMD : newMd.getUserMetadata().entrySet()) {
            blob.getAllHeaders().put(userMD.getKey(), userMD.getValue());
         }

         return new FutureBase<String>() {
            public String get() throws InterruptedException, ExecutionException {
               return eTag;
            }
         };
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

   }

   public Future<? extends Blob> getBlob(final String bucketName, final String key,
            GetOptions... optionsList) {
      final GetOptions options = (optionsList.length == 0) ? new GetOptions() : optionsList[0];
      return new FutureBase<Blob>() {
         public Blob get() throws InterruptedException, ExecutionException {
            if (!getContainerToBlobs().containsKey(bucketName))
               throw new ContainerNotFoundException(bucketName);
            Map<String, Blob> realContents = getContainerToBlobs().get(bucketName);
            if (!realContents.containsKey(key))
               throw new KeyNotFoundException(bucketName, key);

            Blob object = realContents.get(key);

            if (options.getIfMatch() != null) {
               if (!object.getMetadata().getETag().equals(options.getIfMatch()))
                  throwResponseException(412);
            }
            if (options.getIfNoneMatch() != null) {
               if (object.getMetadata().getETag().equals(options.getIfNoneMatch()))
                  throwResponseException(304);
            }
            if (options.getIfModifiedSince() != null) {
               DateTime modifiedSince = options.getIfModifiedSince();
               if (object.getMetadata().getLastModified().isBefore(modifiedSince))
                  throw new ExecutionException(new RuntimeException(String.format(
                           "%1$s is before %2$s", object.getMetadata().getLastModified(),
                           modifiedSince)));

            }
            if (options.getIfUnmodifiedSince() != null) {
               DateTime unmodifiedSince = options.getIfUnmodifiedSince();
               if (object.getMetadata().getLastModified().isAfter(unmodifiedSince))
                  throw new ExecutionException(new RuntimeException(String.format(
                           "%1$s is after %2$s", object.getMetadata().getLastModified(),
                           unmodifiedSince)));
            }
            Blob returnVal = copyBlob(object);

            if (options.getRanges() != null && options.getRanges().size() > 0) {
               byte[] data = (byte[]) returnVal.getData();
               ByteArrayOutputStream out = new ByteArrayOutputStream();
               for (String s : options.getRanges()) {
                  if (s.startsWith("-")) {
                     int length = Integer.parseInt(s);
                     out.write(data, data.length - length, length);
                  } else if (s.endsWith("-")) {
                     int offset = Integer.parseInt(s);
                     out.write(data, offset, data.length - offset);
                  } else if (s.contains("-")) {
                     String[] firstLast = s.split("\\-");
                     int offset = Integer.parseInt(firstLast[0]);
                     int last = Integer.parseInt(firstLast[1]);
                     int length = (last < data.length) ? last + 1 : data.length - offset;
                     out.write(data, offset, length);
                  } else {
                     throw new IllegalArgumentException("first and last were null!");
                  }

               }
               returnVal.setData(out.toByteArray());
               returnVal.setContentLength(out.size());
               returnVal.getMetadata().setSize(new Long(data.length));
            }
            returnVal.setData(new ByteArrayInputStream((byte[]) returnVal.getData()));
            return returnVal;
         }
      };
   }

   public BlobMetadata blobMetadata(String container, String key) {
      try {
         return copy(getBlob(container, key).get().getMetadata());
      } catch (Exception e) {
         Utils.<ContainerNotFoundException> rethrowIfRuntimeOrSameType(e);
         Utils.<KeyNotFoundException> rethrowIfRuntimeOrSameType(e);
         throw new RuntimeException(e);// TODO
      }
   }

   private Blob copyBlob(Blob object) {
      Blob returnVal = blobProvider.create(copy(object.getMetadata()));
      returnVal.setData(object.getData());
      return returnVal;
   }

   public ConcurrentMap<String, ConcurrentMap<String, Blob>> getContainerToBlobs() {
      return containerToBlobs;
   }

   public Future<Void> clearContainer(final String container) {
      return new FutureBase<Void>() {
         public Void get() throws InterruptedException, ExecutionException {
            getContainerToBlobs().get(container).clear();
            return null;
         }
      };
   }

   public Blob newBlob() {
      return blobProvider.create(null);
   }

}
