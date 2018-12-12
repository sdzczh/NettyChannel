package com.yibi.websocket.netty.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yibi.websocket.enums.CoinType;
import com.yibi.websocket.enums.EnumExchange;
import com.yibi.websocket.enums.EnumScene;
import com.yibi.websocket.netty.WebSocketService;
import com.yibi.websocket.utils.*;
import com.yibi.websocket.variables.RedisKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订阅信息处理类需要实现WebSocketService接口
 *
 * @author okcoin
 */
@Service
@Transactional
@Log4j2
public class OKCoinServiceImpl implements WebSocketService {

    @Resource
    private RedisTemplate<String, String> redis;

    static {
        //ClassPathXmlApplicationContext appCtx = new ClassPathXmlApplicationContext("applicationContext.xml");
        //redis = (RedisTemplate<String, Object>) appCtx.getBean("redis");
    }
    PriceConversionUtils pu = new PriceConversionUtils();

    @Override
    public void onReceive(String msg) {
        try {
            Object json = JSON.parse(msg);
            if (json instanceof JSONObject) {
                log.info("收到okcoin服务器数据jsonObject：" + ((JSONObject)json).toJSONString());
            }
            if (json instanceof JSONArray) {
                JSONArray result = (JSONArray) json;
                JSONObject resultObj = result.getJSONObject(0);
                String channel = resultObj.getString("channel");
                if (channel.contains("depth")) {
                    String[] strArr = channel.split("_");
                    String c1 = strArr[3].toUpperCase();
                    String c2 = strArr[4].toUpperCase();
                    log.info("收到okcoin服务器数据最新深度变化【" + c1 + " - " + c2 +"】：" + resultObj.toJSONString());
                    JSONObject data = resultObj.getJSONObject("data");

                    /*----------------------------------------发送深度广播-----------------------------------------------------------*/
                    JSONObject broadcast = new JSONObject();
                    broadcast.put("action", "broadcast");
                    JSONObject broadcastData = new JSONObject();
                    /*switch (c1){
                        case "BTC" : broadcastData.put("scene", EnumScene.SCENEN_DETAILS_OKEX_DEPTH_BTC);
                        case "ETH" : broadcastData.put("scene", EnumScene.SCENEN_DETAILS_OKEX_DEPTH_ETH);
                        default : broadcastData.put("scene", -1);
                    }*/
                    broadcastData.put("coin", c1);
                    broadcastData.put("info", data);
                    broadcast.put("data", broadcastData);
                    WebsocketClientUtils.sendTextMessage(broadcast.toJSONString());
                }else if (channel.contains("deals")) {
                    String[] strArr = channel.split("_");
                    String c1 = strArr[3].toUpperCase();
                    JSONArray data = resultObj.getJSONArray("data").getJSONArray(0);

                    /*----------------------------------------发送最新价格广播-----------------------------------------------------------*/
                    List<String> list = new ArrayList<>();
                    list.add("CNY");
                    list.add("BTC");
                    list.add("ETH");
                    BigDecimal cnyTotal = new BigDecimal(0);
                    for(String coin : list){
                        String price = data.get(1).toString();
                        if(coin.equals(c1)){
                            price = "1";
                        }else {
                            String usdtPrice = RedisUtil.searchString(redis, String.format(RedisKey.USDT_PRICE, coin));
                            BigDecimal bPrice = new BigDecimal(usdtPrice).multiply(new BigDecimal(price));
                            price = BigDecimalUtils.round(bPrice, 8).toString();
                        }
                        if("CNY".equals(coin)){
                            BigDecimal total = new BigDecimal(price).multiply(new BigDecimal(data.get(2).toString()));
                            cnyTotal = total;
                            //记录超级大单
                            getSuperOrder(coin, total, data);
                            //记录24小时状态
                        }else{
                            save24hState(coin, cnyTotal, data);
//                            save24hState(coin);
                        }
                        JSONObject broadcast = new JSONObject();
                        broadcast.put("action", "broadcast");
                        JSONObject broadcastData = new JSONObject();
                        Integer c2 = CoinType.getCode(coin);
                        broadcastData.put("scene", EnumScene.SCENEN_INDEX_OKEX);
                        broadcastData.put("info", price);
                        broadcastData.put("c1", CoinType.getCode(c1));
                        broadcastData.put("c2", c2);
                        broadcastData.put("exchangeId", EnumExchange.OKEX.getExchangId());
                        broadcast.put("data", broadcastData);
                        WebsocketClientUtils.sendTextMessage(broadcast.toJSONString());
                    }
                }else if (channel.contains("kline")) {
                    String[] strArr = channel.split("_");
                    String c1 = strArr[3].toUpperCase();
                    String c2 = strArr[4].toUpperCase();
                    log.info("收到okcoin服务器数据最新K-line变化【" + c1 + " - " + c2 +"】：" + resultObj.toJSONString());
                    JSONArray data = resultObj.getJSONArray("data");
                    JSONArray array = data.getJSONArray(0);
                    Long timestamp = array.getLong(0);
                    BigDecimal open = array.getBigDecimal(1);
                    BigDecimal high = array.getBigDecimal(2);
                    BigDecimal low = array.getBigDecimal(3);
                    BigDecimal price = array.getBigDecimal(4);
                    BigDecimal amount = array.getBigDecimal(5);
                    if (price == null) price = new BigDecimal(0);
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("amount", amount);
                    params.put("close", price);
                    params.put("time", DateUtils.stampToDate(String.valueOf(timestamp)));
                    params.put("high", high);
                    params.put("low", low);
                    params.put("open", open);
                    /*----------------------------------------发送线广播-----------------------------------------------------------*/
                    JSONObject broadcast = new JSONObject();
                    broadcast.put("action", "broadcast");
                    JSONObject broadcastData = new JSONObject();
                    /*switch (c1){
                        case "BTC" : broadcastData.put("scene", EnumScene.SCENEN_DETAILS_OKEX_KLINE_BTC); break;
                        case "ETH" : broadcastData.put("scene", EnumScene.SCENEN_DETAILS_OKEX_KLINE_ETH); break;
                        default : broadcastData.put("scene", -1);
                    }*/
                    broadcastData.put("scene", EnumScene.SCENEN_DETAILS_OKEX);
                    broadcastData.put("coin", c1);
                    broadcastData.put("info", params);
                    broadcast.put("data", broadcastData);
                    WebsocketClientUtils.sendTextMessage(broadcast.toJSONString());
                }
                if (channel.equals("addChannel")) {
                    log.info("okcoin数据订阅成功:" + resultObj.toJSONString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取超级大单
     * @param coin
     * @param total
     * @param data
     */
    public void getSuperOrder(String coin, BigDecimal total, JSONArray data) {
        Map<String, Object> resultMap = new HashMap<>();
        BigDecimal min = new BigDecimal("800000");
        if(total.compareTo(min) == -1){
            return;
        }else{
            resultMap.put("time", data.get(3));
            resultMap.put("side", data.get(4));
            resultMap.put("price", data.get(1));
            resultMap.put("total", total);
            resultMap.put("size", data.get(2));
            RedisUtil.addHashMap(redis, String.format(RedisKey.SUPER_ORDER, coin), resultMap, false);
        }
    }

    public void save24hState(String coin, BigDecimal total, JSONArray data) throws Exception {
        String side = data.get(4).toString();
        String inKey = String.format(RedisKey.DAY_IN_ORDER, coin);
        //之前记录的今日交易买入总金额
        String oldIn = RedisUtil.searchString(redis, inKey);
        String outKey = String.format(RedisKey.DAY_OUT_ORDER, coin);
        //之前记录的今日交易卖出总金额
        String oldOut = RedisUtil.searchString(redis, outKey);
        if("bid".equals(side)){
            if(!"".equals(oldIn) && oldIn != null){
                total = total.add(new BigDecimal(oldIn));
            }
            RedisUtil.addString(redis, inKey, total.toString());

        }else if("ask".equals(side)) {
            if (!"".equals(oldOut) && oldOut != null) {
                total = total.add(new BigDecimal(oldOut));
            }
            RedisUtil.addString(redis, outKey, total.toString());
        }else{
            throw new Exception("获取最新订单信息有误");
        }
        BigDecimal actual = new BigDecimal(oldIn).subtract(new BigDecimal(oldOut));
        String actualKey = RedisKey.DAY_ACTUAL_ORDER;
        RedisUtil.addString(redis, actualKey, actual.toString());
        String marketCap = RedisUtil.searchString(redis, String.format(RedisKey.COIN_MARKET_CAP, coin));
        String actualParentKey = String.format(RedisKey.DAY_ACTUALPARENT_ORDER, coin);
        if(actual.compareTo(BigDecimal.ZERO) == -1){
            actual = BigDecimalUtils.plusMinus(actual);
            BigDecimal parent = actual.divide(new BigDecimal(marketCap), BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            RedisUtil.addString(redis, actualParentKey, "-" + BigDecimalUtils.roundDown(parent, 2) + "%");
        }else{
            BigDecimal parent = actual.divide(new BigDecimal(marketCap), BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            RedisUtil.addString(redis, actualParentKey, "+" + BigDecimalUtils.roundDown(parent, 2) + "%");
        }
    }
/*    public void save24hState(String coin){
        String inKey = RedisKey.DAY_IN_ORDER;
        //之前记录的今日交易买入总金额
        String oldIn = RedisUtil.searchString(redis, inKey);
        String outKey = RedisKey.DAY_OUT_ORDER;
        //之前记录的今日交易卖出总金额
        String oldOut = RedisUtil.searchString(redis, outKey);
        BigDecimal actual = new BigDecimal(oldIn).subtract(new BigDecimal(oldOut));
        String marketCap = RedisUtil.searchString(redis, String.format(RedisKey.COIN_MARKET_CAP, coin));
        String actualParentKey = RedisKey.DAY_ACTUALPARENT_ORDER;
        if(actual.compareTo(BigDecimal.ZERO) == -1){
            actual = BigDecimalUtils.plusMinus(actual);
            BigDecimal parent = actual.divide(new BigDecimal(marketCap), BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            RedisUtil.addString(redis, actualParentKey, "-" + BigDecimalUtils.roundDown(parent, 2) + "%");
        }else{
            BigDecimal parent = actual.divide(new BigDecimal(marketCap), BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            RedisUtil.addString(redis, actualParentKey, "+" + BigDecimalUtils.roundDown(parent, 2) + "%");
        }
    }*/
}
