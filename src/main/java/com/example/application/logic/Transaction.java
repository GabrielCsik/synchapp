package com.example.application.logic;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Transaction {
    private PublicKey fromAdress;
    private PublicKey toAdress;
    private int amount;
    public byte[] signature;
    private static int sequence = 0;

    public Transaction(PublicKey fromAdress, PublicKey toAdress, int amount, PrivateKey signatureFromAdress) {
        this.fromAdress = fromAdress;
        this.toAdress = toAdress;
        this.amount = amount;
        generateSignature(signatureFromAdress);
    }

    public Transaction(PublicKey toAdress, int amount) {
        this.toAdress = toAdress;
        this.amount = amount;
    }

    public String calculateHash() {
        sequence++;
        if (fromAdress == null) {
            return StringUtil.applySha256(StringUtil.getStringFromKey(toAdress)
                    + Integer.toString(amount) + sequence
            );
        }
        return StringUtil.applySha256(StringUtil.getStringFromKey(fromAdress)
                + StringUtil.getStringFromKey(toAdress) + Integer.toString(amount)
                + sequence
        );
    }

    public PublicKey getFromAdress() {
        return fromAdress;
    }

    public void setFromAdress(PublicKey fromAdress) {
        this.fromAdress = fromAdress;
    }

    public PublicKey getToAdress() {
        return toAdress;
    }

    public void setToAdress(PublicKey toAdress) {
        this.toAdress = toAdress;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return StringUtil.getStringFromKey(fromAdress) + "\n" + StringUtil.getStringFromKey(toAdress) + "\n" + amount;
    }

    //Signs all the data we dont wish to be tampered with.
    public void generateSignature(PrivateKey privateKey) {
        String data;
        data = StringUtil.getStringFromKey(fromAdress) + StringUtil.getStringFromKey(toAdress) + Float.toString(amount);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    //Verifies the data we signed hasnt been tampered with
    public boolean verifiySignature() {
        if(this.signature == null && this.fromAdress == null) return true;
        String data = StringUtil.getStringFromKey(fromAdress) + StringUtil.getStringFromKey(toAdress) + Float.toString(amount);
        return StringUtil.verifyECDSASig(fromAdress, data, signature);
    }

}
