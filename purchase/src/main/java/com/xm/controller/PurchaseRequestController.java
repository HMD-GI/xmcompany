package com.xm.controller;

import com.xm.dto.PurchaseRequestQueryDTO;
import com.xm.dto.PurchaseRequestStatusDTO;
import com.xm.entity.PurchaseRequest;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.PurchaseRequestService;
import com.xm.vo.PurchaseRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 采购申请管理控制器
 */
@Tag(name = "采购申请管理", description = "采购申请的增删改查相关接口")
@Slf4j
@RestController
@RequestMapping("/xm/purchase-request")
public class PurchaseRequestController {

    @Autowired
    private PurchaseRequestService purchaseRequestService; // 注入采购申请服务

    /**
     * 新增采购申请
     * @param request 采购申请信息
     * @return Result
     */
    @Operation(summary = "新增采购申请", description = "新增采购申请信息")
    @PostMapping
    public Result addPurchaseRequest(@RequestBody PurchaseRequest request) {
        log.info("新增采购申请: {}", request);
        return purchaseRequestService.addPurchaseRequest(request);
    }

    /**
     * 更新采购申请
     * @param request 采购申请信息
     * @return Result
     */
    @Operation(summary = "更新采购申请", description = "修改采购申请信息")
    @PutMapping
    public Result updatePurchaseRequest(@RequestBody PurchaseRequest request) {
        log.info("更新采购申请: {}", request);
        return purchaseRequestService.updatePurchaseRequest(request);
    }

    /**
     * 撤回采购申请
     * @param id 采购申请ID
     * @return Result
     */
    @Operation(summary = "撤回采购申请", description = "撤回采购申请")
    @PutMapping("/withdraw/{id}")
    public Result withdrawPurchaseRequest(@PathVariable int id) {
        log.info("撤回采购申请, ID: {}", id);
        return purchaseRequestService.withdrawPurchaseRequest(id);
    }

    /**
     * 变更采购申请状态
     * @param statusDTO 状态变更DTO
     * @return Result
     */
    @Operation(summary = "变更采购申请状态", description = "修改采购申请状态")
    @PutMapping("/status")
    public Result updatePurchaseRequestStatus(@RequestBody PurchaseRequestStatusDTO statusDTO) {
        log.info("变更采购申请状态: {}", statusDTO);
        return purchaseRequestService.updatePurchaseRequestStatus(statusDTO);
    }

    /**
     * 查询采购申请详情
     * @param id 采购申请ID
     * @return Result<PurchaseRequestVO>
     */
    @Operation(summary = "查询采购申请详情", description = "根据ID查询采购申请详细信息")
    @GetMapping("/{id}")
    public Result<PurchaseRequestVO> getPurchaseRequestById(@PathVariable int id) {
        log.info("查询采购申请详情, ID: {}", id);
        return purchaseRequestService.getPurchaseRequestById(id);
    }

    /**
     * 分页查询采购申请列表
     * @param currentPage 当前页码，默认为1
     * @param pageSize 每页显示数量，默认为10
     * @param queryDTO 查询条件
     * @return Result<page<PurchaseRequestVO>>
     */
    @Operation(summary = "分页查询采购申请", description = "根据条件分页查询采购申请列表")
    @GetMapping("/list")
    public Result<page<PurchaseRequestVO>> getPurchaseRequestList(
            @RequestParam(defaultValue = "1") int currentPage,
            @RequestParam(defaultValue = "10") int pageSize,
            PurchaseRequestQueryDTO queryDTO) {
        log.info("分页查询采购申请列表: 页码={}, 每页数量={}, 查询条件={}", currentPage, pageSize, queryDTO);
        return purchaseRequestService.getPurchaseRequestList(currentPage, pageSize, queryDTO);
    }
} 