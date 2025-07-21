package com.xm.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.dto.*;
import com.xm.entity.SaleOrder;
import com.xm.mapper.SaleOrderMapper;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.SaleOrderService;
import com.xm.service.StockService;
import com.xm.utils.RedisIdGenerator;
import com.xm.utils.UserContext;
import com.xm.vo.SaleOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 销售订单服务实现类
 */
@Slf4j
@Service
public class SaleOrderServiceImpl extends ServiceImpl<SaleOrderMapper, SaleOrder> implements SaleOrderService {

    @Autowired
    private SaleOrderMapper saleOrderMapper; // 注入销售订单Mapper

    @Autowired
    private RedisIdGenerator redisIdGenerator; // redisID生成器
    
    @Autowired
    private StockService stockService; // 注入库存服务
    
    /**
     * 新增销售订单
     * @param addDTO 新增信息
     * @return Result
     */
    @Override
    @Transactional
    public Result addSaleOrder(SaleOrderAddDTO addDTO) {
        // 参数校验
        if (addDTO.getQuantity() <= 0) {
            return Result.error("订单数量必须大于0");
        }
        
        // 创建销售订单对象
        SaleOrder saleOrder = new SaleOrder();
        BeanUtils.copyProperties(addDTO, saleOrder);
        
        // 设置订单编号：SO + 当前时间戳后8位 + 3位随机数
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(5);
        String randomNum = String.format("%03d", new Random().nextInt(1000));
        saleOrder.setOrderNo("SO" + timestamp + randomNum);
        
        // 设置订单状态为待处理
        saleOrder.setStatus(0);
        
        // 设置操作人信息（实际项目中应从登录用户中获取）
        saleOrder.setOperatorId(UserContext.getCurrentEmployeeId()); // 示例：假设操作人ID为1
        saleOrder.setOperatorName(UserContext.getCurrentUsername()); // 示例：假设操作人姓名为管理员
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        saleOrder.setCreateTime(now);
        saleOrder.setUpdateTime(now);
        
        // 生成订单ID（实际项目中可能使用分布式ID生成器）
        saleOrder.setId(redisIdGenerator.generateId("sale"));
        
        // 保存销售订单
        save(saleOrder);
        
        log.info("新增销售订单成功，订单编号：{}", saleOrder.getOrderNo());
        return Result.success();
    }
    
    /**
     * 更新销售订单
     * @param updateDTO 更新信息
     * @return Result
     */
    @Override
    @Transactional
    public Result updateSaleOrder(SaleOrderUpdateDTO updateDTO) {
        // 参数校验
        if (updateDTO.getId() <= 0) {
            return Result.error("订单ID无效");
        }
        if (updateDTO.getQuantity() <= 0) {
            return Result.error("订单数量必须大于0");
        }
        
        // 查询订单是否存在
        SaleOrder existOrder = getById(updateDTO.getId());
        if (existOrder == null) {
            return Result.error("订单不存在");
        }
        
        // 检查订单状态，只有待处理和已确认状态的订单才能修改
        if (existOrder.getStatus() > 1) {
            return Result.error("当前订单状态不允许修改");
        }
        
        // 创建更新对象
        SaleOrder saleOrder = new SaleOrder();
        BeanUtils.copyProperties(updateDTO, saleOrder);
        
        // 设置更新时间
        saleOrder.setUpdateTime(LocalDateTime.now());
        
        // 更新订单
        updateById(saleOrder);
        
        log.info("更新销售订单成功，订单ID：{}", updateDTO.getId());
        return Result.success();
    }
    
    /**
     * 销售订单发货
     * @param shipDTO 发货信息
     * @return Result
     */
    @Override
    @Transactional
    public Result shipSaleOrder(SaleOrderShipDTO shipDTO) {
        // 参数校验
        if (shipDTO.getId() <= 0) {
            return Result.error("订单ID无效");
        }
        
        // 查询订单是否存在
        SaleOrder existOrder = getById(shipDTO.getId());
        if (existOrder == null) {
            return Result.error("订单不存在");
        }
        
        // 检查订单状态，只有已确认状态的订单才能发货
        if (existOrder.getStatus() != 1) {
            return Result.error("当前订单状态不允许发货");
        }
        
        // 调用库存模块的出库接口
        StockOutDTO stockOutDTO = new StockOutDTO();
        stockOutDTO.setMaterialName(existOrder.getProductName());
        stockOutDTO.setUnit(existOrder.getUnit());
        stockOutDTO.setQuantity(existOrder.getQuantity());
        stockOutDTO.setRemark("销售订单发货：" + existOrder.getOrderNo());
        
        // 调用出库接口
        Result stockOutResult = stockService.stockOut(stockOutDTO);
        if (stockOutResult.getCode() != 0) {
            return Result.error("出库失败：" + stockOutResult.getMsg());
        }
        
        // 更新订单状态为已发货
        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setId(shipDTO.getId());
        saleOrder.setStatus(2); // 设置状态为已发货
        saleOrder.setShippingTime(LocalDateTime.now()); // 设置发货时间
        saleOrder.setRemark(shipDTO.getRemark()); // 设置备注
        saleOrder.setUpdateTime(LocalDateTime.now()); // 设置更新时间
        
        // 更新订单
        updateById(saleOrder);
        
        log.info("销售订单发货成功，订单ID：{}", shipDTO.getId());
        return Result.success();
    }
    
    /**
     * 查询销售订单详情
     * @param id 订单ID
     * @return Result<SaleOrderVO>
     */
    @Override
    public Result<SaleOrderVO> getSaleOrderById(int id) {
        // 参数校验
        if (id <= 0) {
            return Result.error("订单ID无效");
        }
        
        // 查询订单详情
        SaleOrderVO saleOrderVO = saleOrderMapper.selectSaleOrderById(id);
        if (saleOrderVO == null) {
            return Result.error("订单不存在");
        }
        
        return Result.success(saleOrderVO);
    }
    
    /**
     * 分页查询销售订单列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<SaleOrderVO>>
     */
    @Override
    public Result<page<SaleOrderVO>> getSaleOrderList(int currentPage, int pageSize, SaleOrderQueryDTO queryDTO) {
        // 创建分页对象
        Page<SaleOrder> page = new Page<>(currentPage, pageSize);
        
        // 执行分页查询
        Page<SaleOrderVO> voPage = saleOrderMapper.selectSaleOrderPage(page, queryDTO);
        
        // 封装返回结果
        page<SaleOrderVO> result = new page<>();
        result.setPageSize(pageSize);
        result.setTotal((int) voPage.getTotal());
        result.setList(voPage.getRecords());
        
        return Result.success(result);
    }
}