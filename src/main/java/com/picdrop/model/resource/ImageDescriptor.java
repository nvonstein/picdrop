/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.picdrop.model.FileType;
import java.util.Map;
import org.mongodb.morphia.annotations.Embedded;

/**
 *
 * @author i330120
 */
public class ImageDescriptor extends ResourceDescriptor {

    @Embedded("thumbnailUris")
    Map<String, String> thumbnailUris;
    String orientation; // TODO enum?

    public ImageDescriptor() {
        super(FileType.IMAGE_WILDTYPE);
    }  

    public Map<String, String> getThumbnailUris() {
        return thumbnailUris;
    }

    public void setThumbnailUris(Map<String, String> thumbnailUris) {
        this.thumbnailUris = thumbnailUris;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
    
    @JsonIgnore
    public String getThumbnailUri(String key) {
        return this.thumbnailUris.get(key);
    }
}
