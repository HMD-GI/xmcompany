package com.xm.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.dto.*;
import com.xm.entity.Customer;
import com.xm.entity.SaleOrder;
import com.xm.entity.Stock;
import com.xm.mapper.SaleOrderMapper;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.CustomerService;
import com.xm.service.SaleOrderService;
import com.xm.service.StockService;
import com.xm.utils.RedisIdGenerator;
import com.xm.utils.UserContext;
import com.xm.vo.SaleOrderVO;
import com.xm.vo.SimpleSaleOrderVO;
import com.xm.vo.StockVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
    private CustomerService customerService; // 引入客户服务
    
    @Autowired
    private StockService stockService; // 注入库存服务
    
    /**
     * 新增销售订单
     * @param addDTO 新增信息
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addSaleOrder(SaleOrderAddDTO addDTO) {
        // 参数校验
        if (addDTO.getQuantity() <= 0) {
            return Result.error("订单数量必须大于0");
        }
        // 校验客户是否存在
        Customer customer = customerService.getById(addDTO.getCustomerId());
        if (customer == null) {
            return Result.error("客户不存在");
        }
        
        // 创建销售订单对象
        SaleOrder saleOrder = new SaleOrder();
        BeanUtils.copyProperties(addDTO, saleOrder);

        //添加客户名
        saleOrder.setCustomerName(customer.getName());
        
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
    @Transactional(rollbackFor = Exception.class)
    public Result updateSaleOrder(SaleOrderUpdateDTO updateDTO) {
        // 参数校验
        if (updateDTO.getId() <= 0) {
            return Result.error("订单ID无效");
        }
        if (updateDTO.getQuantity() <= 0) {
            return Result.error("订单数量必须大于0");
        }
        
        // 查询订单是否存在
        //SaleOrder existOrder = getById(updateDTO.getId());
        SaleOrder existOrder = this.lambdaQuery()
                .eq(SaleOrder::getId, updateDTO.getId())
                .last("LIMIT 1 FOR UPDATE")
                .one();
        if (existOrder == null) {
            return Result.error("订单不存在");
        }
        
        // 检查订单状态，只有待处理和已确认状态的订单才能修改

        if (existOrder.getStatus() > 1) {
            return Result.error("当前订单状态不允许修改");
        }
        
        // 创建更新对象
        //SaleOrder saleOrder = saleOrderMapper.selectById(updateDTO.getId());
        BeanUtils.copyProperties(updateDTO, existOrder);
        // 设置更新时间
        existOrder.setUpdateTime(LocalDateTime.now());
        // 设置订单ID
        existOrder.setId(updateDTO.getId());
        // 更新订单
        updateById(existOrder);
        
        log.info("更新销售订单成功，订单ID：{}", updateDTO.getId());
        return Result.success();
    }
    
    /**
     * 销售订单发货
     * @param shipDTO 发货信息
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result shipSaleOrder(SaleOrderShipDTO shipDTO) {
        // 参数校验
        if (shipDTO.getId() <= 0) {
            return Result.error("订单ID无效");
        }
        
        // 查询订单是否存在
        //SaleOrder existOrder = getById(shipDTO.getId());
        SaleOrder existOrder = this.lambdaQuery()
                .eq(SaleOrder::getId, shipDTO.getId())
                .last("LIMIT 1 FOR UPDATE")
                .one();
        if (existOrder == null) {
            return Result.error("订单不存在");
        }
        
        // 检查订单状态，只有已确认状态的订单才能发货
        if (existOrder.getStatus() != 1) {
            return Result.error("当前订单状态不允许发货");
        }
        
        // 调用库存模块的出库接口
        // 先检查产品库存是否存在
        StockVO stock = stockService.getStockByProductName(existOrder.getProductName());
        if (stock == null) {
            return Result.error("产品 " + existOrder.getProductName() + " 的库存信息不存在");
        }
        
        StockOutDTO stockOutDTO = new StockOutDTO();
        stockOutDTO.setStockId(stock.getId());
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
        //SaleOrder saleOrder = new SaleOrder();
        existOrder.setId(shipDTO.getId());
        existOrder.setStatus(2); // 设置状态为已发货
        existOrder.setShippingTime(LocalDateTime.now()); // 设置发货时间
        existOrder.setRemark(shipDTO.getRemark()); // 设置备注
        existOrder.setUpdateTime(LocalDateTime.now()); // 设置更新时间
        
        // 更新订单
        updateById(existOrder);
        
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
    public Result<page<SimpleSaleOrderVO>> getSaleOrderList(int currentPage, int pageSize, SaleOrderQueryDTO queryDTO) {
        // 创建分页对象
        Page<SaleOrder> page = new Page<>(currentPage, pageSize);
        
        // 执行分页查询
        Page<SaleOrderVO> voPage = saleOrderMapper.selectSaleOrderPage(page, queryDTO);

        //转换为SimpleSaleOrderVO对象
        List<SimpleSaleOrderVO> vos = voPage.getRecords().stream().map(saleOrder -> {
            SimpleSaleOrderVO vo = new SimpleSaleOrderVO();
            BeanUtils.copyProperties(saleOrder, vo);
            return vo;
        }).collect(Collectors.toList());
        
        // 封装返回结果
        page<SimpleSaleOrderVO> result = new page<>();
        result.setPageSize(pageSize);
        result.setTotal((int) voPage.getTotal());
        result.setList(vos);
        
        return Result.success(result);
    }

    /**
     * 更新订单状态
     * @param statusUpdateDTO 状态更新信息
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateSaleOrderStatus(SaleOrderStatusUpdateDTO statusUpdateDTO) {
        // 参数校验
        if (statusUpdateDTO.getId() <= 0) {
            return Result.error("订单ID无效");
        }

        // 查询订单是否存在
        //SaleOrder existOrder = getById(statusUpdateDTO.getId());
        SaleOrder existOrder = this.lambdaQuery()
                .eq(SaleOrder::getId, statusUpdateDTO.getId())
                .last("LIMIT 1 FOR UPDATE")
                .one();
        if (existOrder == null) {
            return Result.error("订单不存在");
        }

        // 检查订单状态，只有待处理和已确认状态的订单才能修改状态或者已发货改为已完成可以修改
        if (!(existOrder.getStatus() < 2 || (existOrder.getStatus() == 2 && statusUpdateDTO.getStatus() == 3))){
            return Result.error("当前订单状态不允许修改");
        }
        // 判断库存数量是否达到目标数量
        StockVO stock = stockService.getStockByProductName(existOrder.getProductName());
        if (stock == null || (statusUpdateDTO.getStatus()==1 && existOrder.getQuantity() > stock.getQuantity())){
            return Result.error("库存数量不足");
        }

        // 创建更新对象
        //SaleOrder saleOrder = saleOrderMapper.selectById(statusUpdateDTO.getId());
        //existOrder.setId(statusUpdateDTO.getId());
        existOrder.setStatus(statusUpdateDTO.getStatus());
        existOrder.setRemark(statusUpdateDTO.getRemark()); // 设置备注
        existOrder.setUpdateTime(LocalDateTime.now()); // 设置更新时间

        // 更新订单状态
        updateById(existOrder);

        log.info("订单状态更新成功，订单 ID：{}，新状态：{}", statusUpdateDTO.getId(), statusUpdateDTO.getStatus());
        return Result.success();
    }

    /**
     * 删除销售订单
     * @param id 订单 ID
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteSaleOrder(int id) {
        // 参数校验
        if (id <= 0) {
            return Result.error("订单 ID 无效");
        }
        
        // 查询订单是否存在
        SaleOrder existOrder = getById(id);
        if (existOrder == null) {
            return Result.error("订单不存在");
        }
        
        // 检查订单状态，只有待处理和已取消状态的订单才能删除
        if (existOrder.getStatus() != 0 && existOrder.getStatus() != 4) {
            return Result.error("当前订单状态不允许删除");
        }
        
        // 删除订单
        removeById(id);
        
        log.info("删除销售订单成功，订单 ID：{}", id);
        return Result.success();
    }

}