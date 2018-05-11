/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.cache;

import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.ClientInfo;
import com.gk.htc.ahp.brand.jsmpp.session.SMPPServerSession;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author TUANPLA
 */
public class CacheClient {

    private static final HashMap<String, ClientInfo> SYSID_ADDRANGE_CLIENT = new HashMap<>();
    protected final Object monitor;

    @SuppressWarnings("LeakingThisInConstructor")
    public CacheClient() {
        super();
        this.monitor = this;
    }

    public void put(String smppSession, ClientInfo cl) {
        synchronized (monitor) {
            SYSID_ADDRANGE_CLIENT.put(smppSession, cl);
            monitor.notifyAll();
        }
    }

    public ClientInfo get(String smppSession) {
        synchronized (monitor) {
            ClientInfo userKey = SYSID_ADDRANGE_CLIENT.get(smppSession);
            return userKey;
        }
    }

    public ClientInfo remove(String smppSession) {
        synchronized (monitor) {
            ClientInfo userKey = SYSID_ADDRANGE_CLIENT.remove(smppSession);
            return userKey;
        }
    }

    public void showKeyCache() {
        synchronized (monitor) {
            Set<String> keys = SYSID_ADDRANGE_CLIENT.keySet();
            for (String key : keys) {
                Tool.debug(key + "=" + SYSID_ADDRANGE_CLIENT.get(key));
            }
            monitor.notifyAll();
        }
    }

    public void showClientCache() {
        synchronized (monitor) {
            Set<String> keys = SYSID_ADDRANGE_CLIENT.keySet();
            for (String key : keys) {
                ClientInfo cl = get(key);
                if (cl != null) {
                    Tool.debug("key=" + key + "|us=" + cl.getClname() + "|ip=" + cl.getIp() + "|arres=" + cl.getAddressRange() + "|session=" + cl.getSessionId() + "|btime=" + cl.getBindTime());
                }
            }
            monitor.notifyAll();
        }
    }

    public void unbindAllClient() {
        synchronized (monitor) {
            Set<String> keys = SYSID_ADDRANGE_CLIENT.keySet();
            for (String key : keys) {
                ClientInfo cl = get(key);
                SMPPServerSession _clientSession = cl.getSession();
                try {
                    if (_clientSession != null) {
                        _clientSession.unbindAndClose();
                        Tool.debug("===> unbindAndClose: [" + cl.getIp() + ":" + cl.getPort() + "=>" + cl.getAddressRange() + ":" + cl.getSessionId() + "]");
                    }
                } catch (Exception e) {
                }
            }
            monitor.notifyAll();
        }
    }
}
