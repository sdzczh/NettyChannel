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
    public static  final int ZING = 5;
    public static  final int POE = 6;
    public static  final int XRP = 7;
    public static  final int TEC = 8;
    public static  final int USDT = 9;
    public static  final int BJT = 11;
    public static  final int LKC = 12;

    public static String getCoinName(Integer coinType){
        switch (coinType){
            case 0 : return "CNHT";
            case 1 : return "BTC";
            case 2 : return "EOS";
            case 3 : return "LTC";
            case 4 : return "ETH";
            case 5 : return "ZING";
            case 6 : return "POE";
            case 7 : return "XRP";
            case 8 : return "TEC";
            case 9 : return "USDT";
            case 10 : return "DOGE";
            case 11 : return "BJT";
            case 12 : return "LKC";
            case 13 : return "WNA";
            case 14 : return "KLZ";
            case 15 : return "KCASH";
            case 16 : return "DGB";
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
            case "ZING" : return 5;
            case "POE" : return 6;
            case "XRP" : return 7;
            case "TEC" : return 8;
            case "USDT" : return 9;
            case "DOGE" : return 10;
            case "BJT" : return 11;
            case "LKC" : return 12;
            case "WNA" : return 13;
            case "KLZ" : return 14;
            case "KCASH" : return 15;
            case "DGB" : return 16;
            default: return null;
        }
    }
}
