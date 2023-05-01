package com.ruoyi.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * @description: 系统工具类
 **/
public class SystemUtil {
    private static final Logger logger = LoggerFactory.getLogger(SystemUtil.class);
    private static final List<String> startParameters = ManagementFactory.getRuntimeMXBean().getInputArguments();
    private static boolean isDebug = false;
    static {
        for(String parameter : startParameters){
            if("-Ddebug".equals(parameter) || parameter.endsWith("debugger-agent.jar")){
                isDebug = true;
            }
        }

    }

    public static List<String> getStartParameters() {
        List<String> result = new ArrayList<>();
        result.addAll(startParameters);
        return result;
    }

    public static boolean isDebug(){
        return isDebug;
    }

    public static String jstack() {
        return jstack(Thread.getAllStackTraces());
    }

    public static String jstack(Map<Thread, StackTraceElement[]> map) {
        StringBuilder result = new StringBuilder();
        try {
            Iterator<Map.Entry<Thread, StackTraceElement[]>> ite = map.entrySet().iterator();
            while (ite.hasNext()) {
                Map.Entry<Thread, StackTraceElement[]> entry = ite.next();
                StackTraceElement[] elements = entry.getValue();
                Thread thread = entry.getKey();
                if (elements != null && elements.length > 0) {
                    String threadName = entry.getKey().getName();
                    result.append(String.format("%-40sTID: %d STATE: %s%n", threadName, thread.getId(), thread.getState()));
                    for (StackTraceElement el : elements) {
                        result.append(String.format("%-40s%s%n", threadName, el.toString()));
                    }
                    result.append("\n");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return result.toString();
    }

    /**
     * @param src
     * @return byte[]
     * @Description: 解压数据
     */
    public static byte[] uncompress(final byte[] src) throws IOException {
        byte[] result = src;
        byte[] uncompressData = new byte[src.length];
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(src);
        InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(src.length);

        try {
            while (true) {
                int len = inflaterInputStream.read(uncompressData, 0, uncompressData.length);
                if (len <= 0) {
                    break;
                }
                byteArrayOutputStream.write(uncompressData, 0, len);
            }
            byteArrayOutputStream.flush();
            result = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                byteArrayInputStream.close();
            } catch (IOException e) {
                logger.error("Failed to close the stream", e);
            }
            try {
                inflaterInputStream.close();
            } catch (IOException e) {
                logger.error("Failed to close the stream", e);
            }
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
               throw  e;
            }
        }

        return result;
    }

    /**
     * @param src   元数据
     * @param level 压缩级别
     * @return byte[]
     * @Description: 压缩数据
     */
    public static byte[] compress(final byte[] src, final int level) throws IOException {
        byte[] result = src;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(src.length);
        java.util.zip.Deflater defeater = new java.util.zip.Deflater(level);
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, defeater);
        try {
            deflaterOutputStream.write(src);
            deflaterOutputStream.finish();
            deflaterOutputStream.close();
            result = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            defeater.end();
            throw e;
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException ignored) {
                throw ignored;
            }

            defeater.end();
        }
        return result;
    }

    public static int getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName(); // format: "pid@hostname"
        try {
            return Integer.parseInt(name.substring(0, name.indexOf('@')));
        } catch (Exception e) {
            return -1;
        }
    }

    public static void sleep(long sleepMs) {
        if (sleepMs < 0) {
            return;
        }
        try {
            Thread.sleep(sleepMs);
        } catch (Throwable ignored) {
        }
    }

    public static String currentStackTrace() {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement ste : stackTrace) {
            sb.append("\n\t");
            sb.append(ste.toString());
        }

        return sb.toString();
    }

    public static boolean checkCRC(byte[] bytesContent, int bodyLen, int bodyCRC) {
        int crc = crc32(bytesContent, 0, bodyLen);
        if (crc != bodyCRC) {
            return false;
        }
        return true;
    }

    public static int crc32(byte[] array) {
        if (array != null) {
            return crc32(array, 0, array.length);
        }
        return 0;
    }

    public static int crc32(byte[] array, int offset, int length) {
        CRC32 crc32 = new CRC32();
        crc32.update(array, offset, length);
        return (int) (crc32.getValue() & 0x7FFFFFFF);
    }

    /*public static void main(String[] args) throws ExecutionException, InterruptedException {
        long bigin = System.currentTimeMillis();
        //使用阻塞队列来获取结果。
        LinkedBlockingQueue<Fiber<Integer>> fiberQueue = new LinkedBlockingQueue<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < 1; i++) {
            int finalI = i+1;
            //这里的Fiber有点像Callable,可以返回数据
            Fiber<Integer> fiber = new Fiber<>((SuspendableCallable<Integer>) () -> {
                //这里用于测试内存占用量
                Fiber.sleep(1000+(new Random().nextInt(finalI*1000) ));
                System.out.println("in-" + finalI + "-" + LocalDateTime.now().format(formatter));
//                getData();
                return finalI;
            });
            //开始执行
            fiber.start();
            //加入队列
            fiberQueue.add(fiber);
        }
        while (true) {
            //阻塞
            Fiber<Integer> fiber = fiberQueue.take();
            System.out.println("out-" + fiber.get() + "-" + LocalDateTime.now().format(formatter));
        }
//        System.out.println("结束时间"+(System.currentTimeMillis() - bigin));
    }
    @Suspendable
    public static Object  getData() throws InterruptedException {
        String url = "http://localhost:8080/api/service/data";
        Thread.sleep(23232232332L);
        return null;
    }*/
}
