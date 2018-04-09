package com.lls.thread;
import javax.annotation.Resource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Resource
public class Storage {

    public  BlockingQueue<com.lls.thread.Resource> queues = new LinkedBlockingQueue<com.lls.thread.Resource>(10);


    /**
     * 生产
     *
     * @param resource
     *            产品
     * @throws InterruptedException
     */
    public void push(com.lls.thread.Resource resource) throws InterruptedException {
        System.out.println("add---------------"+queues.size());
        queues.put(resource);
    }

    /**
     * 消费
     *
     * @return 产品
     * @throws InterruptedException
     */
    public com.lls.thread.Resource pop() throws InterruptedException {
        System.out.println("pop---------------"+queues.size());
        return queues.take();
    }

}
