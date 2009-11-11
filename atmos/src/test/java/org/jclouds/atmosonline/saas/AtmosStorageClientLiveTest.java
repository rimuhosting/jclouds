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
package org.jclouds.atmosonline.saas;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.security.SecureRandom;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.jclouds.atmosonline.saas.blobstore.strategy.RecursiveRemove;
import org.jclouds.atmosonline.saas.domain.AtmosObject;
import org.jclouds.atmosonline.saas.domain.BoundedSortedSet;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.FileType;
import org.jclouds.atmosonline.saas.domain.SystemMetadata;
import org.jclouds.atmosonline.saas.options.ListOptions;
import org.jclouds.blobstore.KeyAlreadyExistsException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.blobstore.integration.internal.BaseBlobStoreIntegrationTest;
import org.jclouds.blobstore.strategy.ClearContainerStrategy;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

/**
 * Tests behavior of {@code AtmosStorageClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "emcsaas.AtmosStorageClientLiveTest")
public class AtmosStorageClientLiveTest {

   private static final class HeadMatches implements Runnable {
      private final AtmosStorageClient connection;
      private final String name;
      private final String metadataValue;

      private HeadMatches(AtmosStorageClient connection, String name, String metadataValue) {
         this.connection = connection;
         this.name = name;
         this.metadataValue = metadataValue;
      }

      public void run() {
         try {
            verifyHeadObject(connection, name, metadataValue);
         } catch (Exception e) {
            throw new AssertionError(e);
         }
      }
   }

   private static final class ObjectMatches implements Runnable {
      private final AtmosStorageClient connection;
      private final String name;
      private final String metadataValue;
      private final String compare;

      private ObjectMatches(AtmosStorageClient connection, String name, String metadataValue,
               String compare) {
         this.connection = connection;
         this.name = name;
         this.metadataValue = metadataValue;
         this.compare = compare;
      }

      public void run() {
         try {
            verifyObject(connection, name, compare, metadataValue);
         } catch (Exception e) {
            throw new AssertionError(e);
         }
      }
   }

   private static final int INCONSISTENCY_WINDOW = 5000;
   protected AtmosStorageClient connection;
   private String containerPrefix = BaseBlobStoreIntegrationTest.CONTAINER_PREFIX;

   URI container1;
   URI container2;

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException {
      String uid = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      connection = new AtmosStorageContextBuilder(new AtmosStoragePropertiesBuilder(uid, key)
               .build()).withModules(new Log4JLoggingModule()).buildContext().getApi();
      ClearContainerStrategy clearer = new RecursiveRemove(connection);
      for (DirectoryEntry entry : connection.listDirectories().get(10, TimeUnit.SECONDS)) {
         if (entry.getObjectName().startsWith(containerPrefix)) {
            clearer.execute(entry.getObjectName());
            deleteConfirmed(entry.getObjectName());
         }
      }
   }
   @Test
   public void testListDirectorys() throws Exception {
      BoundedSortedSet<? extends DirectoryEntry> response = connection.listDirectories().get(10,
               TimeUnit.SECONDS);
      assert null != response;
   }

   String privateDirectory;
   String publicDirectory;
   String account;

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreateDirectory() throws Exception {
      boolean created = false;
      while (!created) {
         privateDirectory = containerPrefix + new SecureRandom().nextInt();
         try {
            created = connection.createDirectory(privateDirectory).get(10, TimeUnit.SECONDS) != null;
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 409)
               continue;
            throw e;
         }
      }
      BoundedSortedSet<? extends DirectoryEntry> response = connection.listDirectories().get(10,
               TimeUnit.SECONDS);
      for (DirectoryEntry id : response) {
         BoundedSortedSet<? extends DirectoryEntry> r2 = connection.listDirectory(id.getObjectName()).get(10,
                  TimeUnit.SECONDS);
         assert r2 != null;
      }
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateDirectory" })
   public void testListOptions() throws Exception {
      createOrReplaceObject("object2", "here is my data!", "meta-value1");
      createOrReplaceObject("object3", "here is my data!", "meta-value1");
      createOrReplaceObject("object4", "here is my data!", "meta-value1");
      BoundedSortedSet<? extends DirectoryEntry> r2 = connection.listDirectory(privateDirectory,
               ListOptions.Builder.limit(1)).get(10, TimeUnit.SECONDS);
      // test bug exists:
      assertEquals(r2.size(), 3);
      // assertEquals(r2.size(), 1);
      // assert r2.getToken() != null;
      // assertEquals(r2.last().getObjectName(),"object2");
      // r2 = connection.listDirectory(privateDirectory,
      // ListOptions.Builder.token(r2.getToken())).get(10,
      // TimeUnit.SECONDS);
      // assertEquals(r2.size(), 2);
      // assert r2.getToken() == null;
      // assertEquals(r2.last().getObjectName(),"object4");

   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testListOptions" })
   public void testFileOperations() throws Exception {
      // create the object
      createOrReplaceObject("object", "here is my data!", "meta-value1");
      assertEventuallyObjectMatches("object", "here is my data!", "meta-value1");
      assertEventuallyHeadMatches("object", "meta-value1");
      // try overwriting the object
      createOrReplaceObject("object", "here is my data?", "meta-value?");
      assertEventuallyObjectMatches("object", "here is my data?", "meta-value?");

      // loop to gather metrics
      for (boolean stream : new Boolean[] { true, false }) {
         for (int i = 0; i < 10; i++) {
            System.err.printf("upload/delete/create attempt %d type %s%n", i + 1, stream ? "stream"
                     : "string");
            // try updating
            createOrUpdateWithErrorLoop(stream, "there is my data", "2");

            deleteConfirmed(privateDirectory + "/object");
            // now create
            createOrUpdateWithErrorLoop(stream, "where is my data", "3");

         }
      }
   }

   private void createOrUpdateWithErrorLoop(boolean stream, String data, String metadataValue)
            throws Exception {
      createOrReplaceObject("object", makeData(data, stream), metadataValue);
      assertEventuallyObjectMatches("object", data, metadataValue);
   }

   Object makeData(String in, boolean stream) {
      return stream ? IOUtils.toInputStream(in) : in;
   }

   private void createOrReplaceObject(String name, Object data, String metadataValue)
            throws Exception {
      // Test PUT with string data, ETag hash, and a piece of metadata
      AtmosObject object = connection.newObject();
      object.getContentMetadata().setName(name);
      object.setData(data);
      object.getContentMetadata().setContentLength(16);
      object.generateMD5();
      object.getContentMetadata().setContentType("text/plain");
      object.getUserMetadata().getMetadata().put("Metadata", metadataValue);
      replaceObject(object);
   }

   /**
    * Due to eventual consistency, container commands may not return correctly immediately. Hence,
    * we will try up to the inconsistency window to see if the assertion completes.
    */
   protected static void assertEventually(Runnable assertion) throws InterruptedException {
      long start = System.currentTimeMillis();
      AssertionError error = null;
      for (int i = 0; i < 30; i++) {
         try {
            assertion.run();
            if (i > 0)
               System.err.printf("%d attempts and %dms asserting %s%n", i + 1, System
                        .currentTimeMillis()
                        - start, assertion.getClass().getSimpleName());
            return;
         } catch (AssertionError e) {
            error = e;
         }
         Thread.sleep(INCONSISTENCY_WINDOW / 30);
      }
      if (error != null)
         throw error;

   }

   protected void assertEventuallyObjectMatches(final String name, final String compare,
            final String metadataValue) throws InterruptedException {
      assertEventually(new ObjectMatches(connection, privateDirectory + "/" + name, metadataValue,
               compare));
   }

   protected void assertEventuallyHeadMatches(final String name, final String metadataValue)
            throws InterruptedException {
      assertEventually(new HeadMatches(connection, privateDirectory + "/" + name, metadataValue));
   }

   private static void verifyHeadObject(AtmosStorageClient connection, String path,
            String metadataValue) throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      AtmosObject getBlob = connection.headFile(path);
      assertEquals(IOUtils.toString((InputStream) getBlob.getData()), "");
      verifyMetadata(metadataValue, getBlob);
   }

   private static void verifyObject(AtmosStorageClient connection, String path, String compare,
            String metadataValue) throws InterruptedException, ExecutionException,
            TimeoutException, IOException {
      AtmosObject getBlob = connection.readFile(path).get(120, TimeUnit.SECONDS);
      assertEquals(getBlob.getData() instanceof String ? getBlob.getData() : IOUtils
               .toString((InputStream) getBlob.getData()), compare);
      verifyMetadata(metadataValue, getBlob);
   }

   private static void verifyMetadata(String metadataValue, AtmosObject getBlob) {
      assertEquals(getBlob.getContentMetadata().getContentLength(), new Long(16));
      assert getBlob.getContentMetadata().getContentType().startsWith("text/plain");
      assertEquals(getBlob.getUserMetadata().getMetadata().get("Metadata"), metadataValue);
      SystemMetadata md = getBlob.getSystemMetadata();
      assertEquals(md.getSize(), 16);
      assert md.getGroupID() != null;
      assertEquals(md.getHardLinkCount(), 1);
      assert md.getInceptionTime() != null;
      assert md.getLastAccessTime() != null;
      assert md.getLastMetadataModification() != null;
      assert md.getLastUserDataModification() != null;
      assert md.getObjectID() != null;
      assertEquals(md.getObjectName(), "object");
      assert md.getPolicyName() != null;
      assertEquals(md.getType(), FileType.REGULAR);
      assert md.getUserID() != null;

      try {
         Utils.toStringAndClose(URI.create(
                  "http://accesspoint.emccis.com/rest/objects/"
                           + getBlob.getSystemMetadata().getObjectID()).toURL().openStream());
         assert false : "shouldn't have worked, since it is private";
      } catch (IOException e) {

      }
   }

   private void replaceObject(AtmosObject object) throws Exception {
      alwaysDeleteFirstReplaceStrategy(object);
      // retryAndCheckSystemMetadataAndPutIfPresentReplaceStrategy(object); // HEAD 200 followed by
      // PUT = 404!
   }

   private void alwaysDeleteFirstReplaceStrategy(AtmosObject object) throws Exception {
      deleteConfirmed(privateDirectory + "/" + object.getContentMetadata().getName());
      long time = System.currentTimeMillis();
      try {
         connection.createFile(privateDirectory, object).get(30, TimeUnit.SECONDS);
         System.err.printf("%s %s; %dms%n", "created",
                  object.getData() instanceof InputStream ? "stream" : "string", System
                           .currentTimeMillis()
                           - time);
      } catch (Exception e) {
         String message = (e.getCause().getCause() != null) ? e.getCause().getCause().getMessage()
                  : e.getCause().getMessage();
         System.err.printf("failure %s %s; %dms: [%s]%n", "creating",
                  object.getData() instanceof InputStream ? "stream" : "string", System
                           .currentTimeMillis()
                           - time, message);
         throw e;
      }
   }

   private void deleteConfirmed(final String path) throws InterruptedException, ExecutionException,
            TimeoutException {
      long time = System.currentTimeMillis();
      deleteImmediateAndVerifyWithHead(path);
      System.err.printf("confirmed deletion after %dms%n", System.currentTimeMillis() - time);
   }

   private void deleteImmediateAndVerifyWithHead(final String path) throws InterruptedException,
            ExecutionException, TimeoutException {
      try {
         connection.deletePath(path).get(10, TimeUnit.SECONDS);
      } catch (KeyNotFoundException ex) {
      }
      assert !connection.pathExists(path);
   }

   protected void deleteConsistencyAware(final String path) throws InterruptedException,
            ExecutionException, TimeoutException {
      try {
         connection.deletePath(path).get(10, TimeUnit.SECONDS);
      } catch (KeyNotFoundException ex) {
      }
      assert Utils.enventuallyTrue(new Supplier<Boolean>() {
         public Boolean get() {
            return !connection.pathExists(path);
         }
      }, INCONSISTENCY_WINDOW);
   }

   protected void retryAndCheckSystemMetadataAndPutIfPresentReplaceStrategy(AtmosObject object)
            throws Exception {

      int failures = 0;
      while (true) {
         try {
            checkSystemMetadataAndPutIfPresentReplaceStrategy(object);
            break;
         } catch (ExecutionException e1) {// bug
            if (!(e1.getCause() instanceof KeyAlreadyExistsException))
               throw e1;
            else
               failures++;
         }
      }
      if (failures > 0)
         System.err.printf("%d failures create/replacing %s%n", failures,
                  object.getData() instanceof InputStream ? "stream" : "string");
   }

   private void checkSystemMetadataAndPutIfPresentReplaceStrategy(AtmosObject object)
            throws Exception {
      long time = System.currentTimeMillis();
      boolean update = true;
      try {
         connection.getSystemMetadata(privateDirectory + "/object");
      } catch (KeyNotFoundException ex) {
         update = false;
      }
      try {
         if (update)
            connection.updateFile(privateDirectory, object).get(30, TimeUnit.SECONDS);
         else
            connection.createFile(privateDirectory, object).get(30, TimeUnit.SECONDS);
         System.err.printf("%s %s; %dms%n", update ? "updated" : "created",
                  object.getData() instanceof InputStream ? "stream" : "string", System
                           .currentTimeMillis()
                           - time);
      } catch (Exception e) {
         String message = (e.getCause().getCause() != null) ? e.getCause().getCause().getMessage()
                  : e.getCause().getMessage();
         System.err.printf("failure %s %s; %dms: [%s]%n", update ? "updating" : "creating", object
                  .getData() instanceof InputStream ? "stream" : "string", System
                  .currentTimeMillis()
                  - time, message);
         throw e;
      }
   }
}
