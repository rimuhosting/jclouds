package org.jclouds.aws.ec2.options;

import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.asType;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.enableMonitoring;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.inAvailabilityZone;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.withAdditionalInfo;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.withDeviceName;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.withKernelId;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.withKeyName;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.withRamdisk;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.withSecurityGroup;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.withSubnetId;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.withUserData;
import static org.jclouds.aws.ec2.options.RunInstancesOptions.Builder.withVirtualName;
import static org.testng.Assert.assertEquals;

import java.util.Collections;

import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.http.options.HttpRequestOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of RunInstancesOptions and RunInstancesOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class RunInstancesOptionsTest {

   @Test
   public void testAssignability() {
      assert HttpRequestOptions.class.isAssignableFrom(RunInstancesOptions.class);
      assert !String.class.isAssignableFrom(RunInstancesOptions.class);
   }

   @Test
   public void testWithKeyName() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.withKeyName("test");
      assertEquals(options.buildFormParameters().get("KeyName"), Collections.singletonList("test"));
   }

   @Test
   public void testNullWithKeyName() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("KeyName"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithKeyNameStatic() {
      RunInstancesOptions options = withKeyName("test");
      assertEquals(options.buildFormParameters().get("KeyName"), Collections.singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithKeyNameNPE() {
      withKeyName(null);
   }

   @Test
   public void testWithSecurityGroup() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.withSecurityGroup("test");
      assertEquals(options.buildFormParameters().get("SecurityGroup"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullWithSecurityGroup() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("SecurityGroup"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithSecurityGroupStatic() {
      RunInstancesOptions options = withSecurityGroup("test");
      assertEquals(options.buildFormParameters().get("SecurityGroup"), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithSecurityGroupNPE() {
      withSecurityGroup(null);
   }

   @Test
   public void testWithAdditionalInfo() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.withAdditionalInfo("test");
      assertEquals(options.buildFormParameters().get("AdditionalInfo"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullWithAdditionalInfo() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("AdditionalInfo"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithAdditionalInfoStatic() {
      RunInstancesOptions options = withAdditionalInfo("test");
      assertEquals(options.buildFormParameters().get("AdditionalInfo"), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithAdditionalInfoNPE() {
      withAdditionalInfo(null);
   }

   @Test
   public void testWithUserData() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.withUserData("test");
      assertEquals(options.buildFormParameters().get("UserData"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullWithUserData() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("UserData"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithUserDataStatic() {
      RunInstancesOptions options = withUserData("test");
      assertEquals(options.buildFormParameters().get("UserData"), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithUserDataNPE() {
      withUserData(null);
   }

   @Test
   public void testWithInstanceType() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.asType(InstanceType.C1_XLARGE);
      assertEquals(options.buildFormParameters().get("InstanceType"), Collections
               .singletonList("c1.xlarge"));
   }

   @Test
   public void testNullWithInstanceType() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("InstanceType"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithInstanceTypeStatic() {
      RunInstancesOptions options = asType(InstanceType.C1_XLARGE);
      assertEquals(options.buildFormParameters().get("InstanceType"), Collections
               .singletonList("c1.xlarge"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithInstanceTypeNPE() {
      asType(null);
   }

   @Test
   public void testInAvailabilityZone() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.inAvailabilityZone("test");
      assertEquals(options.buildFormParameters().get("Placement.AvailabilityZone"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullAvailabilityZone() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("Placement.AvailabilityZone"),
               Collections.EMPTY_LIST);
   }

   @Test
   public void testInAvailabilityZoneStatic() {
      RunInstancesOptions options = inAvailabilityZone("test");
      assertEquals(options.buildFormParameters().get("Placement.AvailabilityZone"), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testInAvailabilityZoneNPE() {
      inAvailabilityZone(null);
   }

   @Test
   public void testWithKernelId() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.withKernelId("test");
      assertEquals(options.buildFormParameters().get("KernelId"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullWithKernelId() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("KernelId"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithKernelIdStatic() {
      RunInstancesOptions options = withKernelId("test");
      assertEquals(options.buildFormParameters().get("KernelId"), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithKernelIdNPE() {
      withKernelId(null);
   }

   @Test
   public void testWithDeviceName() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.withDeviceName("test");
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.DeviceName"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullWithDeviceName() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.DeviceName"),
               Collections.EMPTY_LIST);
   }

   @Test
   public void testWithDeviceNameStatic() {
      RunInstancesOptions options = withDeviceName("test");
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.DeviceName"), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithDeviceNameNPE() {
      withDeviceName(null);
   }

   @Test
   public void testWithMonitoringEnabled() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.enableMonitoring();
      assertEquals(options.buildFormParameters().get("Monitoring.Enabled"), Collections
               .singletonList("true"));
   }

   @Test
   public void testNullWithMonitoringEnabled() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("Monitoring.Enabled"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithMonitoringEnabledStatic() {
      RunInstancesOptions options = enableMonitoring();
      assertEquals(options.buildFormParameters().get("Monitoring.Enabled"), Collections
               .singletonList("true"));
   }

   @Test
   public void testWithSubnetId() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.withSubnetId("test");
      assertEquals(options.buildFormParameters().get("SubnetId"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullWithSubnetId() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("SubnetId"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithSubnetIdStatic() {
      RunInstancesOptions options = withSubnetId("test");
      assertEquals(options.buildFormParameters().get("SubnetId"), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithSubnetIdNPE() {
      withSubnetId(null);
   }

   @Test
   public void testWithRamdisk() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.withRamdisk("test");
      assertEquals(options.buildFormParameters().get("RamdiskId"), Collections
               .singletonList("test"));
   }

   @Test
   public void testNullWithRamdisk() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("RamdiskId"), Collections.EMPTY_LIST);
   }

   @Test
   public void testWithRamdiskStatic() {
      RunInstancesOptions options = withRamdisk("test");
      assertEquals(options.buildFormParameters().get("RamdiskId"), Collections
               .singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithRamdiskNPE() {
      withRamdisk(null);
   }

   @Test
   public void testWithVirtualName() {
      RunInstancesOptions options = new RunInstancesOptions();
      options.withVirtualName("test");
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.VirtualName"),
               Collections.singletonList("test"));
   }

   @Test
   public void testNullWithVirtualName() {
      RunInstancesOptions options = new RunInstancesOptions();
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.VirtualName"),
               Collections.EMPTY_LIST);
   }

   @Test
   public void testWithVirtualNameStatic() {
      RunInstancesOptions options = withVirtualName("test");
      assertEquals(options.buildFormParameters().get("BlockDeviceMapping.VirtualName"),
               Collections.singletonList("test"));
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testWithVirtualNameNPE() {
      withVirtualName(null);
   }

}
