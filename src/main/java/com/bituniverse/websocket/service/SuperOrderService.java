package com.bituniverse.websocket.service;

import com.bituniverse.websocket.entity.SuperOrder;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author: autogeneration
 * @date: 2019-01-04 20:50:11
 **/ 
public interface SuperOrderService {
    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:50:11
     **/ 
    int insert(SuperOrder record);

    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:50:11
     **/ 
    int insertSelective(SuperOrder record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:50:11
     **/ 
    int updateByPrimaryKey(SuperOrder record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:50:11
     **/ 
    int updateByPrimaryKeySelective(SuperOrder record);

    /**
     * 删除
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:50:11
     **/ 
    int deleteByPrimaryKey(Integer id);

    /**
     * 按主键查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:50:11
     **/ 
    SuperOrder selectByPrimaryKey(Integer id);

    /**
     * 条件查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:50:11
     **/ 
    List<SuperOrder> selectAll(Map<Object, Object> param);

    /**
     * 分页查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:50:11
     **/ 
    List<SuperOrder> selectPaging(Map<Object, Object> param);

    /**
     * 统计查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:50:11
     **/ 
    int selectCount(Map<Object, Object> param);
}