/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Private
 */
public class ReadProperty {

    private final static ReadProperty INSTANCE = new ReadProperty();

    static public ReadProperty getInstance() {
        return INSTANCE;
    }

    public String getValue(String filePatch, String key) {
        try {
            File f = new File(filePatch);
            if (f.exists()) {
                Properties pro = new Properties();
                FileInputStream in = new FileInputStream(f);
                pro.load(in);

                String p = pro.getProperty(key);
                return p;
            } else {
                System.out.println("File not found!");
                return null;
            }
        } catch (IOException e) {
            System.out.println("ReadProperty.getValue:"+e.getMessage());
            return null;
        }
    }

    public Properties getProperties(String filePatch) {
        try {
            File f = new File(filePatch);
            if (f.exists()) {
                Properties pro = new Properties();
                FileInputStream in = new FileInputStream(f);
                pro.load(in);
                return pro;
            } else {
                System.out.println("File not found!");
                return null;
            }
        } catch (IOException e) {
            System.out.println("ReadProperty.getProperties:"+e.getMessage());
            return null;
        }
    }
}
