package com.yibi.websocket.service;

import com.yibi.websocket.entity.OkexDealRecord;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author: autogeneration
 * @date: 2018-12-12 16:30:10
 **/ 
public interface OkexDealRecordService {
    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2018-12-12 16:30:10
     **/ 
    int insert(OkexDealRecord record);

    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2018-12-12 16:30:10
     **/ 
    int insertSelective(OkexDealRecord record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2018-12-12 16:30:10
     **/ 
    int updateByPrimaryKey(OkexDealRecord record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2018-12-12 16:30:10
     **/ 
    int updateByPrimaryKeySelective(OkexDealRecord record);

    /**
     * 删除
     * 
     * @author: autogeneration
     * @date: 2018-12-12 16:30:10
     **/ 
    int deleteByPrimaryKey(Integer id);

    /**
     * 按主键查询
     * 
     * @author: autogeneration
     * @date: 2018-12-12 16:30:10
     **/ 
    OkexDealRecord selectByPrimaryKey(Integer id);

    /**
     * 条件查询
     * 
     * @author: autogeneration
     * @date: 2018-12-12 16:30:10
     **/ 
    List<OkexDealRecord> selectAll(Map<Object, Object> param);

    /**
     * 分页查询
     * 
     * @author: autogeneration
     * @date: 2018-12-12 16:30:10
     **/ 
    List<OkexDealRecord> selectPaging(Map<Object, Object> param);

    /**
     * 统计查询
     * 
     * @author: autogeneration
     * @date: 2018-12-12 16:30:10
     **/ 
    int selectCount(Map<Object, Object> param);
}