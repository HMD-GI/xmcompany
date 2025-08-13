package com.xm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.dto.*;
import com.xm.entity.SaleOrder;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.vo.SaleOrderVO;
import com.xm.vo.SimpleSaleOrderVO;

/**
 * 销售订单服务接口
 */
public interface SaleOrderService extends IService<SaleOrder> {
    /**
     * 新增销售订单
     * @param addDTO 新增信息
     * @return Result
     */
    Result addSaleOrder(SaleOrderAddDTO addDTO);
    
    /**
     * 更新销售订单
     * @param updateDTO 更新信息
     * @return Result
     */
    Result updateSaleOrder(SaleOrderUpdateDTO updateDTO);
    
    /**
     * 销售订单发货
     * @param shipDTO 发货信息
     * @return Result
     */
    Result shipSaleOrder(SaleOrderShipDTO shipDTO);
    
    /**
     * 更新订单状态
     * @param statusUpdateDTO 状态更新信息
     * @return Result
     */
    Result updateSaleOrderStatus(SaleOrderStatusUpdateDTO statusUpdateDTO);
    
    /**
     * 查询销售订单详情
     * @param id 订单ID
     * @return Result<SaleOrderVO>
     */
    Result<SaleOrderVO> getSaleOrderById(int id);
    
    /**
     * 分页查询销售订单列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<SaleOrderVO>>
     */
    Result<page<SimpleSaleOrderVO>> getSaleOrderList(int currentPage, int pageSize, SaleOrderQueryDTO queryDTO);
}