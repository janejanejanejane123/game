package com.ruoyi.common.utils.number;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.number.randomX.QuantumRandom;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

/**
 * @description: 随机数生成器
 **/
public class RandomUtil {
    private final static Logger logger = LoggerFactory.getLogger(RandomUtil.class);
    private final static QuantumRandom quantumRandom;
    private final static SecureRandom masterRandom;
    private final static SecureRandom slaveRandom;
    private final static int quantumFileMinSize = 2048;
    private static final double DOUBLE_UNIT = 0x1.0p-53; // 1.0 / (1L << 53)
    private static FileChannel fileChannel;

    static {
        try {
            String randomSouce = System.getenv("QUANTUM_RANDOM_FILE");
            if (StringUtils.isNotBlank(randomSouce)) {
                File randomFile = new File(randomSouce);
                logger.info("[RandomUtil.static] init randomSouce:{},file exists:{}",randomSouce,randomFile.exists());
                if (randomFile.exists())
                    fileChannel = new RandomAccessFile(randomFile, "r").getChannel();
                if (fileChannel.size() < quantumFileMinSize) {//量子随机文件不能小于2kb,否者容易造成随机数错误
                    fileChannel = null;
                    throw new ServiceException( "quantum random number file size is less than " + quantumFileMinSize + " bytes!!!");
                }
            }
        } catch (Exception e) {
            logger.error("[RandomUtil.static] init RandomUtil erro", e);
        }
        SecureRandom randomTemp;
        try {
            randomTemp = SecureRandom.getInstanceStrong(); // 获取高强度安全随机数生成器
        } catch (NoSuchAlgorithmException e) {
            randomTemp = new SecureRandom(); // 获取普通的安全随机数生成器
        }
        slaveRandom = new SecureRandom();
        masterRandom = randomTemp;
        quantumRandom = new QuantumRandom(randomTemp);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    if (fileChannel != null) {
                        fileChannel.close();
                    }
                    quantumRandom.shutdown();
                } catch (Exception e) {
                    logger.error("[RandomUtil.static] shutdown random jvm error");
                }
            }
        });
    }


    /**
     * @param num        行数
     * @param rowLen     列数
     * @param seedLength 种子数长度
     * @return List<List                                                               <                                                               Integer>>
     * @Description: 生成一个随机数矩阵
     */
    private static List<List<Integer>> generateCakes(int num, int seedLength, int rowLen, int bound) {
        int counter = 0;
        // 実際ループの回数を記録
        int realCount = 0;
        // 生成する組数を制御するためのカウンター
        int tmprows = 0;
        // 返すためのList
        List<List<Integer>> CakesList = new ArrayList<List<Integer>>();
        while (num > tmprows) {
            List<Integer> list = new ArrayList<Integer>();
            while (counter < rowLen) {

                int cake = generateCakes(seedLength, bound); //随机生成0-37的数字
                if (!list.contains(cake) && 0 != cake) {
                    list.add(cake);
                    counter++;
                }
                realCount++;
            }
            Collections.sort(list);
            tmprows++;
            counter = 0;
            CakesList.add(list);
        }
        return CakesList;
    }

    public static Integer generateCakes(int seedLength, int bound) {
        return generateCakes(seedLength, bound, 50);
    }

    public static Integer generateCakes(int seedLength, int bound, int quantumPercentage) {
        if (nextBoolean() && quantumRandom.getDataSize() > 4 && quantumPercentage != 0) {
            int random = nextInt(100) + 1;
            if (random <= quantumPercentage) {//百分之22机率使用在线随机数
                return quantumRandom.getQuantumRandom(bound);
            }
        }
        return nextInt(bound)
;    }


    /**
     * 生成一个随机数
     *
     * @param seedLength 种子数长度
     * @param bound      范围
     */
