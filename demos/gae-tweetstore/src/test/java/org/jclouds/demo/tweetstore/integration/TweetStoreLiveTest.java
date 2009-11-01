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
package org.jclouds.demo.tweetstore.integration;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_ACCESSKEYID;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_SECRETACCESSKEY;
import static org.jclouds.azure.storage.reference.AzureStorageConstants.PROPERTY_AZURESTORAGE_ACCOUNT;
import static org.jclouds.azure.storage.reference.AzureStorageConstants.PROPERTY_AZURESTORAGE_KEY;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_BLOBSTORE_CONTEXTBUILDERS;
import static org.jclouds.demo.tweetstore.reference.TweetStoreConstants.PROPERTY_TWEETSTORE_CONTAINER;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_KEY;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_USER;
import static org.jclouds.twitter.reference.TwitterConstants.PROPERTY_TWITTER_PASSWORD;
import static org.jclouds.twitter.reference.TwitterConstants.PROPERTY_TWITTER_USER;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.jclouds.aws.s3.S3PropertiesBuilder;
import org.jclouds.aws.s3.blobstore.S3BlobStoreContextBuilder;
import org.jclouds.aws.s3.blobstore.S3BlobStoreContextFactory;
import org.jclouds.azure.storage.blob.AzureBlobPropertiesBuilder;
import org.jclouds.azure.storage.blob.blobstore.AzureBlobStoreContextBuilder;
import org.jclouds.azure.storage.blob.blobstore.AzureBlobStoreContextFactory;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.rackspace.cloudfiles.CloudFilesPropertiesBuilder;
import org.jclouds.rackspace.cloudfiles.blobstore.CloudFilesBlobStoreContextBuilder;
import org.jclouds.rackspace.cloudfiles.blobstore.CloudFilesBlobStoreContextFactory;
import org.jclouds.twitter.TwitterPropertiesBuilder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Starts up the Google App Engine for Java Development environment and deploys an application which
 * tests accesses twitter and blobstores.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "functionalTests")
public class TweetStoreLiveTest {

   GoogleDevServer server;
   private URL url;
   private ImmutableSet<BlobStoreContext<? extends Object>> contexts;
   private String container;

   @BeforeTest
   @Parameters( { "warfile", "devappserver.address", "devappserver.port" })
   public void startDevAppServer(final String warfile, final String address, final String port)
            throws Exception {
      url = new URL(String.format("http://%s:%s", address, port));
      Properties props = new Properties();
      props.setProperty(PROPERTY_TWEETSTORE_CONTAINER, checkNotNull(System
               .getProperty(PROPERTY_TWEETSTORE_CONTAINER)));
      props.setProperty(PROPERTY_BLOBSTORE_CONTEXTBUILDERS, String.format("%s,%s,%s",
               S3BlobStoreContextBuilder.class.getName(), CloudFilesBlobStoreContextBuilder.class
                        .getName(), AzureBlobStoreContextBuilder.class.getName()));

      props = new TwitterPropertiesBuilder(props).withCredentials(
               checkNotNull(System.getProperty(PROPERTY_TWITTER_USER), PROPERTY_TWITTER_USER),
               System.getProperty(PROPERTY_TWITTER_PASSWORD, PROPERTY_TWITTER_PASSWORD)).build();

      props = new S3PropertiesBuilder(props)
               .withCredentials(
                        checkNotNull(System.getProperty(PROPERTY_AWS_ACCESSKEYID),
                                 PROPERTY_AWS_ACCESSKEYID),
                        System.getProperty(PROPERTY_AWS_SECRETACCESSKEY,
                                 PROPERTY_AWS_SECRETACCESSKEY)).build();

      props = new CloudFilesPropertiesBuilder(props).withCredentials(
               checkNotNull(System.getProperty(PROPERTY_RACKSPACE_USER), PROPERTY_RACKSPACE_USER),
               System.getProperty(PROPERTY_RACKSPACE_KEY, PROPERTY_RACKSPACE_KEY)).build();

      props = new AzureBlobPropertiesBuilder(props).withCredentials(
               checkNotNull(System.getProperty(PROPERTY_AZURESTORAGE_ACCOUNT),
                        PROPERTY_AZURESTORAGE_ACCOUNT),
               System.getProperty(PROPERTY_AZURESTORAGE_KEY, PROPERTY_AZURESTORAGE_KEY)).build();

      server = new GoogleDevServer();
      server.writePropertiesAndStartServer(address, port, warfile, props);
   }

