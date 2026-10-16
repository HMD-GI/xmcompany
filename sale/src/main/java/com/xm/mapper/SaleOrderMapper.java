package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.dto.SaleOrderQueryDTO;
import com.xm.entity.SaleOrder;
import com.xm.vo.SaleOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 销售订单Mapper接口
 */
@Mapper
public interface SaleOrderMapper extends BaseMapper<SaleOrder> {
    /**
     * 分页查询销售订单
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return Page<SaleOrderVO>
     */
    Page<SaleOrderVO> selectSaleOrderPage(Page<SaleOrder> page, @Param("query") SaleOrderQueryDTO queryDTO);
    
    /**
     * 根据ID查询销售订单详情
     * @param id 订单ID
     * @return SaleOrderVO
     */
    SaleOrderVO selectSaleOrderById(@Param("id") int id);
}