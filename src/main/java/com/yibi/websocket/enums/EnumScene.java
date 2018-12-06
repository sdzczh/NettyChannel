package com.yibi.websocket.enums;

public enum EnumScene {
	SCENEN_INDEX(300,"首页",300),
	SCENEN_KLINE(400,"详情页",400),
	/* XXX-X-X 场景 交易所1okex  币种1btc*/
	SCENEN_INDEX_OKEX_BTC(30011,"首页",300),
	SCENEN_KLINE_OKEX_BTC(40011,"首页",400),
	;

	private final int scene;
	private final int type;
	private final String desc;
	EnumScene(int scene, String desc, int type) {
		this.scene = scene;
		this.desc = desc;
		this.type = type;
	}

	/**
	 * 场景
	 * @return
	 */
	public int getScene(){
		return this.scene;
	}

	/**
	 * 描述
	 * @return
	 */
	public String getDesc(){
		return this.desc;
	}
	public int getType(){
		return this.type;
	}
	
}
