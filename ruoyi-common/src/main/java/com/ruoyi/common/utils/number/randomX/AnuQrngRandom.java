package com.ruoyi.common.utils.number.randomX;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class AnuQrngRandom {
    private static final String JSON_URL = "https://qrng.anu.edu.au/API/jsonI.php?length=1024&type=hex16&size=8";
    private final static Logger logger = LoggerFactory.getLogger(AnuQrngRandom.class);
    public static final double UPPER_BOUND = Math.pow(2, 64);

    private final ReentrantLock longLock;
    private final DefaultHttpClient httpClient;

    private int curPosition = 0;
    private List<String> hexCache = null;

    public AnuQrngRandom() {
        longLock = new ReentrantLock();
        httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter("http.socket.timeout", 5000);
    }

    /**
     * @return An integer in the range of [0, range - 1], or else -1. Range should be > 0 and < 2^64
     */
    public long nextLong(long range) {
        if (range <= 0) {
            throw new RuntimeException(String.format("Range out of bound, should be 0 < range < %s", UPPER_BOUND));
        }


        String hexString = null;
        longLock.lock();
        try {

            // load hex blocks into the cache if it is exhausted.
            int tryCounter = 3;
            while (isExhausted() && tryCounter-- > 0) {
                hexCache = fetchRandomHexStrings();
                curPosition = 0;
            }

            if (isExhausted()) {
                throw new RuntimeException(String.format("Unable to get a random hex data"));
            }

            hexString = hexCache.get(curPosition++);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            longLock.unlock();
        }

        if (stringLengthIs(hexString, 16)) {
            // returns the decimal value for the hex string
            return (long) (hexVal(hexString) * (range / UPPER_BOUND));
        }

        return -1;
    }

    /**
     * Fetch some random hex data block from qrng.anu.edu.au
     *
     * @return A list of hex data strings of each 16 characters long.
     */
    public List<String> fetchRandomHexStrings() throws IOException {
        String url = urlCacheBusted(JSON_URL);
        logger.info(String.format("%s - Fetching from %s", new Date(), url));
        // get a block of 1024 * 16 hex digits
        HttpGet req = new HttpGet(url);
        String data = null;
        try {
            HttpResponse res = httpClient.execute(req);
            if (res.getStatusLine().getStatusCode() / 200 == 1) {
                data = EntityUtils.toString(res.getEntity());
            }
        }
        finally {
            req.releaseConnection();
        }

        data = trim(data);
        return isNotEmpty(data) ? parseJsonObject(data) : new ArrayList<String>();
    }

    /**
     * Parses the Json data and returns only the hex strings as a list.
     */
    private List<String> parseJsonObject(String data) {
        JSONObject json =JSON.parseObject(data);
        JSONArray array = json.getJSONArray("data");
        ArrayList<String> list = null;
        if(array != null) {
            int arraySize = array.size();
            list = new ArrayList<String>(arraySize);
            for (int i = 0; i < arraySize; i++) {
                list.add(array.getString(i));
            }
        }
        return CollectionUtils.isNotEmpty(list) ? list : new ArrayList<>(0);
    }

    /**
     * Returns the decimal value of the given hex string.
     */
    public static double hexVal(String hexString) {
        int len = hexString.length();
        double val = 0;
        for (int i = 0; i < len; i++) {
            val += Character.digit(hexString.charAt(i), 16) * Math.pow(2, (len - i - 1) * 4);
        }
        return val;
    }

    /**
     * Adds a cache busting parameter into the url in to form of _=1392069009277
     */
    private static String urlCacheBusted(String url) {
        String pattern = url.indexOf('?') < 0 ? "%s?_=%s" : "%s&_=%s";
        return String.format(pattern, url, System.currentTimeMillis());
    }

    private boolean isExhausted() {
        return hexCache == null || curPosition >= hexCache.size();
    }

    private static boolean isNotEmpty(String s) {
        return s != null && s.length() > 0;
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static boolean stringLengthIs(String s, int length) {
        return s != null && s.length() == length;
    }

    public static void main(String[] args) {
        AnuQrngRandom qr = new AnuQrngRandom();
        for (int i = 0; i < 200; i++) {
            System.out.println(qr.nextLong(1000));
        }

    }
}