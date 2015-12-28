package com.company;

import com.company.entities.Item;

import java.util.Scanner;
import java.util.concurrent.LinkedTransferQueue;


public class Consumer implements Runnable {


    private LinkedTransferQueue<Item> linkedTransferQueue;


    public Consumer(LinkedTransferQueue<Item> linkedTransferQueue) {
        this.linkedTransferQueue = linkedTransferQueue;

    }

    @Override
    public void run() {
        try {
            doIt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void doIt() throws InterruptedException {
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
