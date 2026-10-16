package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.dto.ProductionProjectQueryDTO;
import com.xm.entity.ProductionProject;
import com.xm.vo.ProductionProjectVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 生产项目Mapper接口
 */
@Mapper
public interface ProductionProjectMapper extends BaseMapper<ProductionProject> {
    /**
     * 分页查询生产项目信息
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return IPage<ProductionProjectVO> 分页结果
     */
    IPage<ProductionProjectVO> selectProjectPage(Page<ProductionProject> page, @Param("query") ProductionProjectQueryDTO queryDTO);
    
    /**
     * 根据ID查询生产项目详情
     * @param id 项目ID
     * @return ProductionProjectVO 生产项目视图对象
     */
    ProductionProjectVO selectProjectById(@Param("id") int id);
}