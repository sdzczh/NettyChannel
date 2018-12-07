package com.yibi.websocket.netty.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.yibi.websocket.enums.CoinType;
import com.yibi.websocket.enums.EnumScene;
import com.yibi.websocket.netty.WebSocketService;
import com.yibi.websocket.utils.DateUtils;
import com.yibi.websocket.utils.RedisUtil;
import com.yibi.websocket.utils.WebsocketClientUtils;
import com.yibi.websocket.variables.RedisKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
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

                    /*----------------------------------------发送主流行情广播-----------------------------------------------------------*/
                    JSONObject broadcast = new JSONObject();
                    broadcast.put("action", "broadcast");
                    JSONObject broadcastData = new JSONObject();
                    broadcastData.put("scene", 3521);
                    broadcastData.put("info", data);
                    broadcast.put("data", broadcastData);
                    WebsocketClientUtils.sendTextMessage(broadcast.toJSONString());
                }else if (channel.contains("deals")) {
                    String[] strArr = channel.split("_");
                    String c1 = strArr[3].toUpperCase();
                    log.info("收到okcoin服务器数据最新价格变化【" + c1 +"】：" + resultObj.toJSONString());
                    JSONArray data = resultObj.getJSONArray("data");

                    /*----------------------------------------发送主流行情广播-----------------------------------------------------------*/
                    JSONObject broadcast = new JSONObject();
                    broadcast.put("action", "broadcast");
                    JSONObject broadcastData = new JSONObject();
                    broadcastData.put("scene", EnumScene.SCENEN_INDEX_OKEX_BTC.getScene());
                    broadcastData.put("info", data);
                    broadcast.put("data", broadcastData);
                    WebsocketClientUtils.sendTextMessage(broadcast.toJSONString());
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
                    String redisKey = String.format(RedisKey.KLINEOKEX, c1, c2);
                    RedisUtil.addStringObj(redis, redisKey, params);
                    /*----------------------------------------发送主流行情广播-----------------------------------------------------------*/
                    JSONObject broadcast = new JSONObject();
                    broadcast.put("action", "broadcast");
                    JSONObject broadcastData = new JSONObject();
                    broadcastData.put("scene", EnumScene.SCENEN_KLINE_OKEX_BTC.getScene());
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
