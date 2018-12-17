package com.bituniverse.websocket.service;

import com.bituniverse.websocket.entity.DayState;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author: autogeneration
 * @date: 2018-12-17 16:44:03
 **/ 
public interface DayStateService {
    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2018-12-17 16:44:03
     **/ 
    int insert(DayState record);

    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2018-12-17 16:44:03
     **/ 
    int insertSelective(DayState record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2018-12-17 16:44:03
     **/ 
    int updateByPrimaryKey(DayState record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2018-12-17 16:44:03
     **/ 
    int updateByPrimaryKeySelective(DayState record);

    /**
     * 删除
     * 
     * @author: autogeneration
     * @date: 2018-12-17 16:44:03
     **/ 
    int deleteByPrimaryKey(Integer id);

    /**
     * 按主键查询
     * 
     * @author: autogeneration
     * @date: 2018-12-17 16:44:03
     **/ 
    DayState selectByPrimaryKey(Integer id);

    /**
     * 条件查询
     * 
     * @author: autogeneration
     * @date: 2018-12-17 16:44:03
     **/ 
    List<DayState> selectAll(Map<Object, Object> param);

    /**
     * 分页查询
     * 
     * @author: autogeneration
     * @date: 2018-12-17 16:44:03
     **/ 
    List<DayState> selectPaging(Map<Object, Object> param);

    /**
     * 统计查询
     * 
     * @author: autogeneration
     * @date: 2018-12-17 16:44:03
     **/ 
    int selectCount(Map<Object, Object> param);
}