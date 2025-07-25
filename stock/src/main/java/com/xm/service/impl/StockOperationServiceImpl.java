package com.xm.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.dto.StockOperationQueryDTO;
import com.xm.entity.StockOperation;
import com.xm.mapper.StockOperationMapper;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.StockOperationService;
import com.xm.vo.StockOperationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 库存操作记录服务实现类
 */
@Slf4j
@Service
public class StockOperationServiceImpl extends ServiceImpl<StockOperationMapper, StockOperation> implements StockOperationService {

    /**
     * 添加操作记录
     * @param stockOperation 操作记录信息
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addStockOperation(StockOperation stockOperation) {
        log.info("添加库存操作记录: {}", stockOperation);
        
        // 保存操作记录
        this.save(stockOperation);
        
        return Result.success();
    }

    /**
     * 查询操作记录详情
     * @param id 操作记录ID
     * @return Result<StockOperationVO>
     */
    @Override
    public Result<StockOperationVO> getStockOperationById(int id) {
        log.info("查询库存操作记录详情, ID: {}", id);
        
        // 查询操作记录详情
        StockOperationVO operationVO = this.baseMapper.selectStockOperationById(id);
        if (operationVO == null) {
            return Result.error("操作记录不存在");
        }
        
        return Result.success(operationVO);
    }

    /**
     * 分页查询操作记录列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<StockOperationVO>>
     */
    @Override
    public Result<page<StockOperationVO>> getStockOperationList(int currentPage, int pageSize, StockOperationQueryDTO queryDTO) {
        log.info("分页查询库存操作记录列表: 页码={}, 每页数量={}, 查询条件={}", currentPage, pageSize, queryDTO);
        
        // 创建分页参数
        Page<StockOperation> pageParam = new Page<>(currentPage, pageSize);
        
        // 执行查询
        IPage<StockOperationVO> operationPage = this.baseMapper.selectStockOperationPage(pageParam, queryDTO);
        
        // 封装结果
        page<StockOperationVO> resultPage = new page<>();
        resultPage.setPageSize(pageSize);
        resultPage.setTotal((int) operationPage.getTotal());
        resultPage.setList(operationPage.getRecords());
        
        return Result.success(resultPage);
    }
} 