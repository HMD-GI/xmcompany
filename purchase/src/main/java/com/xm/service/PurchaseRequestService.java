package com.xm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.dto.PurchaseRequestQueryDTO;
import com.xm.dto.PurchaseRequestStatusDTO;
import com.xm.entity.PurchaseRequest;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.vo.PurchaseRequestVO;
import com.xm.vo.SimplePurchaseRequestVO;

/**
 * 采购申请服务接口
 */
public interface PurchaseRequestService extends IService<PurchaseRequest> {
    /**
     * 新增采购申请
     * @param request 采购申请信息
     * @return Result
     */
    Result addPurchaseRequest(PurchaseRequest request);

    /**
     * 更新采购申请
     * @param request 采购申请信息
     * @return Result
     */
    Result updatePurchaseRequest(PurchaseRequest request);

    /**
     * 撤回采购申请
     * @param id 采购申请ID
     * @return Result
     */
    Result withdrawPurchaseRequest(int id);

    /**
     * 变更采购申请状态
     * @param statusDTO 状态变更DTO
     * @return Result
     */
    Result updatePurchaseRequestStatus(PurchaseRequestStatusDTO statusDTO);

    /**
     * 查询采购申请详情
     * @param id 采购申请ID
     * @return Result<PurchaseRequestVO>
     */
    Result<PurchaseRequestVO> getPurchaseRequestById(int id);

    /**
     * 分页查询采购申请列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<PurchaseRequestVO>>
     */
    Result<page<SimplePurchaseRequestVO>> getPurchaseRequestList(int currentPage, int pageSize, PurchaseRequestQueryDTO queryDTO);
} 