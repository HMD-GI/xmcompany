package com.xm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.dto.StockInDTO;
import com.xm.dto.StockOutDTO;
import com.xm.dto.StockQueryDTO;
import com.xm.entity.Stock;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.vo.StockVO;

/**
 * 库存服务接口
 */
public interface StockService extends IService<Stock> {
    /**
     * 入库操作
     * @param stockInDTO 入库信息
     * @return Result
     */
    Result stockIn(StockInDTO stockInDTO);

    /**
     * 出库操作
     * @param stockOutDTO 出库信息
     * @return Result
     */
    Result stockOut(StockOutDTO stockOutDTO);

    /**
     * 更新库存信息
     * @param stock 库存信息
     * @return Result
     */
    Result updateStock(Stock stock);

    /**
     * 查询库存详情
     * @param id 库存ID
     * @return Result<StockVO>
     */
    Result<StockVO> getStockById(int id);

    /**
     * 根据产品名称查询库存详情
     * @param productName 产品名称
     * @return Result<StockVO>
     */
    StockVO getStockByProductName(String productName);

    /**
     * 分页查询库存列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<StockVO>>
     */
    Result<page<StockVO>> getStockList(int currentPage, int pageSize, StockQueryDTO queryDTO);
} 