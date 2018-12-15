package com.bituniverse.websocket.service;

import com.bituniverse.websocket.entity.SuperOrder;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author: autogeneration
 * @date: 2018-12-15 15:57:18
 **/ 
public interface SuperOrderService {
    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2018-12-15 15:57:18
     **/ 
    int insert(SuperOrder record);

    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2018-12-15 15:57:18
     **/ 
    int insertSelective(SuperOrder record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2018-12-15 15:57:18
     **/ 
    int updateByPrimaryKey(SuperOrder record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2018-12-15 15:57:18
     **/ 
    int updateByPrimaryKeySelective(SuperOrder record);

    /**
     * 删除
     * 
     * @author: autogeneration
     * @date: 2018-12-15 15:57:18
     **/ 
    int deleteByPrimaryKey(Integer id);

    /**
     * 按主键查询
     * 
     * @author: autogeneration
     * @date: 2018-12-15 15:57:18
     **/ 
    SuperOrder selectByPrimaryKey(Integer id);

    /**
     * 条件查询
     * 
     * @author: autogeneration
     * @date: 2018-12-15 15:57:18
     **/ 
    List<SuperOrder> selectAll(Map<Object, Object> param);

    /**
     * 分页查询
     * 
     * @author: autogeneration
     * @date: 2018-12-15 15:57:18
     **/ 
    List<SuperOrder> selectPaging(Map<Object, Object> param);

    /**
     * 统计查询
     * 
     * @author: autogeneration
     * @date: 2018-12-15 15:57:18
     **/ 
    int selectCount(Map<Object, Object> param);

}