package com.bituniverse.websocket.service.impl;

import com.bituniverse.websocket.dao.CapDistributionMapper;
import com.bituniverse.websocket.entity.CapDistribution;
import com.bituniverse.websocket.service.CapDistributionService;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 
 * @author: autogeneration
 * @date: 2018-12-17 17:16:57
 **/ 
@Service("capDistributionService")
public class CapDistributionServiceImpl implements CapDistributionService {
    @Resource
    private CapDistributionMapper capDistributionMapper;

    private static final Logger logger = LoggerFactory.getLogger(CapDistributionServiceImpl.class);

    @Override
    public int insert(CapDistribution record) {
        return this.capDistributionMapper.insert(record);
    }

    @Override
    public int insertSelective(CapDistribution record) {
        return this.capDistributionMapper.insertSelective(record);
    }

    @Override
    public int updateByPrimaryKey(CapDistribution record) {
        return this.capDistributionMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateByPrimaryKeySelective(CapDistribution record) {
        return this.capDistributionMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.capDistributionMapper.deleteByPrimaryKey(id);
    }

    @Override
    public CapDistribution selectByPrimaryKey(Integer id) {
        return this.capDistributionMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<CapDistribution> selectAll(Map<Object, Object> param) {
        return this.capDistributionMapper.selectAll(param);
    }

    @Override
    public List<CapDistribution> selectPaging(Map<Object, Object> param) {
        return this.capDistributionMapper.selectPaging(param);
    }

    @Override
    public int selectCount(Map<Object, Object> param) {
        return this.capDistributionMapper.selectCount(param);
    }

    @Override
    public void saveOrUpdate(CapDistribution capDistribution) {
        if(capDistribution.getId() == null){
            this.insertSelective(capDistribution);
        }else{
            this.updateByPrimaryKeySelective(capDistribution);
        }
    }
}