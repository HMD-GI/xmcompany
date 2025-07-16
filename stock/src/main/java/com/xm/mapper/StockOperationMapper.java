package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.dto.StockOperationQueryDTO;
import com.xm.entity.StockOperation;
import com.xm.vo.StockOperationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 库存操作记录Mapper接口
 */
@Mapper
public interface StockOperationMapper extends BaseMapper<StockOperation> {
    /**
     * 分页查询库存操作记录
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return IPage<StockOperationVO> 分页结果
     */
    IPage<StockOperationVO> selectStockOperationPage(Page<StockOperation> page, @Param("query") StockOperationQueryDTO queryDTO);
    
    /**
     * 根据ID查询库存操作记录详情
     * @param id 操作记录ID
     * @return StockOperationVO 库存操作记录视图对象
     */
    StockOperationVO selectStockOperationById(@Param("id") int id);
} 