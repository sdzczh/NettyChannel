package com.bituniverse.websocket.enums;

public enum CoinType {
    USDT(0),
    CNY(1),
    BTC(2),
    ETH(3),
    ;
    //币种代码
    private Integer code;
    
    CoinType(Integer code) {
        this.code = code;
    }
    
    public Integer code() {
        return this.code;
    }


    
    public static Integer getCode(String name){
    	switch(name){
    		case "USDT" : return 0;
            case "CNY" : return 1;
            case "BTC" : return 2;
            case "ETH" : return 3;
            default:
    			return null;
    	}
    	
    }


}
