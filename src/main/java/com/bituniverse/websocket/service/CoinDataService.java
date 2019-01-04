package com.bituniverse.websocket.service;

import com.bituniverse.websocket.entity.CoinData;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author: autogeneration
 * @date: 2019-01-04 20:12:38
 **/ 
public interface CoinDataService {
    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:12:38
     **/ 
    int insert(CoinData record);

    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:12:38
     **/ 
    int insertSelective(CoinData record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:12:38
     **/ 
    int updateByPrimaryKey(CoinData record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:12:38
     **/ 
    int updateByPrimaryKeySelective(CoinData record);

    /**
     * 删除
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:12:38
     **/ 
    int deleteByPrimaryKey(Integer id);

    /**
     * 按主键查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:12:38
     **/ 
    CoinData selectByPrimaryKey(Integer id);

    /**
     * 条件查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:12:38
     **/ 
    List<CoinData> selectAll(Map<Object, Object> param);

    /**
     * 分页查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:12:38
     **/ 
    List<CoinData> selectPaging(Map<Object, Object> param);

    /**
     * 统计查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:12:38
     **/ 
    int selectCount(Map<Object, Object> param);

    void saveOrUpdate(CoinData coinData);
}