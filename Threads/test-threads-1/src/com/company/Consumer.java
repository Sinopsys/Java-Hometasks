package com.company;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * User: artem
 * Date: 12/2/16.
 * To change this template use File | Settings | File Templates
 */
public class Consumer implements Runnable{

    protected BlockingQueue queue = null;
    boolean alive = true;
    Producer other_thing;

    public Consumer(BlockingQueue queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            while (alive) {
                System.out.println("Consumer woke up");
                if (queue.size() > 0) {
                    System.out.println("Consumer got something from queue");
                    other_thing = (Producer)queue.take();
                }
                else if (other_thing != null) {
                    // System.out.println("Consumer queue is empty, but we alreay have the other_thing");
                    // I wanna be the only one to work with this object
                    synchronized (other_thing) {
                        System.out.println("Consumer has job done");
                        other_thing.jobDone = false;
                        // emulate work, just wait for two seconds but pretend to be busy
                        // no one can access the other_thing for 2 seconds
                        TimeUnit.SECONDS.sleep(2);
                        // we are about to end. Allow others to access other_thing object
                        other_thing.notifyAll();
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

