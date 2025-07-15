package com.xm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.dto.PurchaseRequestQueryDTO;
import com.xm.entity.PurchaseRequest;
import com.xm.vo.PurchaseRequestVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 采购申请Mapper接口
 */
@Mapper
public interface PurchaseRequestMapper extends BaseMapper<PurchaseRequest> {
    /**
     * 分页查询采购申请列表
     * @param page 分页参数
     * @param queryDTO 查询条件
     * @return 采购申请VO分页数据
     */
    IPage<PurchaseRequestVO> selectPurchaseRequestPage(Page<PurchaseRequestVO> page, @Param("query") PurchaseRequestQueryDTO queryDTO);
} 