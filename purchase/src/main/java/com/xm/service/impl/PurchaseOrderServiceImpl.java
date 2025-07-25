package com.xm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.dto.PurchaseOrderQueryDTO;
import com.xm.dto.PurchaseOrderStatusDTO;
import com.xm.entity.PurchaseOrder;
import com.xm.entity.PurchaseRequest;
import com.xm.entity.Supplier;
import com.xm.mapper.PurchaseOrderMapper;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.PurchaseOrderService;
import com.xm.service.PurchaseRequestService;
import com.xm.service.SupplierService;
import com.xm.utils.RedisIdGenerator;
import com.xm.vo.PurchaseOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 采购订单服务实现类
 */
@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrderMapper, PurchaseOrder> implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper; // 注入采购订单Mapper

    @Autowired
    private RedisIdGenerator redisIdGenerator; // 注入Redis主键生成器

    @Autowired
    private SupplierService supplierService; // 注入供应商服务

    @Autowired
    private PurchaseRequestService purchaseRequestService; // 注入采购申请服务

    @Autowired
    private RedisTemplate<String, String> redisTemplate; // 注入RedisTemplate

    /**
     * 新增采购订单
     * @param order 采购订单信息
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addPurchaseOrder(PurchaseOrder order) {
        // 1. 校验供应商是否存在
        Supplier supplier = supplierService.getById(order.getSupplierId());
        if (supplier == null) {
            return Result.error("供应商不存在");
        }
        
        // 2. 校验采购申请是否存在（如果有关联）
        if (order.getPurchaseRequestId() > 0) {
            PurchaseRequest request = purchaseRequestService.getById(order.getPurchaseRequestId());
            if (request == null) {
                return Result.error("关联的采购申请不存在");
            }
            
            // 检查采购申请状态是否为已通过
            if (request.getStatus() != 2) {
                return Result.error("只能关联已通过的采购申请");
            }
        }else{
            return Result.error("需要关联采购申请");
        }

        
        // 3. 生成订单编号（PO + 年月日 + 5位序列号）
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        
        // 4. 计算总金额
        BigDecimal totalAmount = order.getUnitPrice().multiply(new BigDecimal(order.getQuantity()));
        order.setTotalAmount(totalAmount);
        
        // 5. 通过Redis生成采购订单主键ID
        int id = redisIdGenerator.generateId("purchase_order");
        order.setId(id);
        
        // 6. 设置初始状态和时间
        order.setStatus(0); // 默认状态为"待确认"
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        // 7. 保存采购订单
        this.save(order);
        return Result.success("新增采购订单成功");
    }

    /**
     * 更新采购订单
     * @param order 采购订单信息
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updatePurchaseOrder(PurchaseOrder order) {
        // 1. 校验采购订单是否存在
        //PurchaseOrder existingOrder = this.getById(order.getId());
        // 行锁读取
        PurchaseOrder existingOrder = this.lambdaQuery()
                .eq(PurchaseOrder::getId, order.getId())
                .last("LIMIT 1 FOR UPDATE")
                .one();
        if (existingOrder == null) {
            return Result.error("采购订单不存在");
        }
        
        // 2. 只允许待确认状态的订单修改
        if (existingOrder.getStatus() != 0) {
            return Result.error("只有待确认状态的订单可以修改");
        }
        
        // 3. 校验供应商是否存在
        if (order.getSupplierId() > 0 && order.getSupplierId() != existingOrder.getSupplierId()) {
            Supplier supplier = supplierService.getById(order.getSupplierId());
            if (supplier == null) {
                return Result.error("供应商不存在");
            }
        }
        
        // 4. 校验采购申请是否存在（如果有关联且有变更）
        if (order.getPurchaseRequestId() > 0 && order.getPurchaseRequestId() != existingOrder.getPurchaseRequestId()) {
            PurchaseRequest request = purchaseRequestService.getById(order.getPurchaseRequestId());
            if (request == null) {
                return Result.error("关联的采购申请不存在");
            }
            
            // 检查采购申请状态是否为已通过
            if (request.getStatus() != 2) {
                return Result.error("只能关联已通过的采购申请");
            }
        }
        
        // 5. 计算总金额
        BigDecimal totalAmount = order.getUnitPrice().multiply(new BigDecimal(order.getQuantity()));
        order.setTotalAmount(totalAmount);
        
        // 6. 保留不可修改的字段
        order.setOrderNo(existingOrder.getOrderNo());
        order.setCreateTime(existingOrder.getCreateTime());
        order.setUpdateTime(LocalDateTime.now());
        
        // 7. 更新采购订单
        this.updateById(order);
        return Result.success("更新采购订单成功");
    }

    /**
     * 作废采购订单
     * @param id 采购订单ID
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result cancelPurchaseOrder(int id) {
        // 1. 校验采购订单是否存在
        //PurchaseOrder order = this.getById(id);
        PurchaseOrder order = this.lambdaQuery()
                .eq(PurchaseOrder::getId, id)
                .last("LIMIT 1 FOR UPDATE")
                .one();
        if (order == null) {
            return Result.error("采购订单不存在");
        }
        
        // 2. 已完成的订单不能作废
        if (order.getStatus() == 4) {
            return Result.error("已完成的订单不能作废");
        }
        
        // 3. 已作废的订单不能重复作废
        if (order.getStatus() == 5) {
            return Result.error("订单已经是作废状态");
        }
        
        // 4. 更新状态为已作废
        order.setStatus(5);
        order.setUpdateTime(LocalDateTime.now());
        this.updateById(order);
        
        return Result.success("作废采购订单成功");
    }

    /**
     * 变更采购订单状态
     * @param statusDTO 状态变更DTO
     * @return Result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updatePurchaseOrderStatus(PurchaseOrderStatusDTO statusDTO) {
        // 1. 校验状态值
        if (statusDTO.getStatus() < 0 || statusDTO.getStatus() > 5) {
            return Result.error("无效的状态值");
        }
        
        // 2. 校验采购订单是否存在
        //PurchaseOrder order = this.getById(statusDTO.getId());
        PurchaseOrder order = this.lambdaQuery()
                .eq(PurchaseOrder::getId, statusDTO.getId())
                .last("LIMIT 1 FOR UPDATE")
                .one();
        if (order == null) {
            return Result.error("采购订单不存在");
        }
        
        // 3. 已作废的订单不能变更状态
        if (order.getStatus() == 5) {
            return Result.error("已作废的订单不能变更状态");
        }
        
        // 4. 已完成的订单不能变更状态
        if (order.getStatus() == 4 && statusDTO.getStatus() != 5) {
            return Result.error("已完成的订单不能变更状态");
        }
        
        // 5. 状态只能按流程顺序变更
        if (statusDTO.getStatus() != 5 && statusDTO.getStatus() != order.getStatus() + 1) {
            return Result.error("订单状态只能按流程顺序变更");
        }
        
        // 6. 更新状态
        order.setStatus(statusDTO.getStatus());
        order.setUpdateTime(LocalDateTime.now());
        this.updateById(order);
        
        return Result.success("变更采购订单状态成功");
    }

    /**
     * 查询采购订单详情
     * @param id 采购订单ID
     * @return Result<PurchaseOrderVO>
     */
    @Override
    public Result<PurchaseOrderVO> getPurchaseOrderById(int id) {
        // 使用Mapper查询关联详情
        PurchaseOrderVO orderVO = purchaseOrderMapper.getPurchaseOrderById(id);
        
        if (orderVO == null) {
            return Result.error("采购订单不存在");
        }
        
        return Result.success(orderVO);
    }

    /**
     * 分页查询采购订单列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<PurchaseOrderVO>>
     */
    @Override
    public Result<page<PurchaseOrderVO>> getPurchaseOrderList(int currentPage, int pageSize, PurchaseOrderQueryDTO queryDTO) {
        // 创建分页对象
        Page<PurchaseOrderVO> pageObj = new Page<>(currentPage, pageSize);
        
        // 查询采购订单列表
        IPage<PurchaseOrderVO> iPage = purchaseOrderMapper.selectPurchaseOrderPage(pageObj, queryDTO);
        
        // 封装自定义page对象
        page<PurchaseOrderVO> resultPage = new page<>();
        resultPage.list = iPage.getRecords();
        resultPage.total = (int) iPage.getTotal();
        resultPage.pageSize = (int) iPage.getSize();
        
        return Result.success(resultPage);
    }
    
    /**
     * 生成订单编号
     * 格式：PO + 年月日 + 5位序列号
     * @return 订单编号
     */
    private String generateOrderNo() {
        // 获取当前日期
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 获取当天的序列号
        String key = "xm:order_no:" + dateStr;
        // 使用Redis的incr命令生成自增序列号
        Long sequence = redisTemplate.opsForValue().increment(key);
        
        // 拼接订单编号
        return "PO" + dateStr + String.format("%05d", sequence != null ? sequence : 1);
    }

    /**
     * 获取状态名称
     * @param status 状态码
     * @return 状态名称
     */
    private String getStatusName(int status) {
        switch (status) {
            case 0: return "待确认";
            case 1: return "已确认";
            case 2: return "已下单";
            case 3: return "已到货";
            case 4: return "已完成";
            case 5: return "已作废";
            default: return "未知状态";
        }
    }
} 