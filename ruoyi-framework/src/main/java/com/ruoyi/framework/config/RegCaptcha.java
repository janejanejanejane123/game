package com.ruoyi.framework.config;

import com.google.code.kaptcha.text.impl.DefaultTextCreator;

import java.util.Random;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/3/30,13:41
 * @return:
 **/
public class RegCaptcha extends DefaultTextCreator {

    private static final char[] STR_ARRAY = "们的一沉了是冒我不在伦人金们有灰来他这上玉着个地到大里说去子得也和那要下看天时过出小么起你都把好还多没为又可家学只以主会样年想能生同老中从自面前头到它后然走很像见两用她国动进成回什边作对开而已些现山民候经发工向事命给长水几义三声于高正妈手知理眼志点心战二问但身方实吃做叫当住听革打呢真党全才四已所敌之最光产情路分总条白话东席次亲如被花口放儿常西气五第使写军吧文运在果怎定许快明行因别飞外树物活部门无往船望新带队先力完间却站代员机更九您每风级跟笑啊孩万少直意夜比阶连车重便斗马哪化太指变社似士者干石满决百原拿群究各六本思解立河爸村八难早论吗根共让相研今其书坐接应关信觉死步反处记将千找争领或师结块跑谁草越字加脚紧爱等习阵怕月青半火法题建赶位唱海七女任件感准张团屋爷离色脸片科倒睛利世病刚且由送切星晚表够整认响雪流未场该并底深刻平伟忙提确近亮轻讲农古黑告界拉名呀土清阳照办史改历转画造嘴此治北必服雨穿父内识验传业菜爬睡兴".toCharArray();
    private static final char[] DIGI_ARRAY= "0123456789".toCharArray();
    @Override
    public String getText() {
        Random random = new Random();
        int i0 = random.nextInt(STR_ARRAY.length);
        int i1 = random.nextInt(STR_ARRAY.length);
        int i2 = random.nextInt(STR_ARRAY.length);
        int i3 = random.nextInt(DIGI_ARRAY.length);
        int i4 = random.nextInt(DIGI_ARRAY.length);
        int i5 = random.nextInt(DIGI_ARRAY.length);

        return STR_ARRAY[i0] +"，"+
                STR_ARRAY[i1] + "，"+
                STR_ARRAY[i2] + "，"+
                DIGI_ARRAY[i3] + "，"+
                DIGI_ARRAY[i4] + "，"+
                DIGI_ARRAY[i5];
    }
}
