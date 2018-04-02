package com.lls.thread;

import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Resource
public class SingleQueue {

    public static BlockingQueue<com.lls.thread.Resource> sharedQueue = new LinkedBlockingQueue<com.lls.thread.Resource>();

    public static ExecutorService executors = Executors.newFixedThreadPool(2);

    public static ExecutorService executorsProducer = Executors.newFixedThreadPool(1);





}
