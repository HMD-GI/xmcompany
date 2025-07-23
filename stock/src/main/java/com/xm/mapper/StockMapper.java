package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.dto.StockQueryDTO;
import com.xm.entity.Stock;
import com.xm.vo.StockVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 库存Mapper接口
 */
@Mapper
public interface StockMapper extends BaseMapper<Stock> {
    /**
     * 分页查询库存信息
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return IPage<StockVO> 分页结果
     */
    IPage<StockVO> selectStockPage(Page<Stock> page, @Param("query") StockQueryDTO queryDTO);
    
    /**
     * 根据ID查询库存详情
     * @param id 库存ID
     * @return StockVO 库存视图对象
     */
    StockVO selectStockById(@Param("id") int id);
    
    /**
     * 根据物料ID和仓库位置查询库存
     * @param materialId 物料ID
     * @param warehouseLocation 仓库位置
     * @return StockVO 库存视图对象
     */
    StockVO selectStockByMaterialAndWarehouse(@Param("materialId") int materialId, @Param("warehouseLocation") String warehouseLocation);

    /**
     * 根据产品名称查询库存详情
     * @param productName 产品名称
     * @return StockVO 库存详情
     */
    StockVO selectStockByProductName(@Param("productName") String productName);
}