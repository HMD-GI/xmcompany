package com.xm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.dto.PurchaseOrderQueryDTO;
import com.xm.dto.PurchaseOrderStatusDTO;
import com.xm.entity.PurchaseOrder;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.vo.PurchaseOrderVO;
import com.xm.vo.SimplePurchaseOrderVO;

/**
 * 采购订单服务接口
 */
public interface PurchaseOrderService extends IService<PurchaseOrder> {
    /**
     * 新增采购订单
     * @param order 采购订单信息
     * @return Result
     */
    Result addPurchaseOrder(PurchaseOrder order);

    /**
     * 更新采购订单
     * @param order 采购订单信息
     * @return Result
     */
    Result updatePurchaseOrder(PurchaseOrder order);

    /**
     * 作废采购订单
     * @param id 采购订单ID
     * @return Result
     */
    Result cancelPurchaseOrder(int id);

    /**
     * 变更采购订单状态
     * @param statusDTO 状态变更DTO
     * @return Result
     */
    Result updatePurchaseOrderStatus(PurchaseOrderStatusDTO statusDTO);

    /**
     * 查询采购订单详情
     * @param id 采购订单ID
     * @return Result<PurchaseOrderVO>
     */
    Result<PurchaseOrderVO> getPurchaseOrderById(int id);

    /**
     * 分页查询采购订单列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<PurchaseOrderVO>>
     */
    Result<page<SimplePurchaseOrderVO>> getPurchaseOrderList(int currentPage, int pageSize, PurchaseOrderQueryDTO queryDTO);
} 