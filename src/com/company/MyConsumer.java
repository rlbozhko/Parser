package com.company;

import com.company.entities.Item;

import java.util.Scanner;
import java.util.concurrent.LinkedTransferQueue;


public class MyConsumer implements Runnable {


    private LinkedTransferQueue<Item> linkedTransferQueue;


    public MyConsumer(LinkedTransferQueue<Item> linkedTransferQueue) {
        this.linkedTransferQueue = linkedTransferQueue;

    }

    @Override
    public void run() {
        try {
            doSmth();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void doSmth() throws InterruptedException {

        System.out.println("Попали в MyConsumer");
        Scanner in = new Scanner(System.in);
        int i = 0;
        while(linkedTransferQueue.peek()!=null){
            System.out.println(linkedTransferQueue.take());
            if(i++>=100){
                System.out.println("Continue? Press 1 to Exit or any other character to continue");
                if(in.nextInt()==1){
                    System.exit(0);
                }
            }
        }

    }
}
