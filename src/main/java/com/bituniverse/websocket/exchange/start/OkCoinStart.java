package com.bituniverse.websocket.exchange.start;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bituniverse.websocket.netty.WebSocketService;
import com.bituniverse.websocket.netty.WebSoketClient;
import com.bituniverse.websocket.utils.PropertyUtils;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Log4j2
public class OkCoinStart {
    public static void start(){
        log.info("okcoin订阅方法启动");
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring/applicationContext.xml");
        PropertyUtils pt = new PropertyUtils("serverUrl.properties");
        String okcoinServer = pt.getProperty("OKCOIN.WEBSOCKET");
        WebSocketService service = context.getBean(WebSocketService.class);
        // WebSocket客户端
        WebSoketClient client = new WebSoketClient(okcoinServer, service);
        log.info("websocket--okcoin链接建立");
        // 启动客户端
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
            client.start();
        }
        //订阅最新价格
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/depth5:BTC-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/depth5:ETH-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/depth5:LTC-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/depth5:XRP-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/depth5:EOS-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/depth5:DOGE-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/depth5:KCASH-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/depth5:DGB-USDT\"]}");

        //tiker信息
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/ticker:BTC-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/ticker:ETH-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/ticker:LTC-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/ticker:EOS-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/ticker:XRP-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/ticker:DOGE-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/ticker:KCASH-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/ticker:DGB-USDT\"]}");

        //成交信息
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/trade:BTC-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/trade:ETH-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/trade:LTC-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/trade:EOS-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/trade:XRP-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/trade:DOGE-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/trade:KCASH-USDT\"]}");
        client.sendText("{\"op\": \"subscribe\", \"args\": [\"spot/trade:DGB-USDT\"]}");

        /*JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.add("spot/depth5:ETH-USDT");
        jsonObject.put("args", jsonArray);
        jsonObject.put("op", "subscribe");
        System.out.println(jsonObject);
        client.addChannel(jsonObject.toString());*/
    }
}
