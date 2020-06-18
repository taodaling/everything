package com.daltao.reflection.cls;

import java.io.Serializable;
import java.util.Date;

public interface FansStatistics extends Serializable {
    /**
     * 日期
     *
     * @return
     */
    public Date getActualDate();

    /**
     * 用户群体
     *
     * @return
     */
    public String getUserType();

    /**
     * 来源
     *
     * @return
     */
    public String getFromCategory();

    /**
     * 公众号
     *
     * @return
     */
    public String getFromWx();

    /**
     * 关注人数
     *
     * @return
     */
    public Integer getSubscribeNum();

    /**
     * 当天激活人数
     *
     * @return
     */
    public Integer getDayActivationTotal();

    /**
     * 当天接触人数
     *
     * @return
     */
    public Integer getDayContactNum();


    /**
     * 当天咨询人数
     *
     * @return
     */
    public Integer getDayConsultClickNum();


    /**
     * 当天方案定制人数
     *
     * @return
     */
    public Integer getDaySchemeMakeNum();


    /**
     * 当天智能引导点击总数
     *
     * @return
     */
    public Integer getDayGuideClickNum();

    /**
     * 当天咨询+方案定制总数
     *
     * @return
     */
    public Integer getDayConsultSchemeNum();

    /**
     * 当天取关人数
     *
     * @return
     */
    public Integer getDayUnsubscribeNum();

    /**
     * 当天对话后取关人数
     *
     * @return
     */
    public Integer getDayTextUnsubscribeNum();


    /**
     * 当天一句话后取关总数
     *
     * @return
     */
    public Integer getDayQuickUnsubscribeNum();

    /**
     * 当天沉默人数
     *
     * @return
     */
    public Integer getDaySilenceNum();

    /**
     * 历史粉丝总数（含当天）
     */
    public Integer getHistoryFansTotal();
}
