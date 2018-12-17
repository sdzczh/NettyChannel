package com.bituniverse.websocket.service;

import com.bituniverse.websocket.entity.CapDistribution;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author: autogeneration
 * @date: 2018-12-17 17:16:57
 **/ 
public interface CapDistributionService {
    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2018-12-17 17:16:57
     **/ 
    int insert(CapDistribution record);

    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2018-12-17 17:16:57
     **/ 
    int insertSelective(CapDistribution record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2018-12-17 17:16:57
     **/ 
    int updateByPrimaryKey(CapDistribution record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2018-12-17 17:16:57
     **/ 
    int updateByPrimaryKeySelective(CapDistribution record);

    /**
     * 删除
     * 
     * @author: autogeneration
     * @date: 2018-12-17 17:16:57
     **/ 
    int deleteByPrimaryKey(Integer id);

    /**
     * 按主键查询
     * 
     * @author: autogeneration
     * @date: 2018-12-17 17:16:57
     **/ 
    CapDistribution selectByPrimaryKey(Integer id);

    /**
     * 条件查询
     * 
     * @author: autogeneration
     * @date: 2018-12-17 17:16:57
     **/ 
    List<CapDistribution> selectAll(Map<Object, Object> param);

    /**
     * 分页查询
     * 
     * @author: autogeneration
     * @date: 2018-12-17 17:16:57
     **/ 
    List<CapDistribution> selectPaging(Map<Object, Object> param);

    /**
     * 统计查询
     * 
     * @author: autogeneration
     * @date: 2018-12-17 17:16:57
     **/ 
    int selectCount(Map<Object, Object> param);

    void saveOrUpdate(CapDistribution capDistribution);
}