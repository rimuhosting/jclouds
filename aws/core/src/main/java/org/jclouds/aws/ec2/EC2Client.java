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
package org.jclouds.aws.ec2;

import static org.jclouds.aws.ec2.reference.EC2Parameters.ACTION;
import static org.jclouds.aws.ec2.reference.EC2Parameters.VERSION;

import java.util.SortedSet;
import java.util.concurrent.Future;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.binders.BindGroupNameToIndexedFormParams;
import org.jclouds.aws.ec2.binders.BindInstanceIdsToIndexedFormParams;
import org.jclouds.aws.ec2.binders.BindKeyNameToIndexedFormParams;
import org.jclouds.aws.ec2.binders.BindUserIdGroupPairToSourceSecurityGroupFormParams;
import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.domain.ImageAttribute;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.SecurityGroup;
import org.jclouds.aws.ec2.domain.TerminatedInstance;
import org.jclouds.aws.ec2.domain.UserIdGroupPair;
import org.jclouds.aws.ec2.filters.FormSigner;
import org.jclouds.aws.ec2.functions.ReturnVoidOnGroupNotFound;
import org.jclouds.aws.ec2.options.DescribeImagesOptions;
import org.jclouds.aws.ec2.options.RunInstancesOptions;
import org.jclouds.aws.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeInstancesResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeKeyPairsResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeSecurityGroupsResponseHandler;
import org.jclouds.aws.ec2.xml.KeyPairResponseHandler;
import org.jclouds.aws.ec2.xml.RunInstancesResponseHandler;
import org.jclouds.aws.ec2.xml.TerminateInstancesResponseHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to EC2 via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Endpoint(EC2.class)
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = "2009-08-15")
@VirtualHost
public interface EC2Client {

