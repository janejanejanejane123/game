package com.ruoyi.chatroom.service.impl;

import com.ruoyi.chatroom.domain.ChatSensitiveWord;
import com.ruoyi.chatroom.service.IChatSensitiveWordService;
import com.ruoyi.chatroom.service.SensitiveSerivce;
import com.ruoyi.common.constant.CacheConstant;
import com.ruoyi.common.sensitive.SensitiveWord;
import com.ruoyi.common.sensitive.service.SensitiveWordRepository;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.autoId.SnowflakeIdUtils;
import com.ruoyi.common.utils.clazz.ClassUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/15,16:30
 * @return:
 **/
@Service("sensitiveService")
public class SensitiveServiceImpl implements SensitiveSerivce, SensitiveWordRepository {

    @Resource
    private IChatSensitiveWordService chatSensitiveWordService;

    @Resource
    private SnowflakeIdUtils snowflakeIdUtils;

    @Override
    public int add(String identifier, SensitiveWord entity, Object parameter) {
        ChatSensitiveWord sensitiveWord = (ChatSensitiveWord) parameter;
        return chatSensitiveWordService.insertChatSensitiveWord(sensitiveWord);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int batch(String identifier, Set<SensitiveWord> entity, Object parameter) {
        chatSensitiveWordService.batchInsertRecords((List<ChatSensitiveWord>) parameter);
        return 0;
    }

    @Override
    public int edit(String identifier, SensitiveWord entity, Object parameter) {
        ChatSensitiveWord chatSensitiveWord = (ChatSensitiveWord) parameter;
        return chatSensitiveWordService.updateChatSensitiveWord(chatSensitiveWord);
    }

    @Override
    public int remove(String identifier, SensitiveWord entity, Object parameter) {
        ChatSensitiveWord chatSensitiveWord = (ChatSensitiveWord) parameter;

        return chatSensitiveWordService.deleteChatSensitiveWordById(chatSensitiveWord.getId());
    }

    @Override
    public int updateStatus(String identifier, SensitiveWord entity, Object parameter) {
        ChatSensitiveWord chatSensitiveWord = (ChatSensitiveWord) parameter;

        return chatSensitiveWordService.updateChatSensitiveWord(chatSensitiveWord);
    }

    @Override
    public int count(String identifier) {
        ChatSensitiveWord chatSensitiveWord = new ChatSensitiveWord();
        chatSensitiveWord.setStatus("1");
        return chatSensitiveWordService.count(chatSensitiveWord);
    }

    @Override
    @Async("taskExecutor")
    public Future<List<SensitiveWord>> query(String identifier, long pageSize, long begin) {
        return new AsyncResult<>(querySensitiveWord(identifier,pageSize,begin));
    }


    public List<SensitiveWord> querySensitiveWord(String identifier, long pageSize, long begin) {

        List<SensitiveWord> result = new ArrayList<>();
        PageUtils.startPage(pageSize,begin);
        ChatSensitiveWord chatSe = new ChatSensitiveWord();
        chatSe.setStatus("1");
        List<ChatSensitiveWord> chatSensitiveWords = chatSensitiveWordService.selectChatSensitiveWordList(chatSe);
        for (ChatSensitiveWord chatSensitiveWord : chatSensitiveWords) {
            //添加到集合中.
            result.add(switchObject(chatSensitiveWord));
        }
        return result;
    }


    private SensitiveWord switchObject(ChatSensitiveWord chatSensitiveWord){
        SensitiveWord sensitiveWord = new SensitiveWord();
        sensitiveWord.setId(chatSensitiveWord.getId());
        String strWord = chatSensitiveWord.getSensitiveWord();
        sensitiveWord.setWord(strWord);
        sensitiveWord.setWordLength(strWord.length());
        sensitiveWord.setReplaceStr(chatSensitiveWord.getReplaceStr());
        sensitiveWord.setPre(chatSensitiveWord.getPrefix());
        sensitiveWord.setPrefixLength(chatSensitiveWord.getPrefixLength());
        sensitiveWord.setSufix(chatSensitiveWord.getSuffix());
        sensitiveWord.setSuffixLength(chatSensitiveWord.getSuffixLength());
        sensitiveWord.setFilterType(ClassUtil.formatObject(chatSensitiveWord.getFilterType(),true,Byte.class));
        sensitiveWord.setType(ClassUtil.formatObject(chatSensitiveWord.getStrType(),true,Byte.class));
        sensitiveWord.setUseType(ClassUtil.formatObject(chatSensitiveWord.getUseType(),true,Byte.class));
        return sensitiveWord;
    }

    @Override
    public List<String> getAllIdentifier() {
        List<String> strings = new ArrayList<>(1);
        strings.add(CacheConstant.SENSITIVE_WORD_IDENTIFIER);
        return strings;
    }

    @Override
    public SensitiveWord get(String identifier, SensitiveWord word) {
        ChatSensitiveWord chatSensitiveWord = new ChatSensitiveWord();
        if (word!=null){
            chatSensitiveWord.setStatus("1");
            chatSensitiveWord.setUseType(word.getUseType()+"");
            chatSensitiveWord.setSensitiveWord(word.getWord());
            List<ChatSensitiveWord> chatSensitiveWords = chatSensitiveWordService.selectChatSensitiveWordList(chatSensitiveWord);
            if (chatSensitiveWords!=null&&chatSensitiveWords.size()>0){
                ChatSensitiveWord sensitiveStr = chatSensitiveWords.get(0);
                return switchObject(sensitiveStr);
            }else {
                return null;
            }
        }
        return null;
    }
}
