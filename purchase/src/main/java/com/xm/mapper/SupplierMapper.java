package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.dto.SupplierQueryDTO;
import com.xm.entity.Supplier;
import com.xm.vo.SupplierVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 供应商Mapper接口
 */
@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {
    
    /**
     * 分页查询供应商列表
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return 供应商VO分页数据
     */
    IPage<SupplierVO> selectSupplierPage(Page<SupplierVO> page, @Param("query") SupplierQueryDTO queryDTO);
} 