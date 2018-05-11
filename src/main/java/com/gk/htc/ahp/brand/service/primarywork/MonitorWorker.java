/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.service.primarywork;

import com.gk.htc.ahp.brand.common.MyConfig;
import com.gk.htc.ahp.brand.common.Tool;
import java.util.LinkedList;

/**
 *
 * @author TUANPLA
 */
public class MonitorWorker {

    private static final LinkedList<String> DEMON_THREAD_LIST = new LinkedList<>();            // Dem So Demon Thread
    private static final LinkedList<WorkQueue> WORKS = new LinkedList<>();
    int delay;

    public static void addWorkQueue(final WorkQueue work) {
        synchronized (WORKS) {
            WORKS.add(work);
            WORKS.notify();
        }
    }

    public static void addDemonName(String name) {
        synchronized (DEMON_THREAD_LIST) {
            DEMON_THREAD_LIST.add(name);
            DEMON_THREAD_LIST.notify();
        }

    }

    public static void removeDemonName(String name) {
        synchronized (DEMON_THREAD_LIST) {
            DEMON_THREAD_LIST.remove(name);
            DEMON_THREAD_LIST.notify();
            Tool.debug("|==> " + name + " ended...");
        }
    }

    public static int getDemonSize() {
        synchronized (DEMON_THREAD_LIST) {
            return DEMON_THREAD_LIST.size();
        }
    }

    public static void showDemon() {
        synchronized (DEMON_THREAD_LIST) {
            int i = 1;
            for (String one : DEMON_THREAD_LIST) {
                System.out.println((i++) + ". " + one + " is runing");
            }
            DEMON_THREAD_LIST.notify();
        }
    }

    public static void ShowMonitor() {
        synchronized (WORKS) {
            if (!WORKS.isEmpty()) {
                System.out.println("-------------MonitorWorker [" + MyConfig.LB_NODE + "]-----------");
                for (WorkQueue work : WORKS) {
                    System.out.println(
                            String.format("M-Worker [" + work.getName() + "] [%d] Active: %d, Wait %d, Completed: %d, Task: %d",
                                    work.getMaxPoolSize(),
                                    work.getActiveCount(),
                                    work.getWaitTaskCount(),
                                    work.getCompletedTaskCount(),
                                    work.getTaskCount()
                            )
                    );
                }
                showDemon();
            }
            WORKS.notify();
        }
    }
}
