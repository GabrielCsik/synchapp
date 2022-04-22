package com.example.application.logic;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class User extends Thread {
    private String name;
    private BlockChain blockChain;
    private List<User> userList;
    private Random random = new Random();

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public User(String name, BlockChain blockChain) {
        this.name = name;
        this.blockChain = blockChain;
        generateKeyPair();
    }

    public String getUserName() {
        return name;
    }

    public void createTransaction() {
        while (true) {
            try {
                this.sleep(random.nextInt(10000)+1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            blockChain.createTransaction(new Transaction(this.publicKey,
                    userList.get(random.nextInt(userList.size()-1)).getPublicKey(),
                    random.nextInt(10000), privateKey));
        }
    }

    @Override
    public void run() {
        this.createTransaction();
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<User> getUserList() {
        return userList;
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

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
