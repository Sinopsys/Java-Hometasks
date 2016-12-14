package com.company;

import java.util.concurrent.BlockingQueue;

/**
 * User: artem
 * Date: 12/2/16.
 * To change this template use File | Settings | File Templates
 */
public class Producer implements Runnable{

    protected BlockingQueue queue = null;

    boolean jobDone = false;
    public synchronized boolean isJobDone() { return jobDone; }
    public synchronized void setJobDone(boolean jobDone) { this.jobDone = jobDone; }

    public Producer(BlockingQueue queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            // Producer does not work in a loop.
            // produce things only once and when jobDone is true, he dies
            System.out.println("Putting producer in queue");
            queue.put(this);
            // we are the only one who can touch this object
            synchronized (this) {
                while (!jobDone) {
                    System.out.println("Producer is waiting");
                    // ok, other code can touch it now
                    wait();
                }
            }
            System.out.println("Producer has job is done!");
            // producer process ends, he quits
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Producer{" +
                "queue=" + queue +
                '}';
    }
}
