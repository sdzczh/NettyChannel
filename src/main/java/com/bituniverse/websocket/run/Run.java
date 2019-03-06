package com.bituniverse.websocket.run;

import com.bituniverse.websocket.exchange.start.OkCoinStart;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Run {
    public static void main(String[] args) {
        OkCoinStart.start();
    }
}
