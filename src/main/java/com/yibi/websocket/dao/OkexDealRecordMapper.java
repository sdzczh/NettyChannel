package com.yibi.websocket.dao;

import com.yibi.websocket.entity.OkexDealRecord;
import java.util.List;
import java.util.Map;

public interface OkexDealRecordMapper {
    int insert(OkexDealRecord record);

    int insertSelective(OkexDealRecord record);

    int updateByPrimaryKey(OkexDealRecord record);

    int updateByPrimaryKeySelective(OkexDealRecord record);

    int deleteByPrimaryKey(Integer id);

    OkexDealRecord selectByPrimaryKey(Integer id);

    List<OkexDealRecord> selectAll(Map<Object, Object> param);

    List<OkexDealRecord> selectPaging(Map<Object, Object> param);

    int selectCount(Map<Object, Object> param);
}