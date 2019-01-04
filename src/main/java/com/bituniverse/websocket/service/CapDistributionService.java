package com.bituniverse.websocket.service;

import com.bituniverse.websocket.entity.CapDistribution;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author: autogeneration
 * @date: 2019-01-04 20:04:14
 **/ 
public interface CapDistributionService {
    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:04:14
     **/ 
    int insert(CapDistribution record);

    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:04:14
     **/ 
    int insertSelective(CapDistribution record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:04:14
     **/ 
    int updateByPrimaryKey(CapDistribution record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:04:14
     **/ 
    int updateByPrimaryKeySelective(CapDistribution record);

    /**
     * 删除
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:04:14
     **/ 
    int deleteByPrimaryKey(Integer id);

    /**
     * 按主键查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:04:14
     **/ 
    CapDistribution selectByPrimaryKey(Integer id);

    /**
     * 条件查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:04:14
     **/ 
    List<CapDistribution> selectAll(Map<Object, Object> param);

    /**
     * 分页查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:04:14
     **/ 
    List<CapDistribution> selectPaging(Map<Object, Object> param);

    /**
     * 统计查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:04:14
     **/ 
    int selectCount(Map<Object, Object> param);

    void saveOrUpdate(CapDistribution capDistribution);
}