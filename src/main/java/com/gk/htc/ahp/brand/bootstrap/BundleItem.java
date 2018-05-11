package com.gk.htc.ahp.brand.bootstrap;

public class BundleItem {

    String name;
    String version;
    String className;
    String fileName;

    public BundleItem() {
        this.name = "";
        this.version = "";
        this.className = "";
        this.fileName = "";
    }

    public BundleItem(String name, String version, String className) {
        this.name = name;
        this.version = version;
        this.className = className;
    }

    public String getName() {
        return this.name;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getVersion() {
        return this.version;
    }

    public String getClassName() {
        return this.className;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
