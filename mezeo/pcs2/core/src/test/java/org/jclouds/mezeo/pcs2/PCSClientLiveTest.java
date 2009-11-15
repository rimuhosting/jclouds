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
package org.jclouds.mezeo.pcs2;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.HttpUtils.calculateSize;
import static org.jclouds.mezeo.pcs2.options.PutBlockOptions.Builder.range;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.domain.ResourceType;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.mezeo.pcs2.domain.ContainerList;
import org.jclouds.mezeo.pcs2.domain.FileInfoWithMetadata;
import org.jclouds.mezeo.pcs2.domain.PCSFile;
import org.jclouds.mezeo.pcs2.domain.ResourceInfo;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;

/**
 * Tests behavior of {@code PCSDiscovery}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "pcs2.PCSClientLiveTest")
public class PCSClientLiveTest {

   private PCSClient connection;

   private String user;

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException {
      user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      URI endpoint = URI.create(checkNotNull(System.getProperty("jclouds.test.endpoint"),
               "jclouds.test.endpoint"));

      connection = PCSContextFactory.createContext(endpoint, user, password,
               new Log4JLoggingModule()).getApi();
      ContainerList response = connection.list();
      for (ResourceInfo resource : response) {
         if (resource.getType() == ResourceType.FOLDER
                  && resource.getName().startsWith(containerPrefix)) {
            System.err.printf("*** deleting container %s...%n", resource.getName());
            connection.deleteContainer(resource.getUrl());
         }
      }

   }

   private String containerPrefix = BaseBlobStoreIntegrationTest.CONTAINER_PREFIX;

   @Test
   public void testListContainers() throws Exception {
      ContainerList response = connection.list();
      URI rootUrl = response.getUrl();
      String name = "/";
      validateContainerList(response, rootUrl, name);

      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);

      // Create test containers
      for (String container : new String[] { containerPrefix + ".testListOwnedContainers1",
               containerPrefix + ".testListOwnedContainers2" }) {
         URI containerURI = connection.createContainer(container);
         connection.putMetadataItem(containerURI, "name", container);
         response = connection.list(containerURI);
         validateContainerList(response, rootUrl, container);

         assertEquals(response.getMetadataItems().get("name"), URI.create(containerURI
                  .toASCIIString()
                  + "/metadata/name"));

         validateMetadataItemNameEquals(containerURI, container);

         connection.deleteContainer(containerURI);
      }
   }

   private void validateMetadataItemNameEquals(URI resource, String name)
            throws InterruptedException, ExecutionException, TimeoutException {
      Map<String, String> metadata = Maps.newHashMap();
      connection.addMetadataItemToMap(resource, "name", metadata);
      assertEquals(metadata.get("name"), name);
   }

   private void validateContainerList(ContainerList response, URI parent, String name) {
      assertNotNull(response.getMetadataItems());
      validateResource(response, parent, name);
   }

   private void validateFileInfo(FileInfoWithMetadata response, URI parent, String name, Long size,
            String mimeType) {
      assertNotNull(response.getMetadataItems());
      assertFalse(response.isPublic());
      assertEquals(response.getBytes(), size);
      assertEquals(response.getMimeType(), mimeType);
      assertNotNull(response.getContent());
      assertNotNull(response.getPermissions());
      assertNotNull(response.getThumbnail());
      validateResource(response, parent, name);
   }

   private void validateResource(ResourceInfo response, URI parent, String name) {
      assertNotNull(response);
      assertNotNull(response.getAccessed());
      assertNotNull(response.getBytes());
      assertNotNull(response.getCreated());
      assertNotNull(response.getMetadata());
      assertNotNull(response.getModified());
      assertEquals(response.getName(), name);
      assertEquals(response.getOwner(), user);
      assertEquals(response.getParent(), parent);
      assertNotNull(response.getTags());
      assertNotNull(response.getType());
      assertNotNull(response.getUrl());
      assertNotNull(response.getVersion());
   }

   @Test
   public void testObjectOperations() throws Exception {
      String containerName = containerPrefix + ".testObjectOperations";
      String data = "Here is my data";

      URI container = connection.createContainer(containerName);

      // Test PUT with string data, ETag hash, and a piece of metadata
      PCSFile object = connection.newFile();
      object.getMetadata().setName("object");
      object.getMetadata().setMimeType("text/plain");
      object.setData(data);
      object.setContentLength(data.length());
      URI objectURI = connection.uploadFile(container, object);
      connection.putMetadataItem(objectURI, "name", "object");

      try {
         connection.downloadFile(UriBuilder.fromUri(objectURI).path("sad").build());
         assert false;
      } catch (KeyNotFoundException e) {
      }
      // Test GET of object (including updated metadata)
      InputStream file = connection.downloadFile(objectURI);
      assertEquals(IOUtils.toString(file), data);
      validateFileInfoAndNameIsInMetadata(container, objectURI, "object", new Long(data.length()));

      try {
         connection.uploadFile(container, object);
      } catch (Throwable e) {
         assertEquals(e.getCause().getClass(), HttpResponseException.class);
         assertEquals(((HttpResponseException) e.getCause()).getResponse().getStatusCode(), 422);
      }

      connection.deleteFile(objectURI);
      try {
         connection.getFileInfo(objectURI);
      } catch (Throwable e) {
         assertEquals(e.getClass(), KeyNotFoundException.class);
      }

      String name = "sad";
      // try sending it in 2 parts
      object.getMetadata().setName(name);
      objectURI = connection.createFile(container, object);
      validateFileInfoAndNameIsInMetadata(container, objectURI, name, new Long(0));

      object.setData(data.substring(0, 2));
      object.setContentLength(calculateSize(object.getData()));
      connection.uploadBlock(objectURI, object, range(0, 2));
      validateFileInfoAndNameIsInMetadata(container, objectURI, name, new Long(2));

      object.setData(data.substring(2));
      object.setContentLength(calculateSize(object.getData()));
      connection.uploadBlock(objectURI, object, range(2, data.getBytes().length));
      validateFileInfoAndNameIsInMetadata(container, objectURI, name, new Long(data.length()));

      file = connection.downloadFile(objectURI);
      assertEquals(IOUtils.toString(file), data);

      // change data in an existing file
      data = "Here is my datum";
      object.setData(data.substring(2));
      object.setContentLength(calculateSize(object.getData()));
      connection.uploadBlock(objectURI, object, range(2, data.getBytes().length));
      validateFileInfoAndNameIsInMetadata(container, objectURI, name, new Long(data.length()));

      file = connection.downloadFile(objectURI);
      assertEquals(IOUtils.toString(file), data);

      connection.deleteFile(objectURI);
      connection.deleteContainer(container);
   }

   private FileInfoWithMetadata validateFileInfoAndNameIsInMetadata(URI container, URI objectURI,
            String name, Long size) throws InterruptedException, ExecutionException,
            TimeoutException {
      FileInfoWithMetadata response;
      connection.putMetadataItem(objectURI, "name", name);

      response = connection.getFileInfo(objectURI);
      validateFileInfo(response, container, name, size, "text/plain");

      assertEquals(response.getMetadataItems().get("name"), URI.create(objectURI.toASCIIString()
               + "/metadata/name"));

      validateMetadataItemNameEquals(objectURI, name);
      return response;
   }

}
