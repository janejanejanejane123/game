package com.ruoyi.common.core.redis;

import org.springframework.data.redis.core.script.RedisScript;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/21,19:25
 * @return:
 **/
public class RedisScriptStrings {

    /**
     * 如果存在则加1，并返回当前值， 如果不存在则返回-1
     */
    static final RedisScript EXIST_AND_INCR =RedisScript.
            of("local num= redis.call('exists',KEYS[1]);\n" +
            " if num == 0 \n" +
            " then \n" +
            "   return -1 \n" +
            " else \n" +
            "   return redis.call('incr',KEYS[1]) \n" +
            " end",Long.class );


    public static final RedisScript GET_AND_DEL =RedisScript.of( "local current = redis.call('get', KEYS[1]);\n" +
            "if (current) then\n" +
            "    redis.call('del', KEYS[1]);\n" +
            "end\n" +
            "return current;",String.class);


    /**
     * 比较并交换
     */
    static final RedisScript COMPARE_AND_SWAP = RedisScript.of("local current = redis.call(\"get\",KEYS[1]);\n"+
            // "return ARGV[1]";
            "if current == ARGV[1]\n"+
            "then\n"+
            "   redis.call('setRange',KEYS[1],0,ARGV[2])\n" +
            "   return 1\n" +
            "else\n" +
            "   return 0\n" +
            "end",Long.class);

    /**
     * 登录挤下线
     */
    static final RedisScript KICK_LAST_AFTER_LOGIN= RedisScript.of(
            "local curToken = redis.call( 'get', KEYS[1] );\n"+
            "if curToken ~= nil \n"+
            "then \n" +"" +
//            "   redis.call('del', ARGV[1]..curToken );\n"+
            "   redis.call('set', KEYS[1], ARGV[2]);\n"+
            "   return curToken;\n"+
            "else\n"+
            "   redis.call('set', KEYS[1], ARGV[2]);\n"+
            "   return nil;\n"+
            "end;",String.class);
            ;
    /**
     *  解redis锁
     */
    static final RedisScript REDIS_UNLOCK_LUA=RedisScript.of(
            "if redis.call(\"get\",KEYS[1]) == ARGV[1] \n"+
            "then \n"+
            "    return redis.call(\"del\",KEYS[1]); \n"+
            "else \n"+
            "    return 0; \n"+
            "end",
            Long.class);
    /**
     * 测试get
     */
    static final RedisScript GET_LUA=RedisScript.of(
            "return redis.call(\"get\",KEYS[1]) ;\n",
            String.class);



    static final RedisScript LOGOUT_LUA_SCRIPT=RedisScript.of(
            "redis.call(\"del\",KEYS[1]) ; \n"+
            "redis.call(\"lrem\",KEYS[2] ,0,ARGV[1]); \n");
}
