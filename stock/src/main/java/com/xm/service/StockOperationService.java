package com.xm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.dto.StockOperationQueryDTO;
import com.xm.entity.StockOperation;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.vo.SimpleStockOperationVO;
import com.xm.vo.StockOperationVO;

/**
 * 库存操作记录服务接口
 */
public interface StockOperationService extends IService<StockOperation> {
    /**
     * 添加操作记录
     * @param stockOperation 操作记录信息
     * @return Result
     */
    Result addStockOperation(StockOperation stockOperation);

    /**
     * 查询操作记录详情
     * @param id 操作记录ID
     * @return Result<StockOperationVO>
     */
    Result<StockOperationVO> getStockOperationById(int id);

    /**
     * 分页查询操作记录列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<StockOperationVO>>
     */
    Result<page<SimpleStockOperationVO>> getStockOperationList(int currentPage, int pageSize, StockOperationQueryDTO queryDTO);
} 