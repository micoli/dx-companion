package org.micoli.dxcompanion.configuration.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(name = "path", value = Path.class),
    @JsonSubTypes.Type(name = "action", value = Action.class),
    @JsonSubTypes.Type(name = "observedFile", value = ObservedFile.class),
})
public class AbstractNode {
    public String label;
    public String getLabel() {
        return label;
    }
}
