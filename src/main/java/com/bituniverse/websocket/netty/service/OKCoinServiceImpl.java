package com.bituniverse.websocket.netty.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bituniverse.websocket.Constants;
import com.bituniverse.websocket.enums.EnumScene;
import com.bituniverse.websocket.service.*;
import com.bituniverse.websocket.utils.*;
import com.bituniverse.websocket.netty.WebSocketService;
import com.bituniverse.websocket.variables.CoinType;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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
            //TIKER订阅
            if(Constants.TABLE_TICKER.equals(table)){
                tikerChannel(data);
            }
            if(Constants.TABLE_TRADE.equals(table)){
                tradeChannel(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 成交处理
     * @param data
     */
    private void tradeChannel(JSONArray data) throws ParseException {
        Map<String, Object> map = new HashMap<>();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject = data.getJSONObject(0);
        BigDecimal price = new BigDecimal(jsonObject.getString("price"));
        BigDecimal amount = new BigDecimal(jsonObject.getString("size"));
        String createTime = sdf2.format(sdf1.parse(jsonObject.getString("timestamp")));
        Integer orderType = "buy".equals(jsonObject.getString("side")) ? 0 : 1;
        map.put("price", price.setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString());
        map.put("amount", amount.setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString());
        map.put("createTime", createTime);
        map.put("orderType", orderType);

        //币种
        String instrument = jsonObject.getString("instrument_id");
        String orderCoin = instrument.substring(0, instrument.indexOf("-"));
        String unionCoin = instrument.substring(orderCoin.length() + 1);
        JSONObject broadcastData = new JSONObject();
        broadcastData.put("c1", CoinType.getCode(unionCoin));
        broadcastData.put("c2", CoinType.getCode(orderCoin));
        broadcastData.put("scene", EnumScene.SCENE_KLINE_YIBI.getScene());
        broadcastData.put("gear", 0);
        broadcastData.put("info", map);
        JSONObject broadcast = new JSONObject();
        broadcast.put("data", broadcastData);
        broadcast.put("action", "okBroadcast");
        WebsocketClientUtils.sendTextMessage(broadcast.toJSONString());
    }

    /**
     * TIKER订阅处理
     * @param data
     */
    private void tikerChannel(JSONArray data) {
        Map<String, Object> map = new HashMap<>();
        JSONObject jsonObject = data.getJSONObject(0);
        BigDecimal price = new BigDecimal(jsonObject.getString("last")).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal cnyPrice = price.multiply(new BigDecimal(7.1)).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal vol = new BigDecimal(jsonObject.getString("quote_volume_24h")).setScale(0, BigDecimal.ROUND_HALF_UP);
        BigDecimal percentage = new BigDecimal(jsonObject.getString("open_24h")).subtract(price).divide(new BigDecimal(jsonObject.getString("open_24h")), 2, BigDecimal.ROUND_HALF_UP);
        //usdt价格
        map.put("newPrice", price.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        //cny价格
        map.put("newPriceCNY", cnyPrice.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        //交易量
        map.put("sumAmount", vol.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        //百分比
        map.put("chgPrice", percentage.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
        //币种
        String instrument = jsonObject.getString("instrument_id");
        String orderCoin = instrument.substring(0, instrument.indexOf("-"));
        String unionCoin = instrument.substring(orderCoin.length() + 1);
        map.put("orderCoinType", CoinType.getCode(orderCoin));
        map.put("unitCoinType", CoinType.getCode(unionCoin));
        map.put("orderCoinCnName", orderCoin);
        map.put("orderCoinName", orderCoin);
        map.put("unitCoinName", unionCoin );
        map.put("high", new BigDecimal(jsonObject.getString("high_24h")).setScale(2, BigDecimal.ROUND_HALF_UP));
        map.put("low", new BigDecimal(jsonObject.getString("low_24h")).setScale(2, BigDecimal.ROUND_HALF_UP));
        JSONObject broadcastData = new JSONObject();
        broadcastData.put("c1", CoinType.getCode(unionCoin));
        broadcastData.put("c2", CoinType.getCode(orderCoin));
        broadcastData.put("scene", EnumScene.SCENE_MARKET_YIBI.getScene());
        broadcastData.put("gear", 0);
        broadcastData.put("info", map);
        JSONObject broadcast = new JSONObject();
        broadcast.put("data", broadcastData);
        broadcast.put("action", "okBroadcast");
        WebsocketClientUtils.sendTextMessage(broadcast.toJSONString());
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
            map.put("num", bidsArray.size() - i);
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
        String instrument = okResult.getString("instrument_id");
        String orderCoin = instrument.substring(0, instrument.indexOf("-"));
        String unionCoin = instrument.substring(orderCoin.length() + 1);

        JSONObject broadcastData = new JSONObject();
        broadcastData.put("c1", CoinType.getCode(unionCoin));
        broadcastData.put("c2", CoinType.getCode(orderCoin));
        broadcastData.put("scene", EnumScene.SCENE_ORDER.getScene());
        broadcastData.put("gear", 0);
        broadcastData.put("info", json);
        broadcast.put("data", broadcastData);
        broadcast.put("action", "okBroadcast");
        WebsocketClientUtils.sendTextMessage(broadcast.toJSONString());
    }
}
