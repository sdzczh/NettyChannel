package com.yibi.websocket.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yibi.websocket.enums.EnumExchange;
import com.yibi.websocket.model.Result;
import com.yibi.websocket.model.ResultCode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OkexUtils {

    /**
     * 根据币对获取最新价格
     * @param instrument 币对 例：BTC-USDT
     * @return
     */
    public String getNewPrice(String instrument){
        String url = String.format(EnumExchange.OKEX.getPriceUrl(), instrument);
        String result = "";
        String price = "";
        try {
            result = HTTP.get(url, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONObject data = JSON.parseObject(result);
            price = data.getString("last");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toResultStr(ResultCode.TYPE_ERROR_PARAMS);
        }
        return price;
    }
    /**
     * 判断是否存在此交易所
     * @param exchangId 交易所id
     * @return 交易所id
     */
    public static int isValid(int exchangId){
        EnumExchange[] payStatusList= EnumExchange.values();
        boolean flag=false;
        for (EnumExchange enumExchange : payStatusList) {
            if(enumExchange.getExchangId() == exchangId){
                return exchangId;
            }
        }
        return -1;
    }

    /**
     * 获取24小时之前的价格
     * @param instrument 币对
     * @return
     */
    public String getYestodayPrice(String instrument){
        Calendar cal= Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
        Date time=cal.getTime();
        String start = new SimpleDateFormat("yyyy-MM-dd'T'hh").format(time);
        cal.add(Calendar.HOUR,1);
        time=cal.getTime();
        String end = new SimpleDateFormat("yyyy-MM-dd'T'hh").format(time);
        String url = String.format(EnumExchange.OKEX.getYestodayPriceUrl(), instrument, start, end);
        String result = "";
        String price = "";
        try {
            result = HTTP.get(url, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONArray data = JSON.parseArray(result);
            JSONObject params = data.getJSONObject(0);
            price = params.getString("close");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toResultStr(ResultCode.TYPE_ERROR_PARAMS);
        }
        return price;
    }
    /**
     * 获取今日凌晨的价格
     * @param instrument 币对
     * @return
     */
    public String getTodayPrice(String instrument){
        Calendar cal= Calendar.getInstance();
        Date time=cal.getTime();
        String end = new SimpleDateFormat("yyyy-MM-dd").format(time);
        cal.add(Calendar.DATE,-1);
        time=cal.getTime();
        String start = new SimpleDateFormat("yyyy-MM-dd").format(time);
        String url = String.format(EnumExchange.OKEX.getTodayPriceUrl(), instrument, start, end);
        String result = "";
        String price = "";
        try {
            result = HTTP.get(url, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONArray data = JSON.parseArray(result);
            JSONObject params = data.getJSONObject(0);
            price = params.getString("close");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.toResultStr(ResultCode.TYPE_ERROR_PARAMS);
        }
        return price;
    }
}
