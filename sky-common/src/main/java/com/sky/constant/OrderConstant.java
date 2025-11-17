package com.sky.constant;

public class OrderConstant {
    //支付状态
    public static final Integer NOT_PAID = 0;
    public static final Integer HAVE_PAID = 1;
    public static final Integer RETURN_PAID = 2;

    //配送状态
    public static final Integer NOW_DILIVER = 1;
    public static final Integer LATER_DILIVER = 1;

    //支付方式
    public static final Integer WECHAT_PAID = 1;
    public static final Integer ALI_PAID = 2;

    //餐具数量状态
    public static final Integer OFFER_BY_DISH = 1;
    public static final Integer OFFER_BY_USER = 0;

    //订单状态
    public static final Integer WAIT_PAID = 1;
    public static final Integer WAIT_RECIEVE = 2;
    public static final Integer HAVE_RECIEVE = 3;
    public static final Integer IS_DELIVERING = 4;
    public static final Integer HAVE_FINISH = 5;
    public static final Integer HAVE_CANCLED = 6;
}
