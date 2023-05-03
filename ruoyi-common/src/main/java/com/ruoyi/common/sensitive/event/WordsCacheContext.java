package com.ruoyi.common.sensitive.event;

import com.ruoyi.common.sensitive.cache.JvmWordsCache;
import com.ruoyi.common.sensitive.cache.RedisWordsCache;
import lombok.extern.slf4j.Slf4j;

import java.util.Enumeration;
import java.util.Vector;

/**
 * @Description: 目标事件源对象上下文
 */
@Slf4j
public final class WordsCacheContext {
    private Vector<CacheChangedListener> listeners = new Vector<CacheChangedListener>();
    private static WordsCacheContext INSTANCE;

    public static final WordsCacheContext getInstance(boolean isInitJvmCache) {
        if(INSTANCE == null){
            synchronized (WordsCacheContext.class){
                if(INSTANCE == null){
                    INSTANCE = new WordsCacheContext(isInitJvmCache);
                }
            }
        }
        return INSTANCE;
    }

    private WordsCacheContext(boolean isInitJvmCache) {

        try {
            register(RedisWordsCache.getInstance());
            if(isInitJvmCache) {
                register(JvmWordsCache.getInstance());
            }
        } catch (Exception e) {
            log.error("[WordsCacheContext.Constructor] register Cache error", e);
            throw e;
        }
    }

    /**
     * @Description: 注册事件
     * @param listener
     * @return void
     */
    public void register(CacheChangedListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    /**
     * @Description: 变更事件
     * @param event 事件类型枚举
     * @return void
     */
    public void dispatchChanged(String identifier, CacheChangedEvent event) {
        event.doEvent();
        Enumeration<CacheChangedListener> enums = listeners.elements();
        while (enums.hasMoreElements()) {
            CacheChangedListener listener = enums.nextElement();
            log.info("trigger event：{}，perform monitoring business：{}，data：{}", event.getAction(), listener.getListenerName(), event.getSource());
            listener.handleChangedEvent(identifier,event);
        }
    }

    public interface CacheChangedListener extends java.util.EventListener {
        /**
         * @Description:  缓存改变事件
         * @param event 事件类型枚举
         * @return
         */
        void handleChangedEvent(String identifier, CacheChangedEvent event);
        /**
         * @Description: 获取监听名字
         * @return String
         */
        String getListenerName();
    }
}
