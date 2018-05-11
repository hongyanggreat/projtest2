package com.gk.htc.ahp.brand.bootstrap;

import java.io.*;

public class DirFilterWatcher implements FileFilter {

    private final String filter;

    public DirFilterWatcher() {
        this.filter = "";
    }

    public DirFilterWatcher(String filter) {
        this.filter = filter;
    }

    @Override
    public boolean accept(File file) {
        if ("".equals(filter)) {
            return true;
        }
        return (file.getName().endsWith(filter));
    }
}
