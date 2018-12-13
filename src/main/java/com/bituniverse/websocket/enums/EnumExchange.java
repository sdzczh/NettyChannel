package com.bituniverse.websocket.enums;


/**
 * 交易所枚举类
 */
public enum EnumExchange {
    OKEX(1, "OKEX"),
    HUOBI(2, "HUOBI"),
    BINANCE(3, "BINANCE");
    private final int exchangId;
    private final String desc;
    EnumExchange(int exchangId, String desc){
        this.desc = desc;
        this.exchangId = exchangId;
    }

    public int getExchangId() {
        return exchangId;
    }

    public String getDesc() {
        return desc;
    }

}
