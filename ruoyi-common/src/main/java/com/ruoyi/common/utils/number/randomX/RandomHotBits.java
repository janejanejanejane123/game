package com.ruoyi.common.utils.number.randomX;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Stack;

/**
    <p>
    Implementation of a <b>randomX</b>-compliant class which obtains
    genuine random data from <a href="http://www.fourmilab.ch/">John
    Walker</a>'s <a href="http://www.fourmilab.ch/hotbits/">HotBits</a>
    radioactive decay random sequence generator.
    </p>

    <p>
    Designed and implemented in July 1996 by
    <a href="http://www.fourmilab.ch/">John Walker</a>.
    </p>
*/

public class RandomHotBits extends RandomX {
    private final static Logger logger = LoggerFactory.getLogger(RandomHotBits.class);
    private volatile long lastPullTime = System.currentTimeMillis();
    private Stack<Byte> stack = new Stack();
    String apiKey = "Pseudorandom";


    //  Constructors

    /** Creates a new random sequence generator.  An API key of
        "Pseudorandom" will be used, requesting pseudorandom data
        from the HotBits server. */

    public RandomHotBits() {
    }

    /** Creates a new random sequence generator using the specified
        API key to request radioactively-generated random data from
        the HotBits generator.

@param apikey API Key to request HotBits data
    */

    public RandomHotBits(String apikey) {
        this();
        apiKey = apikey;
    }

    /*  Private method to fill buffer from HotBits server.  */

    private synchronized void fillBuffer()
        throws IOException
    {
        logger.info("pull hotBits data,HotBits size is {}",stack.size());
        Byte[] array = getBytesSafe(getApiUrl());
        this.lastPullTime = System.currentTimeMillis();
        for (int i = 0; i < array.length; i++) {
            stack.push(array[i]);
        }
        logger.info("pull hotBits data finish,Spend time:{},pull data Size:{},HotBits size is {}",System.currentTimeMillis() - lastPullTime,array.length,stack.size());
    }

    public String getApiUrl(){
        return "https://www.fourmilab.ch/cgi-bin/Hotbits?nbytes=128&fmt=bin&apikey=" + apiKey;
    }

    /** Get next byte from generator.
        @return the next byte from the generator.
    */

    public byte nextByte()  {
        try {
            logger.info("nextByte HotBits size is {}",stack.size());
            synchronized (stack) {
                if (stack.empty()) {
                    fillBuffer();
                }
                return stack.pop();
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot obtain HotBits", e);
        }
    }

    public int getDataSize(){
        return stack.size();
    }
};
