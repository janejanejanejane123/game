package com.ruoyi.member.util;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.Assert;
import com.ruoyi.common.utils.CheckPatternUtils;
import com.ruoyi.common.utils.SpringContextUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.member.service.ITUserConfigService;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Map;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/7/5,13:20
 * @return:
 **/
@Slf4j
public class ConfigCheckImpl implements ConstraintValidator<ConfigCheck,String> {

    private String confKey;

    @Override
    public void initialize(ConfigCheck configCheck){
        String confKey = configCheck.confKey();
        log.info("初始化：{}",confKey);
        this.confKey = confKey;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isValid(String arg, ConstraintValidatorContext constraintValidatorContext) {
        ITUserConfigService bean = SpringContextUtil.getBean(ITUserConfigService.class);
        Assert.test(bean==null,"system.error");
        AjaxResult ajaxResult = bean.queryRegisteredConfig();
        List<Map<String,Object>> data = (List<Map<String,Object>>)ajaxResult.get("data");
        if (data!=null&&data.size()>0){
            for (Map<String, Object> datum : data) {
                String extra = Convert.toStr(datum.get("extra"));
                if (this.confKey.equals(datum.get("confkey"))){
                    if (datum.get("open").equals((int)Constants.OPEN)){
                        if (datum.get("need").equals((int)Constants.OPEN)){
                            if (arg==null||"".equals(arg)){
                                return false;
                            }
                        }
                        if (!StringUtils.isBlank(extra)){
                            if (arg!=null&&!"".equals(arg)){
                                return CheckPatternUtils.match(arg,extra);
                            }
                        }
                    }else {
                        Assert.test(StringUtils.isNotBlank(arg),"system.error");
                    }
                };
            }
        }
        return true;
    }
}
