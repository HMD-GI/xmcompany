package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.dto.ProductionProgressQueryDTO;
import com.xm.entity.ProductionProgress;
import com.xm.vo.ProductionProgressVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 生产进度Mapper接口
 */
@Mapper
public interface ProductionProgressMapper extends BaseMapper<ProductionProgress> {
    /**
     * 分页查询项目进度记录
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return IPage<ProductionProgressVO> 分页结果
     */
    Page<ProductionProgressVO> selectProgressPage(Page<ProductionProgress> page, @Param("query") ProductionProgressQueryDTO queryDTO);
    
    /**
     * 根据ID查询进度记录详情
     * @param id 记录ID
     * @return ProductionProgressVO 进度记录视图对象
     */
    ProductionProgressVO selectProgressById(@Param("id") int id);
}