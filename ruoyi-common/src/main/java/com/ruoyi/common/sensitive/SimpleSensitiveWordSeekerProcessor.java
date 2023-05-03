package com.ruoyi.common.sensitive;




import com.ruoyi.common.sensitive.conf.Config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @Description: 简单的敏感词处理器，从配置文件读取敏感词初始化，
 *               使用者只需要在classpath放置sensitive-word.properties敏感词文件即可
 */
public class SimpleSensitiveWordSeekerProcessor extends SensitiveWordSeekerManage {

    private static volatile SimpleSensitiveWordSeekerProcessor instance;

    /**
     * 获取实例
     * 
     * @return
     */
    public static SimpleSensitiveWordSeekerProcessor newInstance() {
        if (null == instance) {
            synchronized (SimpleSensitiveWordSeekerProcessor.class) {
                if (null == instance) {
                    instance = new SimpleSensitiveWordSeekerProcessor();
                }
            }
        }
        return instance;
    }

    /**
     * 私有构造器
     */
    private SimpleSensitiveWordSeekerProcessor() {
        initialize();
    }

    /**
     * 初始化敏感词
     */
    private void initialize() {
        Map<String, String> map = Config.newInstance().getAll();
        Set<Entry<String, String>> entrySet = map.entrySet();

        Map<String, SensitiveWordSeeker> seekers = new HashMap<String, SensitiveWordSeeker>();
        Set<SensitiveWord> kws;

        for (Entry<String, String> entry : entrySet) {
            String[] words = entry.getValue().split(",");
            kws = new HashSet<SensitiveWord>();
            for (String word : words) {
                kws.add(new SensitiveWord(word));
            }
            seekers.put(entry.getKey(), new SensitiveWordSeeker(kws));
        }
        this.seekers.putAll(seekers);
    }
}
