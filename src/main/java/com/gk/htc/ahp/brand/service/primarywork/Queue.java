package com.gk.htc.ahp.brand.service.primarywork;

import com.gk.htc.ahp.brand.common.Tool;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * <p>
 * Title: </p>
 * <p>
 * Description: Queue xu ly Data </p>
 * <p>
 * Copyright: Copyright (c) 2011</p>
 * <p>
 * Company:PG Media </p>
 *
 * @author TuanPLA
 * @version 3.0
 * @param <V>
 */
public class Queue<V> {

    static final Logger logger = Logger.getLogger(Queue.class);
    protected LinkedList<V> dataQueue = null;
    protected final Object monitor;
    private static int count = 0;
    private String name = "queue_" + ++count;
    private final static ArrayList queueList = new ArrayList();

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public static void showQueuesSize() {
        String response = "[----Queues Size:---\n";
        for (Object oneQueue : queueList) {
            Queue q = (Queue) oneQueue;
            response += q.getName() + ": " + q.size() + "\n";
        }
        response += "----End Queue Size-------]";
        Tool.debug(response);
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public Queue(String name) {
        this.monitor = this;
        this.dataQueue = new LinkedList<>();
        this.name = name;
        queueList.add(this);
    }

    /**
     * This method is used by a consummer. If you attempt to remove an object
     * from an dataQueue is empty dataQueue, you will be blocked (suspended)
     * until an object becomes available to remove. A blocked thread will thus
     * wake up.
     *
     * @return the first object (the one is removed).
     */
    public V dequeue() {
        synchronized (monitor) {
            while (dataQueue.isEmpty()) { //Threads are blocked
                try { //if the dataQueue is empty.
                    monitor.wait(); //wait until other thread call notify().
                } catch (InterruptedException ex) {
                    logger.error(Tool.getLogMessage(ex));
                }
            }
            return dataQueue.removeFirst();
        }
    }

    public void enqueue(V item) {
        synchronized (monitor) {
            dataQueue.addLast(item);
            monitor.notifyAll();
        }
    }

    public void enqueueFirst(V item) {
        synchronized (monitor) {
            dataQueue.addFirst(item);
            monitor.notifyAll();
        }
    }

    public int size() {
        synchronized (monitor) {
            return dataQueue.size();
        }
    }

    public boolean isEmpty() {
        synchronized (monitor) {
            return dataQueue.isEmpty();
        }
    }

    protected Collection dequeueAll() {
        List list;
        synchronized (monitor) {
            list = Arrays.asList(dataQueue.toArray());
            dataQueue.clear();
        }
        return list;
    }

    protected void enqueueAll(Collection c) {
        synchronized (monitor) {
            dataQueue.addAll(c);
            monitor.notifyAll();
        }
    }

    public boolean contain(V obj) {
        boolean flag = false;
        synchronized (monitor) {
            for (Iterator<V> it = dataQueue.iterator(); it.hasNext();) {
                Object one = it.next();
                if (one.equals(obj)) {
                    flag = true;
                    break;
                }
            }
            monitor.notifyAll();
            return flag;
        }
    }
}