   /**
    * Returns information about AMIs, AKIs, and ARIs. This includes image type, product codes,
    * architecture, and kernel and RAM disk IDs. Images available to you include p ublic images,
    * private images that you own, and private images owned by other users for which you have
    * explicit launch permissions.
    * 
    * @see #describeInstances
    * @see #describeImageAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImages.html"
    *      />
    * @see DescribeImagesOptions
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeImages")
   @XMLResponseParser(DescribeImagesResponseHandler.class)
   Future<? extends SortedSet<Image>> describeImages(DescribeImagesOptions... options);

   /**
    * Returns information about an attribute of an AMI. Only one attribute can be specified per
    * call.
    * 
    * @param imageId
    *           The ID of the AMI for which an attribute will be described
    * @param attribute
    *           the attribute to describe
    * @see #describeImages
    * @see #modifyImageAttribute
    * @see #resetImageAttribute
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeImageAttribute.html"
    *      />
    * @see DescribeImagesOptions
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeImageAttribute")
   String describeImageAttribute(@FormParam("ImageId") String imageId,
            @FormParam("Attribute") ImageAttribute attribute);

   /**
    * Returns information about instances that you own.
    * <p/>
    * 
    * If you specify one or more instance IDs, Amazon EC2 returns information for those instances.
    * If you do not specify instance IDs, Amazon EC2 returns information for all relevant instances.
    * If you specify an invalid instance ID, a fault is returned. If you specify an instance that
    * you do not own, it will not be included in the returned results.
    * <p/>
    * Recently terminated instances might appear in the returned results.This interval is usually
    * less than one hour.
    * 
    * @see #runInstances
    * @see #terminateInstances
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeInstances.html"
    *      />
    * @see DescribeInstancesOptions
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeInstances")
   @XMLResponseParser(DescribeInstancesResponseHandler.class)
   Future<? extends SortedSet<Reservation>> describeInstances(
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * Launches a specified number of instances of an AMI for which you have permissions.
    * <p/>
    * 
    * If Amazon EC2 cannot launch the minimum number AMIs you request, no instances will be
    * launched. If there is insufficient capacity to launch the maximum number of AMIs you request,
    * Amazon EC2 launches the minimum number specified for each AMI and allocate the remaining
    * available instances using round robin.
    * <p/>
    * <h4>Security Groups</h4>
    * <b>Note:</b> Every instance is launched in a security group (created using the
    * CreateSecurityGroup operation.
    * <h4>Key Pair</h4>
    * You can provide an optional key pair ID for each image in the launch request (created using
    * the CreateKeyPair operation). All instances that are created from images that use this key
    * pair will have access to the associated public key at boot. You can use this key to provide
    * secure access to an instance of an image on a per-instance basis. Amazon EC2 public images use
    * this feature to provide secure access without passwords.
    * <p/>
    * <b>Note:</b> Launching public images without a key pair ID will leave them inaccessible.
    * <p/>
    * The public key material is made available to the instance at boot time by placing it in the
    * openssh_id.pub file on a logical device that is exposed to the instance as /dev/sda2 (the
    * instance store). The format of this file is suitable for use as an entry within
    * ~/.ssh/authorized_keys (the OpenSSH format). This can be done at boot (e.g., as part of
    * rc.local) allowing for secure access without passwords.
    * <h4>User Data</h4>
    * Optional user data can be provided in the launch request. All instances that collectively
    * comprise the launch request have access to this data. For more information, go the Amazon
    * Elastic Compute Cloud Developer Guide.
    * <h4>Product Codes</h4>
    * 
    * <b>Note:</b> If any of the AMIs have a product code attached for which the user has not
    * subscribed, the RunInstances call will fail.
    * <h4>Kernel</h4>
    * 
    * <b>Important:</b> We strongly recommend using the 2.6.18 Xen stock kernel with High-CPU and
    * High-Memory instances. Although the default Amazon EC2 kernels will work, the new kernels
    * provide greater stability and performance for these instance types. For more information about
    * kernels, go the Amazon Elastic Compute Cloud Developer Guide.
    * 
    * @param imageId
    *           Unique ID of a machine image, returned by a call to
    * @param minCount
    *           Minimum number of instances to launch. If the value is more than Amazon EC2 can
    *           launch, no instances a re launched at all. Constraints: Between 1 and the maximum
    *           number allowed for your account (default: 20).
    * @param maxCount
    *           Maximum number of instances to launch. If the value is more than Amazon EC2 can
    *           launch, the largest possible number above minCount will be launched instead.
    *           Constraints: Between 1 and the maximum number allowed for your account (default:
    *           20).
    * @see #describeInstances
    * @see #terminateInstances
    * @see #authorizeSecurityGroupIngress
    * @see #revokeSecurityGroupIngress
    * @see #describeSecurityGroups
    * @see #createSecurityGroup
    * @see #createKeyPair
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-RunInstances.html"
    *      />
    * @see RunInstancesOptions
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RunInstances")
   @XMLResponseParser(RunInstancesResponseHandler.class)
   Future<Reservation> runInstances(@FormParam("ImageId") String imageId,
            @FormParam("MinCount") int minCount, @FormParam("MaxCount") int maxCount,
            RunInstancesOptions... options);

   /**
    * Shuts down one or more instances. This operation is idempotent; if you terminate an instance
    * more than once, each call will succeed.
    * <p/>
    * Terminated instances will remain visible after termination (approximately one hour).
    * 
    * @param instanceIds
    *           Instance ID to terminate.
    * @see #describeInstances
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-TerminateInstances.html"
    *      />
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "TerminateInstances")
   @XMLResponseParser(TerminateInstancesResponseHandler.class)
   Future<? extends SortedSet<TerminatedInstance>> terminateInstances(
            @FormParam("InstanceId.0") String instanceId,
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * Creates a new 2048-bit RSA key pair with the specified name. The public key is stored by
    * Amazon EC2 and the private key is displayed on the console. The private key is returned as an
    * unencrypted PEM encoded PKCS#8 private key. If a key with the specified name already exists,
    * Amazon EC2 returns an error.
    * 
    * @see #runInstances
    * @see #describeKeyPairs
    * @see #deleteKeyPair
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateKeyPair.html"
    *      />
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateKeyPair")
   @XMLResponseParser(KeyPairResponseHandler.class)
   Future<KeyPair> createKeyPair(@FormParam("KeyName") String keyName);

   /**
    * Returns information about key pairs available to you. If you specify key pairs, information
    * about those key pairs is returned. Otherwise, information for all registered key pairs is
    * returned.
    * 
    * @param keyPairNames
    *           Key pairs to describe.
    * @see #runInstances
    * @see #describeAvailabilityZones
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeKeyPairs.html"
    *      />
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeKeyPairs")
   @XMLResponseParser(DescribeKeyPairsResponseHandler.class)
   Future<? extends SortedSet<KeyPair>> describeKeyPairs(
            @BinderParam(BindKeyNameToIndexedFormParams.class) String... keyPairNames);

   /**
    * Deletes the specified key pair, by removing the public key from Amazon EC2. You must own the
    * key pair
    * 
    * @see #describeKeyPairs
    * @see #createKeyPair
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DeleteKeyPair.html"
    *      />
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteKeyPair")
   Future<Void> deleteKeyPair(@FormParam("KeyName") String keyName);

   /**
    * Creates a new security group. Group names must be unique per account.
    * 
    * @param name
    *           Name of the security group. Accepts alphanumeric characters, spaces, dashes, and
    *           underscores.
    * @param description
    *           Description of the group. This is informational only. If the description contains
    *           spaces, you must enc lose it in single quotes (') or URL-encode it. Accepts
    *           alphanumeric characters, spaces, dashes, and underscores.
    * @see #runInstances
    * @see #describeSecurityGroups
    * @see #authorizeSecurityGroupIngress
    * @see #revokeSecurityGroupIngress
    * @see #deleteSecurityGroup
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateSecurityGroup.html"
    *      />
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateSecurityGroup")
   Future<Void> createSecurityGroup(@FormParam("GroupName") String name,
            @FormParam("GroupDescription") String description);

   /**
    * Deletes a security group that you own.
    * 
    * @param name
    *           Name of the security group to delete.
    * 
    * @see #describeSecurityGroups
    * @see #authorizeSecurityGroupIngress
    * @see #revokeSecurityGroupIngress
    * @see #createSecurityGroup
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DeleteSecurityGroup.html"
    *      />
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteSecurityGroup")
   @ExceptionParser(ReturnVoidOnGroupNotFound.class)
   Future<Void> deleteSecurityGroup(@FormParam("GroupName") String name);

   /**
    * Returns information about security groups that you own.
    * 
    * @param securityGroupNames
    *           Name of the security groups
    * 
    * @see #createSecurityGroup
    * @see #authorizeSecurityGroupIngress
    * @see #revokeSecurityGroupIngress
    * @see #deleteSecurityGroup
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeSecurityGroups.html"
    *      />
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeSecurityGroups")
   @XMLResponseParser(DescribeSecurityGroupsResponseHandler.class)
   Future<? extends SortedSet<SecurityGroup>> describeSecurityGroups(
            @BinderParam(BindGroupNameToIndexedFormParams.class) String... securityGroupNames);

   /**
    * 
    * Adds permissions to a security group based on another group.
    * 
    * @param groupName
    *           Name of the group to modify. The name must be valid and belong to the account
    * @param sourceSecurityGroup
    *           group to associate with this group.
    * 
    * @see #createSecurityGroup
    * @see #describeSecurityGroups
    * @see #revokeSecurityGroupIngress
    * @see #deleteSecurityGroup
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-AuthorizeSecurityGroupIngress.html"
    * 
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AuthorizeSecurityGroupIngress")
   Future<Void> authorizeSecurityGroupIngress(
            @FormParam("GroupName") String groupName,
            @BinderParam(BindUserIdGroupPairToSourceSecurityGroupFormParams.class) UserIdGroupPair sourceSecurityGroup);

   /**
    * 
    * Adds permissions to a security group.
    * <p/>
    * Permissions are specified by the IP protocol (TCP, UDP or ICMP), the source of the request (by
    * IP range or an Amazon EC2 user-group pair), the source and destination port ranges (for TCP
    * and UDP), and the ICMP codes and types (for ICMP). When authorizing ICMP, -1 can be used as a
    * wildcard in the type and code fields. Permission changes are propagated to instances within
    * the security group as quickly as possible. However, depending on the number of instances, a
    * small delay might occur.
    * 
    * @param groupName
    *           Name of the group to modify. The name must be valid and belong to the account
    * @param ipProtocol
    *           IP protocol.
    * @param fromPort
    *           Start of port range for the TCP and UDP protocols, or an ICMP type number. An ICMP
    *           type number of -1 indicates a wildcard (i.e., any ICMP type number).
    * @param toPort
    *           End of port range for the TCP and UDP protocols, or an ICMP code. An ICMP code of -1
    *           indicates a wildcard (i.e., any ICMP code).
    * @param cidrIp
    *           CIDR range.
    * 
    * @see #createSecurityGroup
    * @see #describeSecurityGroups
    * @see #revokeSecurityGroupIngress
    * @see #deleteSecurityGroup
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-AuthorizeSecurityGroupIngress.html"
    * 
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AuthorizeSecurityGroupIngress")
   Future<Void> authorizeSecurityGroupIngress(@FormParam("GroupName") String groupName,
            @FormParam("IpProtocol") IpProtocol ipProtocol, @FormParam("FromPort") int fromPort,
            @FormParam("ToPort") int toPort, @FormParam("CidrIp") String cidrIp);

   /**
    * 
    * Revokes permissions from a security group. The permissions used to revoke must be specified
    * using the same values used to grant the permissions.
    * 
    * @param groupName
    *           Name of the group to modify. The name must be valid and belong to the account
    * @param sourceSecurityGroup
    *           group to associate with this group.
    * 
    * @see #createSecurityGroup
    * @see #describeSecurityGroups
    * @see #authorizeSecurityGroupIngress
    * @see #deleteSecurityGroup
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-RevokeSecurityGroupIngress.html"
    * 
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RevokeSecurityGroupIngress")
   Future<Void> revokeSecurityGroupIngress(
            @FormParam("GroupName") String groupName,
            @BinderParam(BindUserIdGroupPairToSourceSecurityGroupFormParams.class) UserIdGroupPair sourceSecurityGroup);

   /**
    * 
    * Revokes permissions from a security group. The permissions used to revoke must be specified
    * using the same values used to grant the permissions.
    * <p/>
    * Permissions are specified by IP protocol (TCP, UDP, or ICMP), the source of the request (by IP
    * range or an Amazon EC2 user-group pair), the source and destination port ranges (for TCP and
    * UDP), and the ICMP codes and types (for ICMP).
    * 
    * Permission changes are quickly propagated to instances within the security group. However,
    * depending on the number of instances in the group, a small delay is might occur.
    * 
    * @param groupName
    *           Name of the group to modify. The name must be valid and belong to the account
    * @param ipProtocol
    *           IP protocol.
    * @param fromPort
    *           Start of port range for the TCP and UDP protocols, or an ICMP type number. An ICMP
    *           type number of -1 indicates a wildcard (i.e., any ICMP type number).
    * @param toPort
    *           End of port range for the TCP and UDP protocols, or an ICMP code. An ICMP code of -1
    *           indicates a wildcard (i.e., any ICMP code).
    * @param cidrIp
    *           CIDR range.
    * 
    * @see #createSecurityGroup
    * @see #describeSecurityGroups
    * @see #authorizeSecurityGroupIngress
    * @see #deleteSecurityGroup
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-RevokeSecurityGroupIngress.html"
    * 
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RevokeSecurityGroupIngress")
   Future<Void> revokeSecurityGroupIngress(@FormParam("GroupName") String groupName,
            @FormParam("IpProtocol") IpProtocol ipProtocol, @FormParam("FromPort") int fromPort,
            @FormParam("ToPort") int toPort, @FormParam("CidrIp") String cidrIp);
}
