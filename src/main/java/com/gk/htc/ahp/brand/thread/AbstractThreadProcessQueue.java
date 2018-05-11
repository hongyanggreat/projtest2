/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gk.htc.ahp.brand.thread;

import com.gk.htc.ahp.brand.service.primarywork.Queue;

/**
 *
 * @author TUANPLA
 * @param <V>
 */
abstract public class AbstractThreadProcessQueue<V> extends Thread {

    protected final Queue<V> queue_process;

    protected boolean stop = false;

    public AbstractThreadProcessQueue(Queue<V> queue) {
        queue_process = queue;
    }

    public abstract void shutDown();

    public abstract void StoreQueue();

    public abstract void addToqueue(V item);
}
