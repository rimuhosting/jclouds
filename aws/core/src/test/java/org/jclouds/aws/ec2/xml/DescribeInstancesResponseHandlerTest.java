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
package org.jclouds.aws.ec2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.SortedSet;

import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.util.DateService;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code DescribeInstancesResponseHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.DescribeInstancesResponseHandlerTest")
public class DescribeInstancesResponseHandlerTest extends BaseHandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testWhenRunning() throws UnknownHostException {

      InputStream is = getClass().getResourceAsStream("/ec2/describe_instances_running.xml");
      SortedSet<Reservation> contents = Sets.newTreeSet();

      contents.add(new Reservation(ImmutableSortedSet.of("adriancole.ec2ingress"),
               ImmutableSortedSet.of(new RunningInstance("0",
                        "ec2-174-129-81-68.compute-1.amazonaws.com", "ami-1fd73376", "i-0799056f",
                        InstanceState.RUNNING, InstanceType.M1_SMALL, InetAddress
                                 .getByName("174.129.81.68"), "aki-a71cf9ce", "adriancole.ec21",
                        dateService.iso8601DateParse("2009-11-09T03:00:34.000Z"), false,
                        "us-east-1c", null, "ip-10-243-42-70.ec2.internal", InetAddress
                                 .getByName("10.243.42.70"), ImmutableSet.<String> of(),
                        "ari-a51cf9cc", null, null, null)), "993194456877", null, "r-a3c508cb"));

      SortedSet<Reservation> result = factory.create(
               injector.getInstance(DescribeInstancesResponseHandler.class)).parse(is);

      assertEquals(result, contents);

   }

   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream("/ec2/describe_instances.xml");
      SortedSet<Reservation> contents = Sets.newTreeSet();

      contents.add(new Reservation(ImmutableSortedSet.of("default"), ImmutableSortedSet.of(
               new RunningInstance("23", "ec2-72-44-33-4.compute-1.amazonaws.com", "ami-6ea54007",
                        "i-28a64341", InstanceState.RUNNING, InstanceType.M1_LARGE,
                        (InetAddress) null, "aki-ba3adfd3", "example-key-name", dateService
                                 .iso8601DateParse("2007-08-07T11:54:42.000Z"), false,
                        "us-east-1b", null, "10-251-50-132.ec2.internal", null, ImmutableSet
                                 .of("774F4FF8"), "ari-badbad00", null, null, null),
               new RunningInstance("23", "ec2-72-44-33-6.compute-1.amazonaws.com", "ami-6ea54007",
                        "i-28a64435", InstanceState.RUNNING, InstanceType.M1_LARGE,
                        (InetAddress) null, "aki-ba3adfd3", "example-key-name", dateService
                                 .iso8601DateParse("2007-08-07T11:54:42.000Z"), false,
                        "us-east-1b", null, "10-251-50-134.ec2.internal", null, ImmutableSet
                                 .of("774F4FF8"), "ari-badbad00", null, null, null)),
               "UYY3TLBUXIEON5NQVUUX6OMPWBZIQNFM", null, "r-44a5402d"));

      SortedSet<Reservation> result = factory.create(
               injector.getInstance(DescribeInstancesResponseHandler.class)).parse(is);

      assertEquals(result, contents);
   }
}
