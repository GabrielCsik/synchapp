package com.example.application.logic;

import java.util.ArrayList;
import java.util.Date;

public class Block {
    public String hash;
    public String previousHash;
    public ArrayList<Transaction> transactionListInBlock;
    private long timeStamp; //as number of milliseconds since 1/1/1970.
    private int nonce;
    private Object obj = new Object();

    public Block() {
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash(); //Making sure we do this after we set the other values.
    }

    public String calculateHash() {
//        String listString = "";
//        if(transactionListInBlock != null) {
//            for (Transaction transaction : transactionListInBlock) {
//                listString += transaction.hashTransaction();
//            }
//        }
        return StringUtil.applySha256(Long.toString(timeStamp) + Integer.toString(nonce));
    }

    public void mineBlock(int difficulty, int minePower) {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        int i = 0;
        while (!hash.substring(0, difficulty).equals(target)) {
            if (i < minePower) {
                nonce++;
                hash = calculateHash();
                i++;
            } else {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {

                }
                i = 0;
            }
        }
//        System.out.println("Block Mined!!! : " + hash);
    }

    public void setTransactionListInBlock(ArrayList<Transaction> transactionListInBlock) {
        this.transactionListInBlock = transactionListInBlock;
    }

    public void setPrevHash(String hash) {
        previousHash = hash;
    }
}
