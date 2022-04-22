package com.example.application.logic;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Random;

public class Miner extends Thread {
    private Random random = new Random();
    public String name;
    private BlockChain blockChain;
    private int miningPower;

    private PrivateKey privateKey;
    private PublicKey publicKey;


    public Miner(String name, BlockChain blockChain) {
        this.name = name;
        this.blockChain = blockChain;
        miningPower = random.nextInt(140)+1;
        generateKeyPair();
    }

    public void minerStart() {
        while (true) {
            blockChain.minePendingTransactions(this, miningPower);
        }
    }

    public String getMinerName() {
        return name;
    }

    @Override
    public void run() {
        this.minerStart();
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();
            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }catch(Exception e) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public int getMinerHashPower() {
        return miningPower;
    }
}