package com.yibi.websocket.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yibi.websocket.variables.Result;
import com.yibi.websocket.variables.ResultCode;

import java.util.HashMap;
import java.util.Map;

public class PriceConversionUtils {

    /**
     * 价格换算
     * @param coinName 当前币种名称 BTC
     * @param amount 数量
     * @param toCoin 目标币种名称 CNY
     * @return
     */
    public String PriceConversion(String coinName, String amount, String toCoin) {
        StringBuffer url = new StringBuffer();
        Map<String,String> headers = new HashMap<>();
        headers.put("X-CMC_PRO_API_KEY", "d1563a31-ef97-4dfa-82db-61cef205b8c6");
        url.append("https://pro-api.coinmarketcap.com/v1/tools/price-conversion")
                .append("?symbol=").append(coinName).append("&amount=").append(amount)
                .append("&convert=").append(toCoin);
        String result = null;
        try {
            result = HTTP.get(url.toString(), headers);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toResultStr(ResultCode.TYPE_ERROR_TIMEOUT);
        }
        JSONObject resu = JSON.parseObject(result);
        JSONObject status = resu.getJSONObject("status");
        Integer error_code = status.getInteger("error_code");
        //参数错误
        if(0 == error_code){
            JSONObject data = resu.getJSONObject("data");
            JSONObject quote = data.getJSONObject("quote");
            JSONObject coin = quote.getJSONObject(toCoin);
            String price = coin.getString("price");
            return price;
        }else{
            return Result.toResultStr(ResultCode.TYPE_ERROR_PARAMS);
        }
    }
}
