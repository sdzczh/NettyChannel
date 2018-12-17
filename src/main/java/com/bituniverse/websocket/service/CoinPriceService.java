package com.bituniverse.websocket.service;

import com.bituniverse.websocket.entity.CoinPrice;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author: autogeneration
 * @date: 2018-12-17 13:41:23
 **/ 
public interface CoinPriceService {
    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2018-12-17 13:41:23
     **/ 
    int insert(CoinPrice record);

    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2018-12-17 13:41:23
     **/ 
    int insertSelective(CoinPrice record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2018-12-17 13:41:23
     **/ 
    int updateByPrimaryKey(CoinPrice record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2018-12-17 13:41:23
     **/ 
    int updateByPrimaryKeySelective(CoinPrice record);

    /**
     * 删除
     * 
     * @author: autogeneration
     * @date: 2018-12-17 13:41:23
     **/ 
    int deleteByPrimaryKey(Integer id);

    /**
     * 按主键查询
     * 
     * @author: autogeneration
     * @date: 2018-12-17 13:41:23
     **/ 
    CoinPrice selectByPrimaryKey(Integer id);

    /**
     * 条件查询
     * 
     * @author: autogeneration
     * @date: 2018-12-17 13:41:23
     **/ 
    List<CoinPrice> selectAll(Map<Object, Object> param);

    /**
     * 分页查询
     * 
     * @author: autogeneration
     * @date: 2018-12-17 13:41:23
     **/ 
    List<CoinPrice> selectPaging(Map<Object, Object> param);

    /**
     * 统计查询
     * 
     * @author: autogeneration
     * @date: 2018-12-17 13:41:23
     **/ 
    int selectCount(Map<Object, Object> param);
    String getPrice(Integer c1, Integer c2, Integer exchangeId);
}