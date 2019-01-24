package com.bituniverse.websocket.netty.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bituniverse.websocket.entity.*;
import com.bituniverse.websocket.service.*;
import com.bituniverse.websocket.utils.*;
import com.bituniverse.websocket.enums.CoinType;
import com.bituniverse.websocket.enums.EnumExchange;
import com.bituniverse.websocket.enums.EnumScene;
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
 * @author okcoin
 */
@Service
@Transactional
@Log4j2
public class OKCoinServiceImpl implements WebSocketService {

    @Resource
    private RedisTemplate<String, String> redis;
    @Autowired
    private OkexDealRecordService okexDealRecordService;
    @Autowired
    private CoinDataService coinDataService;
    @Autowired
    private SuperOrderService superOrderService;
    @Autowired
    private CoinPriceService coinPriceService;
    @Autowired
    private DayStateService dayStateService;
    @Autowired
    private CapDistributionService capDistributionService;

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
                    String c2 = strArr[3].toUpperCase();
                    String c1 = strArr[4].toUpperCase();
                    log.info("收到okcoin服务器数据最新深度变化【" + c2 + " - " + c1 +"】：" + resultObj.toJSONString());
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
                    broadcastData.put("coin", c2);
                    broadcastData.put("info", data);
                    broadcast.put("data", broadcastData);
                    WebsocketClientUtils.sendTextMessage(broadcast.toJSONString());
                }else if (channel.contains("deals")) {
                    String[] strArr = channel.split("_");
                    String c2 = strArr[3].toUpperCase();
                    JSONArray data = resultObj.getJSONArray("data").getJSONArray(0);
                    //统计usdt日流通量
                    BigDecimal usdtAmount = new BigDecimal(data.get(1).toString());
                    String usdtAmountRedis = RedisUtil.searchHashString(redis, String.format(RedisKey.COIN_DETAILS, EnumExchange.OKEX.getExchangId(), CoinType.getCode(c2)), "usdt_amount");
                    if(!"".equals(usdtAmountRedis) && usdtAmountRedis != null){
                        usdtAmount = new BigDecimal(usdtAmountRedis).add(usdtAmount);
                        RedisUtil.addHashString(redis, String.format(RedisKey.COIN_DETAILS, EnumExchange.OKEX.getExchangId(), CoinType.getCode(c2)), "usdt_amount", usdtAmount.toString());
                    }else{
                        RedisUtil.addHashString(redis, String.format(RedisKey.COIN_DETAILS, EnumExchange.OKEX.getExchangId(), CoinType.getCode(c2)), "usdt_amount", usdtAmount.toString());
                    }

                    /*----------------------------------------发送最新价格广播-----------------------------------------------------------*/
                    List<String> list = new ArrayList<>();
                    list.add("USDT");
                    list.add("CNY");
                    list.add("BTC");
                    list.add("ETH");
                    String usdtPrice = "";
                    for(String coin : list){
                        String price = data.get(1).toString();
                        if(coin.equals(c2)){
                            price = "1";
                        }else if(coin.equals("USDT")){
                            savePrice(CoinType.getCode(coin), CoinType.getCode(c2), EnumExchange.OKEX.getExchangId(), price);
                        }else{
                            usdtPrice = RedisUtil.searchString(redis, String.format(RedisKey.USDT_PRICE, coin));
                            BigDecimal bPrice = new BigDecimal(usdtPrice).multiply(new BigDecimal(price));
                            price = BigDecimalUtils.round(bPrice, 8).toString();
                            //保存最新价格
                            savePrice(CoinType.getCode(coin), CoinType.getCode(c2), EnumExchange.OKEX.getExchangId(), price);
                        }
                        if("CNY".equals(coin)){
                            //插入成交记录
                            insert(data, c2, price);
                            BigDecimal total = new BigDecimal(price).multiply(new BigDecimal(data.get(2).toString()));
                            //记录超级大单
                            getSuperOrder(c2, total, data, usdtPrice);
                            //记录24小时状态
                            save24hState(c2, total, data, price, usdtAmountRedis);
                            //饼图数据
                            getFundDistribution(c2, total, data);
                        }
                        /*JSONObject broadcast = new JSONObject();
                        broadcast.put("action", "broadcast");
                        JSONObject broadcastData = new JSONObject();
                        Integer c1 = CoinType.getCode(coin);
                        broadcastData.put("scene", EnumScene.SCENEN_INDEX_OKEX);
                        broadcastData.put("info", price);
                        broadcastData.put("c2", CoinType.getCode(c2));
                        broadcastData.put("c1", c1);
                        broadcastData.put("exchangeId", EnumExchange.OKEX.getExchangId());
                        broadcast.put("data", broadcastData);
                        WebsocketClientUtils.sendTextMessage(broadcast.toJSONString());*/
                    }
                }else if (channel.contains("kline")) {
                    /*String[] strArr = channel.split("_");
                    String c2 = strArr[3].toUpperCase();
                    String c1 = strArr[4].toUpperCase();
                    log.info("收到okcoin服务器数据最新K-line变化【" + c2 + " - " + c1 +"】：" + resultObj.toJSONString());
                    JSONArray data = resultObj.getJSONArray("data");
                    JSONArray array = data.getJSONArray(0);
                    Long timestamp = array.getLong(0);
                    BigDecimal open = array.getBigDecimal(1);
                    BigDecimal high = array.getBigDecimal(2);
                    BigDecimal low = array.getBigDecimal(3);
                    BigDecimal price = array.getBigDecimal(4);
                    BigDecimal amount = array.getBigDecimal(5);
                    if (price == null) price = new BigDecimal(0);
                    Map<String, Object> params = new HashMap<>();
                    params.put("amount", amount);
                    params.put("close", price);
                    params.put("time", DateUtils.stampToDate(String.valueOf(timestamp)));
                    params.put("high", high);
                    params.put("low", low);
                    params.put("open", open);
                    *//*----------------------------------------发送线广播-----------------------------------------------------------*//*
                    JSONObject broadcast = new JSONObject();
                    broadcast.put("action", "broadcast");
                    JSONObject broadcastData = new JSONObject();
                    *//*switch (c1){
                        case "BTC" : broadcastData.put("scene", EnumScene.SCENEN_DETAILS_OKEX_KLINE_BTC); break;
                        case "ETH" : broadcastData.put("scene", EnumScene.SCENEN_DETAILS_OKEX_KLINE_ETH); break;
                        default : broadcastData.put("scene", -1);
                    }*//*
                    broadcastData.put("scene", EnumScene.SCENEN_DETAILS_OKEX);
                    broadcastData.put("coin", c2);
                    broadcastData.put("info", params);
                    broadcast.put("data", broadcastData);
                    WebsocketClientUtils.sendTextMessage(broadcast.toJSONString());*/
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
     * 保存价格
     * @param c1
     * @param c2
     * @param exchangId
     * @param price
     */
    private void savePrice(Integer c1, Integer c2, Integer exchangId, String price) {
        String key = String.format(RedisKey.COIN_PRICE, exchangId, c2, c1);
        Map<Object, Object> map = new HashMap<>();
        map.put("c1", c1);
        map.put("c2", c2);
        map.put("exchangeid", exchangId);
        List<CoinPrice> list = coinPriceService.selectAll(map);
        if(list != null && list.size() != 0){
            CoinPrice coinPrice = list.get(0);
            coinPrice.setPrice(price);
            coinPriceService.saveOrUpdate(coinPrice);
            RedisUtil.addString(redis, key, price);
        }else{
            RedisUtil.addString(redis, key, price);
            CoinPrice coinPrice = new CoinPrice();
            coinPrice.setPrice(price);
            coinPrice.setExchangeId(exchangId);
            coinPrice.setC2(c2);
            coinPrice.setC1(c1);
            coinPriceService.insertSelective(coinPrice);
        }
    }

    /**
     * 资金分布饼形图
     * @param c2 当前币种
     * @param total 成交额
     * @param data
     */
    private void getFundDistribution(String c2, BigDecimal total, JSONArray data) {
        Integer coin = CoinType.getCode(c2);
        String redisKey = String.format(RedisKey.COIN_FUND_DISTRIBUTION, EnumExchange.OKEX.getExchangId(), coin);
        //平均交易额
        String average = RedisUtil.searchHashString(redis, redisKey, "average");
        //交易笔数
        String amount = RedisUtil.searchHashString(redis, redisKey, "amount");
        //交易类型 0买 1卖
        String side = data.get(4).toString();
        Integer type = side.equals("bid") ? 0 : 1;
        if(!StrUtils.isBlank(average) && !StrUtils.isBlank(amount)){
            BigDecimal averageBigdecimal = new BigDecimal(average);
            BigDecimal amountBigdecimal = new BigDecimal(amount);
            //小单
            if(total.compareTo(averageBigdecimal) == -1){
                common(c2, "small", type, total);
                /*String small = RedisUtil.searchString(redis, String.format(RedisKey.COIN_FUND_DISTRIBUTION_DETAILS, EnumExchange.OKEX.getExchangId(), c2, "small", type));
                String num = RedisUtil.searchString(redis, String.format(RedisKey.COIN_FUND_DISTRIBUTION_DETAILS, EnumExchange.OKEX.getExchangId(), c2, "num", type));
                if(!StrUtils.isBlank(small) && !StrUtils.isBlank(num)) {
                    BigDecimal smallBig = new BigDecimal(small);
                    BigDecimal numBig = new BigDecimal(num);
                    RedisUtil.addString(redis, String.format(RedisKey.COIN_FUND_DISTRIBUTION_DETAILS, EnumExchange.OKEX.getExchangId(), c2, "small", type), smallBig.add(total).toString());
                    RedisUtil.addString(redis, String.format(RedisKey.COIN_FUND_DISTRIBUTION_DETAILS, EnumExchange.OKEX.getExchangId(), c2, "num", type), numBig.add(new BigDecimal(1)).toString());
                }else{
                    RedisUtil.addString(redis, String.format(RedisKey.COIN_FUND_DISTRIBUTION_DETAILS, EnumExchange.OKEX.getExchangId(), c2, "small", type), total.toString());
                    RedisUtil.addString(redis, String.format(RedisKey.COIN_FUND_DISTRIBUTION_DETAILS, EnumExchange.OKEX.getExchangId(), c2, "num", type), "1");
                }*/
            }
            //大单
            else if(averageBigdecimal.multiply(new BigDecimal(10)).compareTo(total) == -1){
                common(c2, "big", type, total);
            }
            //中单
            else{
                common(c2, "mid", type, total);
            }
            //单项成交额
            String sum = RedisUtil.searchString(redis,String.format(RedisKey.COIN_FUND_DISTRIBUTION_TOTAL, EnumExchange.OKEX.getExchangId(), coin, type));
            if(StrUtils.isBlank(sum)){
                RedisUtil.addString(redis, String.format(RedisKey.COIN_FUND_DISTRIBUTION_TOTAL, EnumExchange.OKEX.getExchangId(), coin, type), total.toString());
            }else {
                RedisUtil.addString(redis, String.format(RedisKey.COIN_FUND_DISTRIBUTION_TOTAL, EnumExchange.OKEX.getExchangId(), coin, type), new BigDecimal(sum).add(total).toString());
            }
            //交易额均值
            total = total.add(averageBigdecimal.multiply(amountBigdecimal)).divide(amountBigdecimal.add(new BigDecimal(1)),8, BigDecimal.ROUND_HALF_UP);
            RedisUtil.addHashString(redis, redisKey, "average", total.toString());
            //交易笔数
            RedisUtil.addHashString(redis, redisKey, "amount", amountBigdecimal.add(new BigDecimal(1)).toString());

        }else{
            RedisUtil.addHashString(redis, redisKey, "average", total.toString());
            RedisUtil.addHashString(redis, redisKey, "amount", "1");
        }
    }

    /**
     * 大中小单 记录数据
     * @param c2 交易币
     * @param action big mid small
     * @param type 入0 出1
     * @param total 金额
     */
    private void common(String c2, String action, Integer type, BigDecimal total){
        Map<Object, Object> map = new HashMap<>();
        map.put("coin", c2);
        map.put("exchangeId", EnumExchange.OKEX.getExchangId());
        map.put("type", type);
        map.put("param", action);
        CapDistribution capDistribution;
        List<CapDistribution> list = capDistributionService.selectAll(map);
        if(list != null && list.size() != 0){
            capDistribution = list.get(0);
        }else {
            capDistribution = new CapDistribution();
        }
        String big = RedisUtil.searchString(redis, String.format(RedisKey.COIN_FUND_DISTRIBUTION_DETAILS, EnumExchange.OKEX.getExchangId(), c2, action, type));
        String num = RedisUtil.searchString(redis, String.format(RedisKey.COIN_FUND_DISTRIBUTION_DETAILS, EnumExchange.OKEX.getExchangId(), c2, "num", type));
        if(!StrUtils.isBlank(big) && !StrUtils.isBlank(num)) {
            BigDecimal bigBig = new BigDecimal(big);
            BigDecimal numBig = new BigDecimal(num);
            RedisUtil.addString(redis, String.format(RedisKey.COIN_FUND_DISTRIBUTION_DETAILS, EnumExchange.OKEX.getExchangId(), c2, action, type), bigBig.add(total).toString());
            RedisUtil.addString(redis, String.format(RedisKey.COIN_FUND_DISTRIBUTION_DETAILS, EnumExchange.OKEX.getExchangId(), c2, "num", type), numBig.add(new BigDecimal(1)).toString());
            capDistribution.setAmount(bigBig.add(total).toString());
        }else{
            RedisUtil.addString(redis, String.format(RedisKey.COIN_FUND_DISTRIBUTION_DETAILS, EnumExchange.OKEX.getExchangId(), c2, action, type), total.toString());
            RedisUtil.addString(redis, String.format(RedisKey.COIN_FUND_DISTRIBUTION_DETAILS, EnumExchange.OKEX.getExchangId(), c2, "num", type), "1");
            capDistribution.setAmount(total.toString());
        }
        capDistribution.setCoin(c2);
        capDistribution.setExchangeId(EnumExchange.OKEX.getExchangId());
        capDistribution.setType(type);
        capDistribution.setParam(action);
        capDistributionService.saveOrUpdate(capDistribution);
    }

    /**
     * 获取超级大单
     * @param coin
     * @param total
     * @param data
     * @param usdtPrice
     */
    public void getSuperOrder(String coin, BigDecimal total, JSONArray data, String usdtPrice) {
        Map<String, Object> resultMap = new HashMap<>();
        BigDecimal min = new BigDecimal("800000");
        if(total.compareTo(min) == -1){
            return;
        }else{
            //插入数据库
            String side = data.get(4).toString();
            Integer action = "ask".equals(side) ? 1 : 0;
            SuperOrder superOrder = new SuperOrder();
            BigDecimal price = new BigDecimal(data.get(1).toString()).multiply(new BigDecimal(usdtPrice));
            superOrder.setCoin(coin);
            superOrder.setExchangeId(EnumExchange.OKEX.getExchangId());
            superOrder.setPrice(price.toString());
            superOrder.setSide(action.toString());
            superOrder.setTotal(total.toString());
            superOrder.setSize(data.get(2).toString());
            superOrder.setTime(DateUtils.getCurrentDateStr() + " " + data.get(3).toString());
            superOrderService.insertSelective(superOrder);
            //写入缓存
            resultMap.put("time", DateUtils.getCurrentDateStr() + " " + data.get(3));
            resultMap.put("side", action.toString());
            resultMap.put("price",price.toString());
            resultMap.put("total", total);
            resultMap.put("size", data.get(2));
            resultMap.put("exchangeId", superOrder.getExchangeId());
            RedisUtil.addListRight(redis, String.format(RedisKey.SUPER_ORDER, EnumExchange.OKEX.getExchangId(), coin), resultMap);
        }
    }

    /**
     * 资产24小时流向
     * @param coin  币种名称
     * @param total 单笔总金额
     * @param data
     * @param price rmb价格
     * @param usdtAmountRedis
     * @throws Exception
     */
    public void save24hState(String coin, BigDecimal total, JSONArray data, String price, String usdtAmountRedis) throws Exception {
        DayState dayState = getDayState(coin);
        if(dayState == null){
            dayState = new DayState();
        }
        Map<Object, Object> map = new HashMap<>();
        map.put("exchangeId", EnumExchange.OKEX.getExchangId());
        map.put("coin", coin);
        List<CoinData> list = coinDataService.selectAll(map);
        CoinData coinData;
        if(list != null && list.size() != 0){
            coinData = list.get(0);
        }else {
            coinData = new CoinData();
        }
        String side = data.get(4).toString();
        /*String inKey = String.format(RedisKey.DAY_IN_ORDER, coin);
        //之前记录的今日交易买入总金额
        String oldIn = RedisUtil.searchString(redis, inKey);
        String outKey = String.format(RedisKey.DAY_OUT_ORDER, coin);
        //之前记录的今日交易卖出总金额
        String oldOut = RedisUtil.searchString(redis, outKey);*/
        String oldIn = dayState == null ? null : dayState.getDayIn();
        String oldOut = dayState == null ? null : dayState.getDayOut();
        if("bid".equals(side)){
            if(!"".equals(oldIn) && oldIn != null){
                total = total.add(new BigDecimal(oldIn));
            }
            RedisUtil.addString(redis, String.format(RedisKey.DAY_IN_ORDER, EnumExchange.OKEX.getExchangId(), coin), total.toString());
            dayState.setDayIn(total.toString());

        }else if("ask".equals(side)) {
            if (!"".equals(oldOut) && oldOut != null) {
                total = total.add(new BigDecimal(oldOut));
            }
            RedisUtil.addString(redis, String.format(RedisKey.DAY_OUT_ORDER, EnumExchange.OKEX.getExchangId(), coin), total.toString());
            dayState.setDayOut(total.toString());
        }else{
            throw new Exception("获取最新订单信息有误");
        }
        BigDecimal actual;
        if(oldIn == null && oldOut==null ){
             actual = new BigDecimal(0);
        }else {
             actual = new BigDecimal(oldIn).subtract(new BigDecimal(oldOut));
        }
        String actualKey = String.format(RedisKey.DAY_ACTUAL_ORDER, EnumExchange.OKEX.getExchangId(), coin);
        RedisUtil.addString(redis, actualKey, actual.toString());
        dayState.setActual(actual.toString());
        //市值
        String marketCap = RedisUtil.searchHashString(redis, String.format(RedisKey.COIN_DETAILS, 0, CoinType.getCode(coin)), "marketCap");
        //24小时净流入百分比
        String actualParentKey = String.format(RedisKey.DAY_ACTUALPARENT_ORDER, EnumExchange.OKEX.getExchangId(), coin);
        if(actual.compareTo(BigDecimal.ZERO) == -1){
            actual = BigDecimalUtils.plusMinus(actual);
            BigDecimal parent = actual.divide(new BigDecimal(marketCap), BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            RedisUtil.addString(redis, actualParentKey, "-" + BigDecimalUtils.roundDown(parent, 2) + "%");
            dayState.setRatio("-" + BigDecimalUtils.roundDown(parent, 2) + "%");
        }else{
            BigDecimal parent = actual.divide(new BigDecimal(marketCap), BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            RedisUtil.addString(redis, actualParentKey, "+" + BigDecimalUtils.roundDown(parent, 2) + "%");
            dayState.setRatio("+" + BigDecimalUtils.roundDown(parent, 2) + "%");
        }
        dayState.setCoin(coin);
        dayStateService.saveOrUpdate(dayState);
        String priceChangeRedisKey = String.format(RedisKey.COIN_DETAILS, EnumExchange.OKEX.getExchangId(), CoinType.getCode(coin));
        //详情页title 价格
        RedisUtil.addHashString(redis, priceChangeRedisKey, "price", price);
        //详情页title 价格变化
        String oldPrice = coinPriceService.getPrice(1, CoinType.getCode(coin), EnumExchange.OKEX.getExchangId());
        RedisUtil.addHashString(redis, priceChangeRedisKey, "24hchange_price", new BigDecimal(price).subtract(new BigDecimal(oldPrice)).toString());
        BigDecimal priceChange = new BigDecimal(price).subtract(new BigDecimal(oldPrice));
        //详情页title 价格变化百分比
        BigDecimal parent = new BigDecimal(0);
        if(priceChange.compareTo(BigDecimal.ZERO) == -1){
            priceChange = BigDecimalUtils.plusMinus(priceChange);
            parent = BigDecimalUtils.roundDown(priceChange.divide(new BigDecimal(oldPrice), BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)), 2);
            RedisUtil.addHashString(redis, priceChangeRedisKey, "percent_change_24h", "-" + parent + "%");
            coinData.setPriceChangePercent("-" + parent + "%");
        }else{
            parent = BigDecimalUtils.roundDown(priceChange.divide(new BigDecimal(oldPrice), BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)), 2);
            RedisUtil.addHashString(redis, priceChangeRedisKey, "percent_change_24h", "+" + parent + "%");
            coinData.setPriceChangePercent("+" + parent + "%");
        }
        //当前价格折合usdt价格
        String usdtPrice = RedisUtil.searchString(redis, String.format(RedisKey.USDT_PRICE, "CNY"));
        usdtPrice = new BigDecimal(price).divide(new BigDecimal(usdtPrice), 4,  BigDecimal.ROUND_HALF_UP).toString();
        RedisUtil.addHashString(redis, priceChangeRedisKey, "usdt_price", usdtPrice);
        //24小时最高价
        String dayHigh = RedisUtil.searchHashString(redis, priceChangeRedisKey, "24h_high");
        if("".equals(dayHigh) || dayHigh == null  || new BigDecimal(dayHigh).compareTo(new BigDecimal(price)) == -1){
            dayHigh = price;
            RedisUtil.addHashString(redis, priceChangeRedisKey, "24h_high", dayHigh);
        }
        //24小时最低价
        String dayLow = RedisUtil.searchHashString(redis, priceChangeRedisKey, "24h_low");
        if("".equals(dayLow) || dayLow == null || new BigDecimal(price).compareTo(new BigDecimal(dayLow)) == -1){
            dayLow = price;
            RedisUtil.addHashString(redis, priceChangeRedisKey, "24h_low", dayLow);
        }

        //存入coinData对象到数据库
        coinData.setCoin(coin);
        coinData.setExchangeId(EnumExchange.OKEX.getExchangId());
        coinData.setPrice(price);
        coinData.setPriceChange(priceChange.toString());
        coinData.setPriceUsdt(usdtPrice);
        coinData.setDayHigh(dayHigh);
        coinData.setDayLow(dayLow);
        coinData.setDayVolume(usdtAmountRedis);
        coinData.setMarketCap(marketCap);
        coinDataService.saveOrUpdate(coinData);
    }

    /**
     * 插入最新成交记录
     * @param data
     * @param coin
     * @param price
     */
    public void insert(JSONArray data, String coin, String price){
        Integer coinId = CoinType.getCode(coin.toUpperCase());
        String volume = data.get(2).toString();
        String time = data.get(3).toString();
        Integer type = "bid".equals(data.get(4)) ? 0 : 1;
        OkexDealRecord okexDealRecord = new OkexDealRecord();
        okexDealRecord.setPrice(price);
        okexDealRecord.setTime(DateUtils.getCurrentDateStr() + " " + time);
        okexDealRecord.setType(type);
        okexDealRecord.setVolume(volume);
        okexDealRecord.setCoinId(coinId);
        okexDealRecord.setExchangeId(EnumExchange.OKEX.getExchangId());
        okexDealRecordService.insertSelective(okexDealRecord);
    }

    /**
     * 获取24小时资金变化
     * @param coin
     * @return
     */
    public DayState getDayState(String coin){
        String inKey = String.format(RedisKey.DAY_IN_ORDER, EnumExchange.OKEX.getExchangId(), coin);
        String oldIn = RedisUtil.searchString(redis, inKey);
        String outKey = String.format(RedisKey.DAY_OUT_ORDER, EnumExchange.OKEX.getExchangId(), coin);
        String oldOut = RedisUtil.searchString(redis, outKey);
        String actual = RedisUtil.searchString(redis, String.format(RedisKey.DAY_ACTUAL_ORDER, EnumExchange.OKEX.getExchangId(), coin));
        String ratio = RedisUtil.searchString(redis, String.format(RedisKey.DAY_ACTUALPARENT_ORDER, EnumExchange.OKEX.getExchangId(), coin));
        if(StrUtils.isNotEmptyBatch(oldIn, oldOut, actual, ratio)){
            Map<Object, Object> map = new HashMap<>();
            map.put("coin", coin);
            map.put("exchangeid", EnumExchange.OKEX.getExchangId());
            List<DayState> list = dayStateService.selectAll(map);
            if(list != null && list.size() !=0){
                return list.get(0);
            }
            return null;
        }else{
            DayState dayState = new DayState();
            if("BTC".equals(coin)){
                dayState.setId(1);
            }else{
                dayState.setId(2);
            }
            dayState.setActual(actual);
            dayState.setCoin(coin);
            dayState.setDayIn(oldIn);
            dayState.setExchangeId(EnumExchange.OKEX.getExchangId());
            dayState.setDayOut(oldOut);
            dayState.setRatio(ratio);
            return dayState;
        }
    }
}
