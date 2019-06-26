package com.zkmeiling.serialport.model;

import java.util.Arrays;

/**
 * @author: chenl
 * @data: 2018/10/30
 * @description:
 */
public class FingerReceived {

    private int type;
    private int reply;
    private int length;
    private int sum;
    private int xor;
    private byte[] content;
    private int result;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getXor() {
        return xor;
    }

    public void setXor(int xor) {
        this.xor = xor;
    }

    @Override
    public String toString() {
        return "FingerReceived{" +
                "type=" + type +
                ", reply=" + reply +
                ", length=" + length +
                ", sum=" + sum +
                ", xor=" + xor +
                ", content=" + Arrays.toString(content) +
                '}';
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
