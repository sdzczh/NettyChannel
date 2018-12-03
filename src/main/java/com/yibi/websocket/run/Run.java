package com.yibi.websocket.run;

import com.yibi.websocket.netty.WebSocketService;
import com.yibi.websocket.netty.WebSoketClient;
import com.yibi.websocket.utils.PropertyUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Log4j2
public class Run {
    public static void main(String[] args) {
        log.info("全球行情更新方法启动");
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring/applicationContext.xml");
        PropertyUtils pt = new PropertyUtils("serverUrl.properties");
        String okcoinServer = pt.getProperty("BIT.WEBSOCKET");
        WebSocketService service = context.getBean(WebSocketService.class);
        // WebSocket客户端
        WebSoketClient client = new WebSoketClient(okcoinServer, service);
        log.info("websocket--okcoin链接建立");
        // 启动客户端
        client.start();

        // 添加订阅btc_usd, eth_usd K-line
        client.addChannel("ok_sub_spot_btc_usd_kline_1min");
        client.addChannel("ok_sub_spot_eth_usd_kline_1min");
        // 添加订阅btc_usd, eth_usd 深度
        client.addChannel("ok_sub_spot_btc_usdt_depth");
        client.addChannel("ok_sub_spot_bch_btc_depth");
//        client.addChannel("ok_sub_spot_eth_usd_depth");
        //订阅最新价格
        client.addChannel("ok_sub_spot_btc_usd_deals");
        client.addChannel("ok_sub_spot_eth_usd_deals");

    }
}
