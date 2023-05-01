package com.ruoyi.common.utils.hash;

import java.util.Date;

public class Block {
    private String hash;
    private String prehash;
    private String data;
    private long timestamp;
    private int nonce;

    public Block(String prehash, String data) {
        this.prehash = prehash;
        this.data = data;
        this.timestamp = new Date().getTime();
        this.hash = setHash();
        this.nonce = 0;

    }

    public String setHash() {
        String sha256 = null;
        try {
            sha256 = Sha256Utils.getSha256(prehash + data + timestamp + Integer.toString(nonce));

        } catch (Exception e) {
            System.out.println("区块计算hash出现错误");
        }
        return sha256;

    }

    public String getHash() {
        return hash;
    }

    public void mine(int targetBits) {
        String target = new String(new char[targetBits]).replace('\0', '0'); //Create a string with difficulty * "0"
        while (!hash.substring(0, targetBits).equals(target)) {
            nonce++;
            hash = setHash();
        }

    }

    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }



    @Override
    public String toString() {
        return "Block{" +
                "hash='" + hash + '\'' +
                ", prehash='" + prehash + '\'' +
                ", data='" + data + '\'' +
                ", timestamp=" + timestamp +
                ", nonce=" + nonce +
                '}';
    }


    public static void main(String[] args) {
        Block block = new Block("a001","343254234234");
        String hash = block.setHash();
        String fdsfs234r32 = Block.str2HexStr("fdsfs234r32");


        System.out.println(hash);
        System.out.println(fdsfs234r32);
    }
}