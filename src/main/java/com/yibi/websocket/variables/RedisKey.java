package com.yibi.websocket.variables;

/**
 * @描述 Redis-Key值声明类<br>
 * @author administrator
 * @版本 v1.0.0
 * @日期 2017-10-13
 */
public class RedisKey {

	public static final String TEST="TEST";
	
	public static final String SYSTEM_PARAM = "bituniverse:systemParam:%s";
	
	public static final String TABLE_NAME = "bituniverse:tableName:%s";
	
	public static final String LATEST_TRANS_PRICE = "bituniverse:order:%s:%s:price";/*最新成交价*/
	
	//k线数据  时间间隔类型（1:1min,2:5min,3:30min,4:1hour,5:1day）:计价币种:交易币种
	public static final String KLINEOKEX = "bituniverse:kline:okcoin:%s:%s";
	public static final String KLINEHUOBI = "bituniverse:kline:huobi:%s:%s";
	public static final String KLINEBINANCE = "bituniverse:kline:binance:%s:%s";

	
}
