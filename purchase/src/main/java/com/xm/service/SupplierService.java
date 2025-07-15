package com.xm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xm.dto.SupplierQueryDTO;
import com.xm.dto.SupplierStatusDTO;
import com.xm.entity.Supplier;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.vo.SupplierVO;

/**
 * 供应商服务接口
 */
public interface SupplierService extends IService<Supplier> {
    
    /**
     * 添加供应商
     * @param supplier 供应商信息
     * @return Result
     */
    Result addSupplier(Supplier supplier);
    
    /**
     * 更新供应商信息
     * @param supplier 供应商信息
     * @return Result
     */
    Result updateSupplier(Supplier supplier);
    
    /**
     * 删除供应商
     * @param id 供应商ID
     * @return Result
     */
    Result deleteSupplier(int id);
    
    /**
     * 根据ID查询供应商
     * @param id 供应商ID
     * @return Result<SupplierVO>
     */
    Result<SupplierVO> getSupplierById(int id);
    
    /**
     * 分页查询供应商列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<SupplierVO>>
     */
    Result<page<SupplierVO>> getSupplierList(int currentPage, int pageSize, SupplierQueryDTO queryDTO);
    
    /**
     * 更新供应商状态
     * @param statusDTO 状态更新DTO
     * @return Result
     */
    Result updateSupplierStatus(SupplierStatusDTO statusDTO);
} 