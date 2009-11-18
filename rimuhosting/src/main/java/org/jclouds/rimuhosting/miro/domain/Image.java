package org.jclouds.rimuhosting.miro.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by IntelliJ IDEA.
 * User: ivan
 * Date: 18/11/2009
 * Time: 4:54:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class Image implements Comparable<Image>{
    @SerializedName("distro_code")
    private String id;
    @SerializedName("distro_desciption")
    private String description;
    @Override
    public int compareTo(Image image) {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
