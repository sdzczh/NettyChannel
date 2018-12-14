package com.bituniverse.websocket.service;

import com.bituniverse.websocket.entity.CoinData;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author: autogeneration
 * @date: 2018-12-14 22:30:09
 **/ 
public interface CoinDataService {
    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2018-12-14 22:30:09
     **/ 
    int insert(CoinData record);

    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2018-12-14 22:30:09
     **/ 
    int insertSelective(CoinData record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2018-12-14 22:30:09
     **/ 
    int updateByPrimaryKey(CoinData record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2018-12-14 22:30:09
     **/ 
    int updateByPrimaryKeySelective(CoinData record);

    /**
     * 删除
     * 
     * @author: autogeneration
     * @date: 2018-12-14 22:30:09
     **/ 
    int deleteByPrimaryKey(Integer id);

    /**
     * 按主键查询
     * 
     * @author: autogeneration
     * @date: 2018-12-14 22:30:09
     **/ 
    CoinData selectByPrimaryKey(Integer id);

    /**
     * 条件查询
     * 
     * @author: autogeneration
     * @date: 2018-12-14 22:30:09
     **/ 
    List<CoinData> selectAll(Map<Object, Object> param);

    /**
     * 分页查询
     * 
     * @author: autogeneration
     * @date: 2018-12-14 22:30:09
     **/ 
    List<CoinData> selectPaging(Map<Object, Object> param);

    /**
     * 统计查询
     * 
     * @author: autogeneration
     * @date: 2018-12-14 22:30:09
     **/ 
    int selectCount(Map<Object, Object> param);
}