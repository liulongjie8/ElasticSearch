package com.lls.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@org.springframework.stereotype.Service
public class Service {

    public void start(String index, String type){

        SingleQueue.executorsProducer.execute(Producer.getRunbale(index,type));
        SingleQueue.executors.execute(Customer.getRunbale());

    }
}
