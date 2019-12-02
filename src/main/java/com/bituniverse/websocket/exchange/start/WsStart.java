package com.bituniverse.websocket.exchange.start;

import com.bituniverse.websocket.netty.WebSocketService;
import com.bituniverse.websocket.netty.WebSoketClient;
import com.bituniverse.websocket.utils.PropertyUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Log4j2
public class WsStart {
    public static void start(){
        log.info("okcoin订阅方法启动");
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring/applicationContext.xml");
        PropertyUtils pt = new PropertyUtils("serverUrl.properties");
        String okcoinServer = pt.getProperty("SERVER.WEBSOCKET");
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
    }
}
