package com.ruoyi.common.cache;

import com.ruoyi.common.annotation.Overtime;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.util.Stack;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/9,12:26
 * @return:
 **/
@Slf4j
public class OvertimeContext {

    private static final ThreadLocal<Stack<Overtime>> STACK_HOLDER =new ThreadLocal<>();

    static Overtime getCurrentOvertime(){
        Stack stack;
        if ((stack=STACK_HOLDER.get())!=null&&stack.size()!=0){
            return STACK_HOLDER.get().peek();
        }else {
            return null;
        }
    }


    public static void popStack(Stack<Overtime> stack) {
        if (stack!=null){
            if (stack.size()!=0){
                stack.pop();
            }
            if (stack.size()==0){
                OvertimeContext.STACK_HOLDER.remove();
            }
        }
    }

    public static Stack<Overtime> getStack(){
        Stack<Overtime> overtimes = OvertimeContext.STACK_HOLDER.get();
        if (overtimes==null){
            overtimes=new Stack<>();
            OvertimeContext.STACK_HOLDER.set(overtimes);
        }

        return overtimes;
    }
}
