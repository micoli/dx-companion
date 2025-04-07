package org.micoli.dxcompanion.configuration.models;

public class Configuration {
    AbstractNode[] nodes;
    private String serial = null;

    public AbstractNode[] getNodes() {
        return nodes;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSerial() {
        return serial;
    }
}