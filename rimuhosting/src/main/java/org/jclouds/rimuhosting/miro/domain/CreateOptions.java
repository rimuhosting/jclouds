package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Some options we need to create a new server/VPS.
 *
 * Copyright RimuHosting 2009
 * @author Ivan Meredith <ivan@ivan.net.nz>
 */
public class CreateOptions {
   /**
    * The control panel to install on the VPS.  Currently only webmin is installable manually.
    * Currently null/empty string works.  For none.  Or webmin.
    * TODO: Control panels need a rest @GET interface. Or enum
    */
   @SerializedName("control_panel")
   private String controlPanel;
   /**
    * Must be a valid Image id.
    */
   @SerializedName("distro")
   private String imageId;
   /**
    * Must be FQDN.
    */
   @SerializedName("domain_name")
   private String name;
   /**
    * The password to use when setting up the server.  If not provided we will set a random one.
    */
   private String password;

   public String getControlPanel() {
      return controlPanel;
   }

   public void setControlPanel(String controlPanel) {
      this.controlPanel = controlPanel;
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

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }
}
