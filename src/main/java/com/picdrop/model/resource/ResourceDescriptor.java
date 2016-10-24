/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.picdrop.model.FileType;
import java.io.IOException;

/**
 *
 * @author i330120
 */
public class ResourceDescriptor {

    protected FileType type;

    protected ResourceDescriptor() {
    }

    public ResourceDescriptor(FileType type) {
        this.type = type;
    }

    @JsonIgnore
    public <T extends ResourceDescriptor> T to(Class<T> type) throws IOException {
        if (type == null) {
            throw new IllegalArgumentException("type is null");
        }
        if (!type.isInstance(this)) {
            throw new IOException(String.format("cannot cast to type '%s'", type.getName()));
        }
        return type.cast(this);
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public static ResourceDescriptor get(FileType ft) {
        if (ft != null) {
            if (FileType.IMAGE_WILDTYPE.isCovering(ft)) {
                return new ImageDescriptor();
            }
        }
        return new ResourceDescriptor(FileType.UNKNOWN);
    }
}