/*    public static Integer generateCakes(int seedLength, int bound, int quantumPercentage) {
        // シードを生成する
        byte[] seeds = getRandomSeed(seedLength); //获取随机的byte数组，用来后续作为种子
        return generateCakes(seeds, bound, quantumPercentage);
    }*/


    /**
     * 生成一个随机数
     *
     * @param seeds 随机种子
     * @param bound      范围
     */
    public static Integer generateCakes(byte[] seeds,  int bound, int quantumPercentage) {
        if (false && quantumRandom.getDataSize() > 4 && quantumPercentage != 0) {
            logger.info("[RandomUtil.generateCakes] quantum random number size is {},bound:{},quantumPercentage:{}", quantumRandom.getDataSize(), bound, quantumPercentage);
            int random = quantumPercentage == 100 ? 0 : masterRandom.nextInt(100) + 1;
            if (random <= quantumPercentage) {
                return quantumRandom.getQuantumRandom(bound);
            } else {
                logger.info("[RandomUtil.generateCakes] not use quantum random number,random:{},quantumPercentage:{}", random, quantumPercentage);
            }
            if (nextBoolean()) {
                return quantumRandom.getQuantumRandom(bound);
            }
        }
        logger.info("[RandomUtil.generateCakes] use local random,bound:{},quantumPercentage:{}", bound, quantumPercentage);
        SecureRandom currentRandom;
//        if(masterRandom.nextBoolean()){
            currentRandom = masterRandom;
//        }else{
//            currentRandom = slaveRandom;
//        }
        // 返すためのList
        currentRandom.setSeed(seeds); //设置种子
        masterRandom.setSeed(seeds);
        //偶尔负数
        int cake = currentRandom.nextInt(bound < 0 ? bound * -1 : bound); //随机生成0-37的数字
        /*if (fileChannel != null && masterRandom.nextBoolean()) {
            try {
                byte data[] = fileReadRandom(currentRandom,4);
                int result = (((data[3]) << 24) | ((data[2] & 0xff) << 16) | ((data[1] & 0xff) << 8) | ((data[0] & 0xff)));

                if(masterRandom.nextBoolean()) {
                    logger.info("[RandomUtil.generateCakes] use local random case A,bound:{}", bound);
                    return (int) (Math.abs(result + cake) % bound);
                }else{
                    logger.info("[RandomUtil.generateCakes] use local random case B,bound:{}", bound);
                    return (int) (Math.abs(result) % bound);
                }
            } catch (Exception e) {
                logger.error("[RandomUtil.generateCakes] fileReadRandom error", e);
            }
        }*/
        masterRandom.nextBytes(seeds);
        slaveRandom.nextBytes(seeds);
        return cake;
    }
    final protected static int next(int numBits) throws Exception {
        int numBytes = (numBits+7)/8;
        byte b[] = fileReadRandom(numBytes);
        int next = 0;


        for (int i = 0; i < numBytes; i++) {
            next = (next << 8) + (b[i] & 0xFF);
        }

        return next >>> (numBytes*8 - numBits);
    }

    public static int nextInt(int bound){
        if (bound <= 0)
            throw new ServiceException("bound Less than or equal to 0");
        if(fileChannel != null) {
            try {
                int r = next(31);
                int m = bound - 1;
                if ((bound & m) == 0)  // i.e., bound is a power of 2
                    r = (int) ((bound * (long) r) >> 31);
                else {
                    for (int u = r;
                         u - (r = u % bound) + m < 0;
                         u = next(31))
                        ;
                }
                return r;
            } catch (Exception e) {
                return masterRandom.nextInt(bound);
            }
        }else {
            return masterRandom.nextInt(bound);
        }
    }

    public static float nextFloat() {
        if(fileChannel != null) {
            try {
            return next(24) / ((float) (1 << 24));
            } catch (Exception e) {
                return masterRandom.nextFloat();
            }
        }else {
            return masterRandom.nextFloat();
        }
    }

    public static double nextDouble() {
        if(fileChannel != null) {
            try {
                return (((long) (next(26)) << 27) + next(27)) * DOUBLE_UNIT;
            } catch (Exception e) {
                return masterRandom.nextDouble();
            }
        }else{
            return masterRandom.nextDouble();
        }
    }

    public static void nextBytes(byte[] bytes) throws Exception {
        for (int i = 0, len = bytes.length; i < len; )
            for (int rnd = next(32),
                 n = Math.min(len - i, Integer.SIZE/Byte.SIZE);
                 n-- > 0; rnd >>= Byte.SIZE)
                bytes[i++] = (byte)rnd;
    }

    public static byte[] fileReadRandom( int size) throws Exception {
        long fileSize = fileChannel.size();
        if (fileSize < quantumFileMinSize) {//量子随机文件不能小于2kb,否者容易造成随机数错误
            fileChannel = null;
            throw new ServiceException("quantum random number file size is less than " + quantumFileMinSize + " bytes!!!");
        }
        long offset = nextLong(masterRandom,fileSize - size);
        return fileReadRandom(offset,size);
    }

    /**
     *
     * @param size 获取范围
     * @return int
     * @Description: 使用随机数文件作为熵, 建议每隔一段时间更好一次熵源, 且随机文件不要过小，不然生成随机数有可能会有问题
     */
    public static byte[] fileReadRandom(long offset, int size) throws Exception {
        byte[] data = new byte[size];
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, offset, size);
        for (int i = 0; i < size; i++) {
            data[i] = mappedByteBuffer.get();
        }
        return data;
    }


    public static long nextLong(SecureRandom currentRandom, long n) {
        // error checking and 2^x checking removed for simplicity.
        long bits, val;
        do {
            bits = (currentRandom.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1) < 0L);
        return val;
    }

    public static byte[] getRandomSeed(int numBytes){
        byte[] seeds = SecureRandom.getSeed(numBytes); //获取随机的byte数组，用来后续作为种子
        byte[] fileSeeds = null;
        try {
            fileSeeds = fileReadRandom(numBytes);
        } catch (Exception e) {}
        if(fileSeeds != null){
            if(masterRandom.nextBoolean()){
                return fileSeeds;
            }else{
                for (int i = 0; i < numBytes; i++) {
                    if(masterRandom.nextBoolean()){
                        seeds[i] = fileSeeds[i];
                    }
                }
            }
        }
        return seeds;
    }

    public static boolean nextBoolean() {
        if (fileChannel != null) {
            try {
                long fileSize = fileChannel.size();
                if (fileSize > quantumFileMinSize) {
                    return next(1) != 0;
                } else {
                    return masterRandom.nextBoolean();
                }
            } catch (Exception e) {
                return masterRandom.nextBoolean();
            }
        } else {
            return masterRandom.nextBoolean();
        }
    }


    public static void main(String[] args) throws Exception {
        FileChannel writeFileChannel = new RandomAccessFile(new File("D:\\randomTest.bin"), "rw").getChannel();
//        Thread.sleep(10000L);
        long start = System.currentTimeMillis();
        Map<Integer, Integer> map = new TreeMap<>();

        for (int a = 0; a < 188; a++) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                    // シードを生成する
                    byte[] seeds = new byte[4]; //获取随机的byte数组，用来后续作为种子
                    String data = "";
                    byte[]  writeData = new byte[88];
                    int index = 0;
                    StringBuilder sb = new StringBuilder(88);
                    for (int i = 0; i < 1000000; i++) {
                        int random = masterRandom.nextInt(100) + 1;
                        if (random <= 1) {//百分之10的机率更新随机种子
                            try {
                                byte[] seed = fileReadRandom(256);
                                masterRandom.setSeed(seed);
                            } catch (Exception e) {
                            }
                        }
                        nextBytes(writeData);
                        writeFile(writeFileChannel,writeData);
                    }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            Thread thread=new Thread(runnable);
            thread.start();
        }
        System.out.println("结束时间:" + (System.currentTimeMillis() - start));
  }


  private static synchronized void writeFile(FileChannel writeFileChannel,byte[] writeData) throws IOException {
      MappedByteBuffer mappedByteBuffer = writeFileChannel.map(FileChannel.MapMode.READ_WRITE, writeFileChannel.size(), writeData.length);
      mappedByteBuffer.put(writeData);
      mappedByteBuffer.force();
      writeFileChannel.force(true);
  }

}
