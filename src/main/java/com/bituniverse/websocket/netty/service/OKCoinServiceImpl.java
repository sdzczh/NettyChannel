package com.bituniverse.websocket.netty.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bituniverse.websocket.Constants;
import com.bituniverse.websocket.entity.*;
import com.bituniverse.websocket.service.*;
import com.bituniverse.websocket.utils.*;
import com.bituniverse.websocket.enums.CoinType;
import com.bituniverse.websocket.enums.EnumExchange;
     import com.bituniverse.websocket.netty.WebSocketService;
import com.bituniverse.websocket.variables.RedisKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 订阅信息处理类需要实现WebSocketService接口
 *
 * @author OkCoinStart
 */
@Service
@Transactional
@Log4j2
public class OKCoinServiceImpl implements WebSocketService {

    @Resource
    private RedisTemplate<String, String> redis;
    @Autowired
    private OkexDealRecordService okexDealRecordService;

    @Override
    public void onReceive(String msg) {
        try {
            Object json = JSON.parse(msg);
            if (json instanceof JSONObject) {
                log.info("收到okcoin服务器数据jsonObject：" + ((JSONObject)json).toJSONString());
            }
            JSONObject jsonObject = JSONObject.parseObject(msg);
            String table = jsonObject.getString("table");
            JSONArray data = jsonObject.getJSONArray("data");
            //深度交易挂单订阅
            if(Constants.TABLE_DEPTH.equals(table)){
                depthOrderChannel(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 交易订阅处理
     * @param data
     */
    private void depthOrderChannel(JSONArray data) {
        JSONObject json = new JSONObject();
        JSONArray sales = new JSONArray();
        JSONArray buys = new JSONArray();

        JSONObject okResult = data.getJSONObject(0);
        JSONArray bidsArray = okResult.getJSONArray("bids");
        for (int i = 0; i < bidsArray.size(); i++) {
            JSONArray info = bidsArray.getJSONArray(i);
            Map<String, Object> map = new HashMap<>();
            map.put("rate", "0");
            map.put("price", info.get(0));
            map.put("remain", info.get(1));
            map.put("num", i + 1);
            sales.add(map);
        }
        JSONArray asksArray = okResult.getJSONArray("asks");
        for (int i = 0; i < asksArray.size(); i++) {
            JSONArray info = asksArray.getJSONArray(i);
            Map<String, Object> map = new HashMap<>();
            map.put("rate", "0");
            map.put("price", info.get(0));
            map.put("remain", info.get(1));
            map.put("num", asksArray.size() - i);
            buys.add(map);
        }
        json.put("buys", buys);
        json.put("sales", sales);
        JSONObject broadcast = new JSONObject();
        broadcast.put("action", "broadcast");

        String instrument = okResult.getString("instrument_id");
        String orderCoin = instrument.substring(0, instrument.indexOf("-"));
        String unionCoin = instrument.substring(orderCoin.length() + 1);

        JSONObject broadcastData = new JSONObject();
        broadcastData.put("c1", CoinType.getCode(unionCoin));
        broadcastData.put("c2", CoinType.getCode(orderCoin));
        broadcastData.put("scene", 350);
        broadcastData.put("gear", 0);
        broadcastData.put("info", json);
        broadcast.put("data", broadcastData);
        broadcast.put("action", "okBroadcast");
        WebsocketClientUtils.sendTextMessage(broadcast.toJSONString());
    }
}
