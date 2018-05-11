/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.thread;

import com.gk.htc.ahp.brand.app.AppStart;
import com.gk.htc.ahp.brand.common.DateProc;
import com.gk.htc.ahp.brand.common.Tool;
import com.gk.htc.ahp.brand.entity.SmsBrandQueue;
import com.gk.htc.ahp.brand.service.primarywork.MonitorWorker;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author TUANPLA
 */
public class ConcatLongMT extends Thread {

    private static Logger logger = Logger.getLogger(ConcatLongMT.class);
    private static final HashMap<String, LinkedList<SmsBrandQueue>> CACHE_LONG_MT = new HashMap<>();
    protected final Object monitor;

    @SuppressWarnings("LeakingThisInConstructor")
    public ConcatLongMT() {
        this.setName("ConcatLongMT:" + DateProc.createTimestamp());
        MonitorWorker.addDemonName(this.getName());
        monitor = this;
    }
    protected boolean stop = false;

    public void shutDown() {
        stop = true;
    }

    private HashMap<String, LinkedList<SmsBrandQueue>> cloneHash() {
        synchronized (monitor) {
            HashMap<String, LinkedList<SmsBrandQueue>> result = (HashMap<String, LinkedList<SmsBrandQueue>>) CACHE_LONG_MT.clone();
            return result;
        }
    }

    @Override
    public void run() {
        while (AppStart.isRuning && !stop) {
            HashMap<String, LinkedList<SmsBrandQueue>> CLONE_CACHE = cloneHash();
            try {
                if (CLONE_CACHE != null && CLONE_CACHE.size() > 0) {
                    logger.debug("ConcatLongMT not null:" + CLONE_CACHE.size());
                    Set<String> keySets = CLONE_CACHE.keySet();
                    for (String oneKey : keySets) {
                        logger.debug("OneKey Set from CLONE_CACHE:" + oneKey);
                        // Lay ra list MT Long tu Clone de kiem tra
                        LinkedList<SmsBrandQueue> oneListLongMT = CLONE_CACHE.get(oneKey);
                        if (oneListLongMT != null && oneListLongMT.size() > 0) {
                            // Lay Ra MT dau Tien
                            SmsBrandQueue first = oneListLongMT.get(0);
                            // Da du tin dai thi tien hanh gep va Gui ve Client
                            if (oneListLongMT.size() == first.getSarTotalSegments()) {
                                // Remove List
                                LinkedList<SmsBrandQueue> listDoSend = removeLongMT(oneKey);
                                SmsBrandQueue msgDoSend = list2MTobject(listDoSend);
                                // Day ve de Submit thi phai Chuyen SarTotalSegments =1 de khong day vao Long nua                                
                                msgDoSend.setSarTotalSegments(1);
                                AppStart.sendPrimaryTask.addToqueue(msgDoSend);
                            } else {
                                // Chua du tin dai thi check Time out
                                long distance = System.currentTimeMillis() - first.getStartTime();
                                if (distance > 12 * 1000) {
                                    logger.debug("ConcatLongMT Qua 10s Gui thoi");
                                    // Qua 10s thi cung gep va gui ve Client
                                    // Remove List
                                    LinkedList<SmsBrandQueue> listDoSend = removeLongMT(oneKey);
                                    // Chuyen List => MT dai
                                    SmsBrandQueue msgDoSend = list2MTobject(listDoSend);
                                    msgDoSend.setTimeOut(true);
                                    // Day ve de Submit thi phai Chuyen SarTotalSegments =1 de khong day vao Long nua    
                                    msgDoSend.setSarTotalSegments(1);
                                    AppStart.sendPrimaryTask.addToqueue(msgDoSend);
                                } else {
                                    logger.debug("Not Time out Continute Wait");
                                }
                            }
                        } else if (oneListLongMT != null) {
                            logger.debug("One Long MT not valid: size=" + oneListLongMT.size());
                        } else {
                            logger.debug("One Long MT not valid: null or Empty");
                        }
                    }
                } else {
                    //  logger.debug("--> CLONE_CACHE IS NULL OR EMPTY");
                }
                // Thread Sleep 1 Second
                Thread.sleep(2000);
            } catch (Exception e) {
                logger.error(Tool.getLogMessage(e));
            }
        }
        MonitorWorker.removeDemonName(this.getName());
    }

    public void putLongMT(SmsBrandQueue mt) {
        synchronized (monitor) {
            String keyCache = mt.getLabel() + "-" + mt.getSarMsgRefNum() + "-" + mt.getPhone();
            logger.debug("Vao Put Long MT key:" + keyCache);
            LinkedList<SmsBrandQueue> oneLongMT = CACHE_LONG_MT.get(keyCache);
            if (oneLongMT != null) {
                logger.debug("->Long MT:" + keyCache + " is not null -> Add Last");
                oneLongMT.addLast(mt);
                // Kiem tra Luon xem du tin dai chua
                if (mt.getSarTotalSegments() == oneLongMT.size()) {
                    // Neu du roi => Remove List
                    LinkedList<SmsBrandQueue> listDoSend = CACHE_LONG_MT.remove(keyCache);
                    // Chuyen thanh 1 Queue voi Message Dai
                    SmsBrandQueue msgDoSend = list2MTobject(listDoSend);
                    // Day ve de Submit thi phai Chuyen SarTotalSegments =1 de khong day vao Long nua                                
                    msgDoSend.setSarTotalSegments(1);
                    AppStart.sendPrimaryTask.addToqueue(msgDoSend);
                } else {
                    // Chua du thi cache thoi
                    CACHE_LONG_MT.put(keyCache, oneLongMT);
                }
            } else {
                logger.debug("->Long MT:" + keyCache + " is null -> Renew Add Last");
                oneLongMT = new LinkedList<>();
                oneLongMT.addLast(mt);
                CACHE_LONG_MT.put(keyCache, oneLongMT);
            }
            monitor.notifyAll();
        }
    }

    private LinkedList<SmsBrandQueue> removeLongMT(String key) {
        synchronized (monitor) {
            logger.debug("ConcatLongMT.removeLongMT:" + key);
            return CACHE_LONG_MT.remove(key);
        }
    }

    /**
     * Gep lai tin dai de gui qua SMPP hoac Webservice Cho Nay van chu xu ly
     * viec tin den truoc den sau theo thu tu
     *
     * @param listMT
     * @return
     */
    private SmsBrandQueue list2MTobject(LinkedList<SmsBrandQueue> listMT) {
        SmsBrandQueue oneQueue = null;
        if (listMT != null && listMT.size() > 0) {
            // Lay ra thang dau tien de gep tin
            oneQueue = listMT.remove(0);
            String info = oneQueue.getMessage();
            String listMessageId = "";
            // Khi gui qua SMPP mac dinh la 1 tin 1 lan gui
            int totalMsg = 1;
            for (SmsBrandQueue oneMT : listMT) {
                info += oneMT.getMessage();
                // Gep MessageId cua SMPP Server de tra DR
                listMessageId += oneMT.getTranId() + "-";
                // Trong list Long MT thi gui tang le
                totalMsg += 1;
            }
            // Set lai Tong so Message
            oneQueue.setTotalSms(totalMsg);
            oneQueue.setMessage(info);
            if (listMessageId.endsWith("-")) {
                listMessageId = listMessageId.substring(0, (listMessageId.length() - 1));
            }
            oneQueue.setList_messageId(listMessageId);
        }
        return oneQueue;
    }
}
