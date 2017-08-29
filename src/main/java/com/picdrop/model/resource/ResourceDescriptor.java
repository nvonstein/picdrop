/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.picdrop.json.Views;
import com.picdrop.model.FileType;
import com.picdrop.model.Mergeable;
import java.io.IOException;

/**
 *
 * @author i330120
 */
public class ResourceDescriptor implements Mergeable<ResourceDescriptor>{

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

    @JsonView(value = Views.Public.class)
    public FileType getType() {
        return type;
    }

    @JsonView(value = Views.Ignore.class)
    public void setType(FileType type) {
        this.type = type;
    }

    public static ResourceDescriptor get(FileType ft) {
        if (ft != null) {
            if (FileType.IMAGE_WILDTYPE.isCovering(ft)) {
                return new ImageDescriptor(ft);
            }
        }
        return new ResourceDescriptor(FileType.UNKNOWN);
    }

    @JsonView(value = Views.Ignore.class)
    public boolean isImage() {
        return FileType.IMAGE_WILDTYPE.isCovering(this.type);
    }

    @JsonView(value = Views.Ignore.class)
    public boolean isUnknown() {
        return FileType.UNKNOWN.isCovering(this.type);
    }
    
    @JsonView(value = Views.Ignore.class)
    public boolean isGlobalWildtype() {
        return this.type.isCovering(FileType.WILDTYPE);
    }
    
    @Override
    public ResourceDescriptor merge(ResourceDescriptor update) throws IOException {
        if (update == null) {
            return this;
        }
        if ((update.type != null) && !this.isGlobalWildtype()) {
            if (this.type.isCovering(update.type)) {
                this.type = update.type;
            }
        }
        return this;
    }

}
