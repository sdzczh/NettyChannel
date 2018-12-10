package com.yibi.websocket.netty.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yibi.websocket.enums.CoinType;
import com.yibi.websocket.enums.EnumScene;
import com.yibi.websocket.netty.WebSocketService;
import com.yibi.websocket.utils.DateUtils;
import com.yibi.websocket.utils.PriceConversionUtils;
import com.yibi.websocket.utils.RedisUtil;
import com.yibi.websocket.utils.WebsocketClientUtils;
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
                    for(String coin : list){
                        String price = data.get(1).toString();
                        if(coin.equals(c1)){
                            price = "1";
                        }else {
                            String usdtPrice = RedisUtil.searchString(redis, String.format(RedisKey.USDT_PRICE, coin));
                            price = new BigDecimal(usdtPrice).multiply(new BigDecimal(price)).toString();
                        }
                        JSONObject broadcast = new JSONObject();
                        broadcast.put("action", "broadcast");
                        JSONObject broadcastData = new JSONObject();
                        /* switch (c1){
                            case "CNY" : broadcastData.put("scene", EnumScene.SCENEN_DETAILS_OKEX_PRICE_CNY);
                            case "BTC" : broadcastData.put("scene", EnumScene.SCENEN_DETAILS_OKEX_PRICE_BTC);
                            case "ETH" : broadcastData.put("scene", EnumScene.SCENEN_DETAILS_OKEX_PRICE_ETH);
                            default : broadcastData.put("scene", -1);
                        }*/
                        broadcastData.put("scene", EnumScene.SCENEN_INDEX_OKEX);
                        broadcastData.put("info", price);
                        broadcast.put("c1", CoinType.getCode(c1));
                        broadcast.put("c2", CoinType.getCode(coin));
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
}
