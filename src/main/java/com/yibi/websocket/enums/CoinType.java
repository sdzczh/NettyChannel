package com.yibi.websocket.enums;

public enum CoinType {
    USDT(0),
    BTC(1),
    ETH(2),
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
    		case "BTC" : return 1;
    		case "ETH" : return 2;
    		default:
    			return null;
    	}
    	
    }


}
