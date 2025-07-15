package com.xm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.dto.SupplierQueryDTO;
import com.xm.dto.SupplierStatusDTO;
import com.xm.entity.Supplier;
import com.xm.mapper.SupplierMapper;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.SupplierService;
import com.xm.vo.SupplierVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import com.xm.utils.RedisIdGenerator;

/**
 * 供应商服务实现类
 */
@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    @Autowired
    private SupplierMapper supplierMapper;

    @Autowired
    private RedisIdGenerator redisIdGenerator; // 注入Redis主键生成器

    /**
     * 添加供应商
     * @param supplier 供应商信息
     * @return Result
     */
    @Override
    @Transactional
    public Result addSupplier(Supplier supplier) {
        // 校验供应商编号是否已存在
        LambdaQueryWrapper<Supplier> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Supplier::getCode, supplier.getCode());
        if (this.count(queryWrapper) > 0) {
            return Result.error("供应商编号已存在");
        }
        // 通过Redis生成供应商主键ID
        int id = redisIdGenerator.generateId("supplier");
        supplier.setId(id); // 设置主键ID
        // 设置初始状态和时间
        supplier.setStatus(1); // 默认状态为"合作中"
        supplier.setCreateTime(LocalDateTime.now());
        supplier.setUpdateTime(LocalDateTime.now());
        // 保存供应商信息
        boolean success = this.save(supplier);
        if (success) {
            return Result.success("添加供应商成功");
        } else {
            return Result.error("添加供应商失败");
        }
    }

    /**
     * 更新供应商信息
     * @param supplier 供应商信息
     * @return Result
     */
    @Override
    @Transactional
    public Result updateSupplier(Supplier supplier) {
        // 校验供应商是否存在
        Supplier existingSupplier = this.getById(supplier.getId());
        if (existingSupplier == null) {
            return Result.error("供应商不存在");
        }
        
        // 校验供应商编号是否与其他供应商重复
        LambdaQueryWrapper<Supplier> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Supplier::getCode, supplier.getCode())
                   .ne(Supplier::getId, supplier.getId());
        if (this.count(queryWrapper) > 0) {
            return Result.error("供应商编号已被其他供应商使用");
        }
        
        // 设置更新时间
        supplier.setUpdateTime(LocalDateTime.now());
        
        // 保留创建时间不变
        supplier.setCreateTime(existingSupplier.getCreateTime());
        
        // 更新供应商信息
        boolean success = this.updateById(supplier);
        
        if (success) {
            return Result.success("更新供应商成功");
        } else {
            return Result.error("更新供应商失败");
        }
    }

    /**
     * 删除供应商
     * @param id 供应商ID
     * @return Result
     */
    @Override
    @Transactional
    public Result deleteSupplier(int id) {
        // 校验供应商是否存在
        if (!this.getBaseMapper().exists(new LambdaQueryWrapper<Supplier>().eq(Supplier::getId, id))) {
            return Result.error("供应商不存在");
        }
        
        // 删除供应商
        boolean success = this.removeById(id);
        
        if (success) {
            return Result.success("删除供应商成功");
        } else {
            return Result.error("删除供应商失败");
        }
    }

    /**
     * 根据ID查询供应商
     * @param id 供应商ID
     * @return Result<SupplierVO>
     */
    @Override
    public Result<SupplierVO> getSupplierById(int id) {
        // 查询供应商
        Supplier supplier = this.getById(id);
        
        if (supplier == null) {
            return Result.error("供应商不存在");
        }
        
        // 转换为VO对象
        SupplierVO supplierVO = new SupplierVO();
        BeanUtils.copyProperties(supplier, supplierVO);
        
        // 设置状态名称
        supplierVO.setStatusName(getStatusName(supplier.getStatus()));
        
        return Result.success(supplierVO);
    }

    /**
     * 分页查询供应商列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<SupplierVO>>
     */
    @Override
    public Result<page<SupplierVO>> getSupplierList(int currentPage, int pageSize, SupplierQueryDTO queryDTO) {
        // 创建分页对象
        Page<SupplierVO> pageParams = new Page<>(currentPage, pageSize);
        
        // 查询供应商列表
        IPage<SupplierVO> pageResult = supplierMapper.selectSupplierPage(pageParams, queryDTO);
        
        // 转换为自定义分页对象
        page<SupplierVO> resultPage = new page<>();
        resultPage.setList(pageResult.getRecords());
        resultPage.setTotal((int) pageResult.getTotal());
        resultPage.setPageSize(pageSize);
        
        return Result.success(resultPage);
    }

    /**
     * 更新供应商状态
     * @param statusDTO 状态更新DTO
     * @return Result
     */
    @Override
    @Transactional
    public Result updateSupplierStatus(SupplierStatusDTO statusDTO) {
        // 校验状态值
        if (statusDTO.getStatus() < 1 || statusDTO.getStatus() > 3) {
            return Result.error("无效的状态值");
        }
        
        // 校验供应商是否存在
        Supplier supplier = this.getById(statusDTO.getId());
        if (supplier == null) {
            return Result.error("供应商不存在");
        }
        
        // 更新状态
        supplier.setStatus(statusDTO.getStatus());
        supplier.setUpdateTime(LocalDateTime.now());
        
        // 如果有备注，更新备注
        if (statusDTO.getRemark() != null && !statusDTO.getRemark().isEmpty()) {
            supplier.setRemark(statusDTO.getRemark());
        }
        
        // 保存更新
        boolean success = this.updateById(supplier);
        
        if (success) {
            return Result.success("更新供应商状态成功");
        } else {
            return Result.error("更新供应商状态失败");
        }
    }
    
    /**
     * 获取状态名称
     * @param status 状态值
     * @return 状态名称
     */
    private String getStatusName(int status) {
        switch (status) {
            case 1:
                return "合作中";
            case 2:
                return "已暂停";
            case 3:
                return "已终止";
            default:
                return "未知状态";
        }
    }
} 