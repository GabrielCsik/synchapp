package com.example.application.logic;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockChain {
    public static List<Block> blockchain;
    public ArrayList<Transaction> pendingTransactions;
    public static int difficulty;
    public int miningReward = 10;
    public int numOfBlocks = 0;
    private AtomicInteger numOfTrans = new AtomicInteger(0);
    private Object obj = new Object();

    public BlockChain() {
        blockchain = new ArrayList<>();
        blockchain.add(createGenenisBlock());
        pendingTransactions = new ArrayList<>();
    }

    public Block createGenenisBlock() {
        Block genesisBlock = new Block();
        genesisBlock.setPrevHash("0");
        numOfBlocks++;
        return genesisBlock;
    }

    public Block getLatestBlock() {
        return blockchain.get(blockchain.size() - 1);
    }

    public void minePendingTransactions(Miner miner, int minePower) {
            Block newBLock = new Block();
            newBLock.mineBlock(difficulty, minePower);
            synchronized (obj) {
                newBLock.setPrevHash(getLatestBlock().hash);
//        System.out.println(counter);
//        System.out.print(miningRewardAdress);
                newBLock.setTransactionListInBlock((ArrayList<Transaction>) pendingTransactions.clone());
                blockchain.add(newBLock);
                pendingTransactions.clear();
                pendingTransactions.add(new Transaction(miner.getPublicKey(), miningReward));
                numOfBlocks++;
                numOfTrans.incrementAndGet();
            }
    }

    public void createTransaction(Transaction transaction) {
//        synchronized (obj) {
        pendingTransactions.add(transaction);
        numOfTrans.incrementAndGet();
//        System.out.print(" T");
//        }
    }

    public int getBalanceOfAddress(PublicKey publicKey) {
        int balance = 0;
        for (Block block : blockchain) {
            if (block.transactionListInBlock != null) {
                for (Transaction tmp : block.transactionListInBlock) {
                    if (tmp.getFromAdress() == null) {
                        if (tmp.getToAdress().equals(publicKey)) {
                            balance = balance + tmp.getAmount();
                        }
                    } else if (tmp.getToAdress() == null) {
                        if (tmp.getFromAdress().equals(tmp.getToAdress())) {
                            if (tmp.getToAdress().equals(publicKey)) {
                                balance = balance - tmp.getAmount();
                            }
                        }
                    } else {
                        if (tmp.getFromAdress().equals(publicKey)) {
                            balance = balance - tmp.getAmount();
                        }
                        if (tmp.getToAdress().equals(publicKey)) {
                            balance = balance + tmp.getAmount();
                        }
                    }
                }
            }
        }
        return balance;
    }

    public int getNumofTransactions() {
        return numOfTrans.get();
    }

    public static Boolean isChainValid(BlockChain blockChain) {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        //loop through blockchain to check hashes:
        for (int i = 1; i < blockChain.getBlockchain().size(); i++) {
            currentBlock = blockChain.getBlockchain().get(i);
            previousBlock = blockChain.getBlockchain().get(i - 1);
            //compare registered hash and calculated hash:
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous Hashes not equal");
                System.out.println(previousBlock.hash);
                System.out.println(currentBlock.previousHash);
                return false;
            }
            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
            for (Transaction transaction : currentBlock.transactionListInBlock) {
                if(transaction == null) continue;
                if (!transaction.verifiySignature()) {
                    System.out.println("Sgnature is not right");
                    return false;
                }
            }
        }
        return true;
    }

    public List<Block> getBlockchain() {
        return blockchain;
    }

    public static void setDifficulty(int difficulty) {
        BlockChain.difficulty = difficulty;
    }

    public float getNumofBlocks() {
        return (float) numOfBlocks;
    }
}
