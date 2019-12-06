package com.bituniverse.websocket.variables;

/**
 * Created by Administrator on 2018/7/12 0012.
 */
public class CoinType {

    public static  final int NONE = -1; //无具体指向币种
    public static  final int CNHT = 0;
    public static  final int BTC = 1;
    public static  final int EOS = 2;
    public static  final int LTC = 3;
    public static  final int ETH = 4;
    public static  final int ETC = 5;
    public static  final int BCH = 6;
    public static  final int XRP = 7;
    public static  final int PGY = 8;
    public static  final int USDT = 9;

    public static String getCoinName(Integer coinType){
        switch (coinType){
            case 0 : return "CNHT";
            case 1 : return "BTC";
            case 2 : return "EOS";
            case 3 : return "LTC";
            case 4 : return "ETH";
            case 5 : return "ETC";
            case 6 : return "BCH";
            case 7 : return "XRP";
            case 8 : return "PGY";
            case 9 : return "USDT";
            default: return null;
        }
    }

    public static Object getCode(String coinName) {
        switch (coinName){
            case "CNHT" : return 0;
            case "BTC" : return 1;
            case "EOS" : return 2;
            case "LTC" : return 3;
            case "ETH" : return 4;
            case "ETC" : return 5;
            case "BCH" : return 6;
            case "XRP" : return 7;
            case "PGY" : return 8;
            case "USDT" : return 9;
            default: return null;
        }
    }
}
