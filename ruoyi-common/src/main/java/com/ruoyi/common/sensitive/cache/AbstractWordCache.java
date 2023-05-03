package com.ruoyi.common.sensitive.cache;


import com.ruoyi.common.sensitive.SensitiveWord;
import com.ruoyi.common.sensitive.SensitiveWordSeeker;
import com.ruoyi.common.sensitive.event.CacheChangedEvent;
import com.ruoyi.common.sensitive.event.WordsCacheContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;


/**
 * @Description: 抽象缓存接口定义
 * @return
 */
@Slf4j
public abstract class AbstractWordCache implements WordsCache, WordsCacheContext.CacheChangedListener {

    private String listenerName;

    public AbstractWordCache(String listenerName) {
        this.listenerName = listenerName;
    }

    public String getListenerName() {
        return listenerName;
    }

    @Override
    public void setDataSource(Object dataSource) {
        if(log.isDebugEnabled()) {
            log.debug("{}: bindDataSource: {}", listenerName, dataSource);
        }
    }

    @Override
    public boolean init()  {
        if(log.isDebugEnabled()) {
            log.debug("{}: init word cache", listenerName);
        }
        return true;
    }

    @Override
    public boolean put(String identifier, SensitiveWord words)  {
        if(log.isDebugEnabled()) {
            log.debug("{}: put word: {},identifier is {}", listenerName, words, identifier);
        }
        return true;
    }

    @Override
    public boolean put(String identifier, Set<SensitiveWord> words)  {
        if(log.isDebugEnabled()) {
            log.debug("{}: put word list: {},identifier is {}", listenerName, words, identifier);
        }
        return true;
    }

    @Override
    public SensitiveWordSeeker get(String identifier)  {
        if(log.isDebugEnabled()) {
            log.debug("{}: get word list,identifier is {}", listenerName, identifier);
        }
        return null;
    }

    @Override
    public boolean update(String identifier, SensitiveWord word)  {
        if(log.isDebugEnabled()) {
            log.debug("{}: update word: {},identifier is {}", listenerName, word, identifier);
        }
        return true;
    }

    @Override
    public boolean remove(String identifier, SensitiveWord words)  {
        if(log.isDebugEnabled()) {
            log.debug("{}: remove word: {},identifier is {}", listenerName, words, identifier);
        }
        return false;
    }

    @Override
    public boolean remove(String identifier, Set<SensitiveWord> wordsSet){
        if(log.isDebugEnabled()) {
            log.debug("{}: remove word Set: {},identifier is {}", listenerName, wordsSet, identifier);
        }
        return false;
    }

    @Override
    public boolean refreshAll()  {
        if(log.isDebugEnabled()) {
            log.debug("{}: refreshAll word cache is {}", listenerName);
        }
        return false;
    }

    @Override
    public SensitiveWordSeeker refresh(String identifier){
        if(log.isDebugEnabled()) {
            log.debug("{}: refresh word cache,identifier is {}", listenerName, identifier);
        }
        return null;
    }

    @Override
    public String getNextId(String identifier){
        if(log.isDebugEnabled()) {
            log.debug("{}: getNextId,identifier is {}", listenerName, identifier);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleChangedEvent(String identifier, CacheChangedEvent event){

        this.init();

        switch (event.getAction()) {

            case PUT:
                this.put(identifier,(SensitiveWord) event.getSource());
                break;

            case PUT_LIST:
                this.put(identifier, (Set<SensitiveWord>) event.getSource());
                break;

            case REMOVE:
                this.remove(identifier, (SensitiveWord) event.getSource());
                break;

            case UPDATE:
                this.update(identifier, (SensitiveWord) event.getSource());
                break;

            case REFRESH:
                this.refresh(identifier);
                break;
            case REFRESH_ALL:
                this.refreshAll();
                break;

            default:
                throw new UnsupportedOperationException();
        }
    }
}
