package com.bituniverse.websocket.service;

import com.bituniverse.websocket.entity.OkexDealRecord;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author: autogeneration
 * @date: 2019-01-04 20:38:46
 **/ 
public interface OkexDealRecordService {
    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:38:46
     **/ 
    int insert(OkexDealRecord record);

    /**
     * 添加
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:38:46
     **/ 
    int insertSelective(OkexDealRecord record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:38:46
     **/ 
    int updateByPrimaryKey(OkexDealRecord record);

    /**
     * 更新
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:38:46
     **/ 
    int updateByPrimaryKeySelective(OkexDealRecord record);

    /**
     * 删除
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:38:46
     **/ 
    int deleteByPrimaryKey(Integer id);

    /**
     * 按主键查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:38:46
     **/ 
    OkexDealRecord selectByPrimaryKey(Integer id);

    /**
     * 条件查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:38:46
     **/ 
    List<OkexDealRecord> selectAll(Map<Object, Object> param);

    /**
     * 分页查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:38:46
     **/ 
    List<OkexDealRecord> selectPaging(Map<Object, Object> param);

    /**
     * 统计查询
     * 
     * @author: autogeneration
     * @date: 2019-01-04 20:38:46
     **/ 
    int selectCount(Map<Object, Object> param);
}