package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IntelliJ IDEA.
 * User: ivan
 * Date: 19/11/2009
 * Time: 12:39:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class Instance implements Comparable<Instance>{


   @SerializedName("allocated_ips")
private IpAddresses ipAddresses;
@SerializedName("billing_info")
private BillingData billingData;
@SerializedName("billing_oid")
private Long billingId;
@SerializedName("data_transfer_allowance")
private DataTransferAllowance allowance;
@SerializedName("distro")
private String imageId;       
   @SerializedName("domain_name")
   private String name;

   @SerializedName("host_server_oid")
   private String hostServerId;
   @SerializedName("is_on_customers_own_physical_server")
   private Boolean onDedicatedHardware;
   @SerializedName("order_oid")
   private Long id;
   @SerializedName("running_state")
   private String state;
   @SerializedName("server_type")
   private String type;
   private String slug;
   @SerializedName("vps_parameters")
   private InstanceParameters instanceParameters;

   public IpAddresses getIpAddresses() {
      return ipAddresses;
   }

   public void setIpAddresses(IpAddresses ipAddresses) {
      this.ipAddresses = ipAddresses;
   }

   public BillingData getBillingData() {
      return billingData;
   }

   public void setBillingData(BillingData billingData) {
      this.billingData = billingData;
   }

   public Long getBillingId() {
      return billingId;
   }

   public void setBillingId(Long billingId) {
      this.billingId = billingId;
   }

   public DataTransferAllowance getAllowance() {
      return allowance;
   }

   public void setAllowance(DataTransferAllowance allowance) {
      this.allowance = allowance;
   }

   public String getImageId() {
      return imageId;
   }

   public void setImageId(String imageId) {
      this.imageId = imageId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getHostServerId() {
      return hostServerId;
   }

   public void setHostServerId(String hostServerId) {
      this.hostServerId = hostServerId;
   }

   public Boolean isOnDedicatedHardware() {
      return onDedicatedHardware;
   }

   public void setOnDedicatedHardware(Boolean onDedicatedHardware) {
      this.onDedicatedHardware = onDedicatedHardware;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getState() {
      return state;
   }

   public void setState(String state) {
      this.state = state;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getSlug() {
      return slug;
   }

   public void setSlug(String slug) {
      this.slug = slug;
   }

   public InstanceParameters getInstanceParameters() {
      return instanceParameters;
   }

   public void setInstanceParameters(InstanceParameters instanceParameters) {
      this.instanceParameters = instanceParameters;
   }

   @Override
   public int compareTo(Instance instance) {
      return name.compareTo(instance.getName());
   }
}
