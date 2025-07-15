package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.dto.PurchaseOrderQueryDTO;
import com.xm.entity.PurchaseOrder;
import com.xm.vo.PurchaseOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 采购订单Mapper接口
 */
@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {
    /**
     * 分页查询采购订单列表
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return 采购订单VO分页数据
     */
    IPage<PurchaseOrderVO> selectPurchaseOrderPage(Page<PurchaseOrderVO> page, @Param("query") PurchaseOrderQueryDTO queryDTO);
    
    /**
     * 根据ID查询采购订单详情
     * @param id 采购订单ID
     * @return 采购订单VO
     */
    PurchaseOrderVO getPurchaseOrderById(@Param("id") int id);
} 