   @BeforeClass
   void clearAndCreateContainers() throws InterruptedException, ExecutionException,
            TimeoutException {
      container = checkNotNull(System.getProperty(PROPERTY_TWEETSTORE_CONTAINER));
      BlobStoreContext<?> s3Context = S3BlobStoreContextFactory.createContext(checkNotNull(System
               .getProperty(PROPERTY_AWS_ACCESSKEYID), PROPERTY_AWS_ACCESSKEYID), System
               .getProperty(PROPERTY_AWS_SECRETACCESSKEY, PROPERTY_AWS_SECRETACCESSKEY));

      BlobStoreContext<?> cfContext = CloudFilesBlobStoreContextFactory.createContext(checkNotNull(
               System.getProperty(PROPERTY_RACKSPACE_USER), PROPERTY_RACKSPACE_USER), System
               .getProperty(PROPERTY_RACKSPACE_KEY, PROPERTY_RACKSPACE_KEY));

      BlobStoreContext<?> azContext = AzureBlobStoreContextFactory.createContext(checkNotNull(
               System.getProperty(PROPERTY_AZURESTORAGE_ACCOUNT), PROPERTY_AZURESTORAGE_ACCOUNT),
               System.getProperty(PROPERTY_AZURESTORAGE_KEY, PROPERTY_AZURESTORAGE_KEY));
      this.contexts = ImmutableSet.of(s3Context, cfContext, azContext);
      boolean deleted = false;
      for (BlobStoreContext<?> context : contexts) {
         if (context.getBlobStore().exists(container)) {
            System.err.printf("deleting container %s at %s%n", container, context.getEndPoint());
            context.getBlobStore().deleteContainer(container).get(30, TimeUnit.SECONDS);
            deleted = true;
         }
      }
      if (deleted) {
         System.err.println("sleeping 30 seconds to allow containers to clear");
         Thread.sleep(30000);
      }
      for (BlobStoreContext<?> context : contexts) {
         System.err.printf("creating container %s at %s%n", container, context.getEndPoint());
         context.getBlobStore().createContainer(container).get(30, TimeUnit.SECONDS);
      }
   }

   @Test
   public void shouldPass() throws InterruptedException, IOException {
      InputStream i = url.openStream();
      String string = IOUtils.toString(i);
      assert string.indexOf("Welcome") >= 0 : string;
   }

   @Test(dependsOnMethods = "shouldPass", expectedExceptions = IOException.class)
   public void shouldFail() throws InterruptedException, IOException {
      new URL(url, "/cron/do").openStream();
   }

   @Test(dependsOnMethods = "shouldFail")
   public void testPrimeContainers() throws IOException {
      URL gurl = new URL(url, "/cron/do");
      HttpURLConnection connection = (HttpURLConnection) gurl.openConnection();
      connection.addRequestProperty("X-AppEngine-Cron", "true");
      InputStream i = connection.getInputStream();
      String string = IOUtils.toString(i);
      assert string.indexOf("Done!") >= 0 : string;
      for (BlobStoreContext<?> context : contexts) {
         assert context.createInputStreamMap(container).size() > 0 : context.getEndPoint();
      }
   }

   @Test(invocationCount = 5, dependsOnMethods = "testPrimeContainers")
   public void testSerial() throws InterruptedException, IOException {
      URL gurl = new URL(url, "/tweets/get");
      InputStream i = gurl.openStream();
      String string = IOUtils.toString(i);
      assert string.indexOf("Tweets in Clouds") >= 0 : string;
   }

   @Test(invocationCount = 10, dependsOnMethods = "testPrimeContainers", threadPoolSize = 3)
   public void testParallel() throws InterruptedException, IOException {
      URL gurl = new URL(url, "/tweets/get");
      InputStream i = gurl.openStream();
      String string = IOUtils.toString(i);
      assert string.indexOf("Tweets in Clouds") >= 0 : string;
   }
}
