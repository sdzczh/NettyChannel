package com.bituniverse.websocket.service.impl;

import com.bituniverse.websocket.dao.CoinPriceMapper;
import com.bituniverse.websocket.entity.CoinPrice;
import com.bituniverse.websocket.service.CoinPriceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import com.bituniverse.websocket.utils.RedisUtil;
import com.bituniverse.websocket.utils.StrUtils;
import com.bituniverse.websocket.variables.RedisKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 
 * @author: autogeneration
 * @date: 2018-12-17 13:41:23
 **/ 
@Service("coinPriceService")
public class CoinPriceServiceImpl implements CoinPriceService {
    @Resource
    private CoinPriceMapper coinPriceMapper;
    @Resource
    private RedisTemplate<String, String> redis;

    private static final Logger logger = LoggerFactory.getLogger(CoinPriceServiceImpl.class);

    @Override
    public int insert(CoinPrice record) {
        return this.coinPriceMapper.insert(record);
    }

    @Override
    public int insertSelective(CoinPrice record) {
        return this.coinPriceMapper.insertSelective(record);
    }

    @Override
    public int updateByPrimaryKey(CoinPrice record) {
        return this.coinPriceMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateByPrimaryKeySelective(CoinPrice record) {
        return this.coinPriceMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.coinPriceMapper.deleteByPrimaryKey(id);
    }

    @Override
    public CoinPrice selectByPrimaryKey(Integer id) {
        return this.coinPriceMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CoinPrice> selectAll(Map<Object, Object> param) {
        return this.coinPriceMapper.selectAll(param);
    }

    @Override
    public List<CoinPrice> selectPaging(Map<Object, Object> param) {
        return this.coinPriceMapper.selectPaging(param);
    }

    @Override
    public int selectCount(Map<Object, Object> param) {
        return this.coinPriceMapper.selectCount(param);
    }
    @Override
    public String getPrice(Integer c1, Integer c2, Integer exchangeId) {
        String key = String.format(RedisKey.COIN_PRICE, exchangeId, c2, c1);
        String price = RedisUtil.searchString(redis, key);
        if(StrUtils.isBlank(price)){
            Map<Object, Object> map = new HashMap<>();
            map.put("c1", c1);
            map.put("c2", c2);
            map.put("exchangeId", exchangeId);
            List<CoinPrice> list = coinPriceMapper.selectAll(map);
            if(list != null && list.size() != 0){
                price = list.get(0).getPrice();
                RedisUtil.addStringObj(redis, key, price);
            }
            return price;
        }else{
            return price;
        }
    }
}