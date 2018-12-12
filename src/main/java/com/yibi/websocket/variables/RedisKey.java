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
	public static final String KLINE = "bituniverse:kline:%s:%s:%s";

	//最新价格 USDT单价 USDT-BTC USDT-CNY
	public static final String USDT_PRICE = "bituniverse:price:usdt:%s";
	//币对最新价格  交易所：计价币：交易币  c1:c2
	public static final String COIN_PRICE = "bituniverse:price:%s:%s:%s";

	//超级大单
	public static final String SUPER_ORDER = "bituniverse:superOrder:%s";
	//24小时流入
	public static final String DAY_IN_ORDER = "bituniverse:dayState:in:%s";
	//24小时流出
	public static final String DAY_OUT_ORDER = "bituniverse:dayState:out:%s";
	//24小时净流入
	public static final String DAY_ACTUAL_ORDER = "bituniverse:dayState:actual:%s";
	//24小时净流入百分比
	public static final String DAY_ACTUALPARENT_ORDER = "bituniverse:dayState:parent:%s";
	//币种市值
	public static final String COIN_MARKET_CAP = "bituniverse:coinMarketCap:%s";

}
