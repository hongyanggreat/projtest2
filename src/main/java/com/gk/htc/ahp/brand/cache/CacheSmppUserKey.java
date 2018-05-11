/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.cache;

import com.gk.htc.ahp.brand.common.Tool;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author TUANPLA
 */
public class CacheSmppUserKey {

    private static final HashMap<String, String> SESSIONID_USER_KEY = new HashMap<>();
    protected final Object monitor;

    @SuppressWarnings("LeakingThisInConstructor")
    public CacheSmppUserKey() {
        super();
        this.monitor = this;
    }

    public void put(String smppSession, String userKey) {
        synchronized (monitor) {
            SESSIONID_USER_KEY.put(smppSession, userKey);
            monitor.notifyAll();
        }
    }

    public String get(String smppSession) {
        synchronized (monitor) {
            String userKey = SESSIONID_USER_KEY.get(smppSession);
            return userKey;
        }
    }

    public String remove(String smppSession) {
        synchronized (monitor) {
            String userKey = SESSIONID_USER_KEY.remove(smppSession);
            return userKey;
        }
    }

    public void showKeyCache() {
        synchronized (monitor) {
            Set<String> keys = SESSIONID_USER_KEY.keySet();
            for (String key : keys) {
                Tool.debug(key + "=" + SESSIONID_USER_KEY.get(key));
            }
            monitor.notifyAll();
        }
    }
}
