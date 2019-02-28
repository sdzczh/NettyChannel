package com.bituniverse.websocket.run;

import com.bituniverse.websocket.exchange.start.OkCoinStart;
import com.bituniverse.websocket.utils.PropertyUtils;
import com.bituniverse.websocket.netty.WebSocketService;
import com.bituniverse.websocket.netty.WebSoketClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Log4j2
public class Run {
    public static void main(String[] args) {
        OkCoinStart.start();
    }
}
