package com.xm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xm.dto.StockInDTO;
import com.xm.dto.StockOutDTO;
import com.xm.dto.StockQueryDTO;
import com.xm.entity.Stock;
import com.xm.entity.StockOperation;
import com.xm.mapper.StockMapper;
import com.xm.page.page;
import com.xm.result.Result;
import com.xm.service.StockOperationService;
import com.xm.service.StockService;
import com.xm.utils.RedisIdGenerator;
import com.xm.utils.UserContext;
import com.xm.vo.StockVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 库存服务实现类
 */
@Slf4j
@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements StockService {

    @Autowired
    private RedisIdGenerator redisIdGenerator; // 注入Redis ID生成器

    @Autowired
    private StockMapper stockMapper; // 注入StockMapper

    
    @Autowired
    private StockOperationService stockOperationService; // 注入库存操作记录服务
    
    @Autowired
    private StringRedisTemplate redisTemplate; // 注入Redis模板

    /**
     * 入库操作
     * @param stockInDTO 入库信息
     * @return Result
     */
    @Override
    @Transactional
    public Result stockIn(StockInDTO stockInDTO) {
        log.info("入库操作: {}", stockInDTO);

        
        // 检查入库数量是否有效
        if (stockInDTO.getQuantity() <= 0) {
            return Result.error("入库数量必须大于0");
        }
        
        // 获取用户上下文信息
        Integer operatorId = UserContext.getCurrentEmployeeId();
        String operatorName = UserContext.getCurrentUsername();
        if (operatorId == null || operatorName == null) {
            return Result.error("无法获取操作人信息");
        }
        
        // 查询物料是否已有库存记录（只用物料名称唯一）
        LambdaQueryWrapper<Stock> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Stock::getMaterialName, stockInDTO.getMaterialName());
        Stock stock = this.getOne(queryWrapper);
        
        // 生成操作编号
        String operationNo = generateOperationNo("IN");
        LocalDateTime now = LocalDateTime.now();
        
        int beforeQuantity = 0;
        int afterQuantity;
        
        // 如果没有库存记录，则创建新的库存记录
        if (stock == null) {
            stock = new Stock();
            stock.setId(redisIdGenerator.generateId("stock"));
            stock.setMaterialName(stockInDTO.getMaterialName()); // 设置物料名称
            stock.setUnit(stockInDTO.getUnit()); // 设置单位
            stock.setQuantity(stockInDTO.getQuantity());
            stock.setLastStockInTime(now);
            stock.setCreateTime(now);
            stock.setUpdateTime(now);
            this.save(stock);
            
            afterQuantity = stockInDTO.getQuantity();
        } else {
            // 已有库存记录，更新库存数量
            beforeQuantity = stock.getQuantity();
            afterQuantity = beforeQuantity + stockInDTO.getQuantity();
            
            stock.setQuantity(afterQuantity);
            stock.setLastStockInTime(now);
            stock.setUpdateTime(now);
            
            this.updateById(stock);
        }
        
        // 记录入库操作
        StockOperation operation = new StockOperation();
        operation.setId(redisIdGenerator.generateId("stock_operation"));
        operation.setOperationNo(operationNo);
        operation.setStockId(stock.getId());
        operation.setOperationType(0); // 0:入库
        operation.setQuantity(stockInDTO.getQuantity());
        operation.setBeforeQuantity(beforeQuantity);
        operation.setAfterQuantity(afterQuantity);
        operation.setOperatorId(operatorId);
        operation.setOperatorName(operatorName);
        operation.setRemark(stockInDTO.getRemark());
        operation.setOperationTime(now);
        operation.setCreateTime(now);
        
        stockOperationService.addStockOperation(operation);
        
        return Result.success();
    }

    /**
     * 出库操作
     * @param stockOutDTO 出库信息
     * @return Result
     */
    @Override
    @Transactional
    public Result stockOut(StockOutDTO stockOutDTO) {
        log.info("出库操作: {}", stockOutDTO);
        
        // 检查出库数量是否有效
        if (stockOutDTO.getQuantity() <= 0) {
            return Result.error("出库数量必须大于0");
        }
        
        // 获取用户上下文信息
        Integer operatorId = UserContext.getCurrentEmployeeId();
        String operatorName = UserContext.getCurrentUsername();
        if (operatorId == null || operatorName == null) {
            return Result.error("无法获取操作人信息");
        }
        
        // 查询库存记录
        Stock stock = this.getById(stockOutDTO.getStockId());
        if (stock == null) {
            return Result.error("库存记录不存在");
        }
        
        // 检查库存数量是否足够
        if (stock.getQuantity() < stockOutDTO.getQuantity()) {
            return Result.error("库存不足");
        }
        
        // 生成操作编号
        String operationNo = generateOperationNo("OUT");
        LocalDateTime now = LocalDateTime.now();
        
        // 更新库存数量
        int beforeQuantity = stock.getQuantity();
        int afterQuantity = beforeQuantity - stockOutDTO.getQuantity();
        
        stock.setQuantity(afterQuantity);
        stock.setLastStockOutTime(now);
        stock.setUpdateTime(now);
        
        this.updateById(stock);
        
        // 记录出库操作
        StockOperation operation = new StockOperation();
        operation.setId(redisIdGenerator.generateId("stock_operation"));
        operation.setOperationNo(operationNo);
        operation.setStockId(stock.getId());
        operation.setOperationType(1); // 1:出库
        operation.setQuantity(stockOutDTO.getQuantity());
        operation.setBeforeQuantity(beforeQuantity);
        operation.setAfterQuantity(afterQuantity);
        operation.setOperatorId(operatorId);
        operation.setOperatorName(operatorName);
        operation.setRemark(stockOutDTO.getRemark());
        operation.setOperationTime(now);
        operation.setCreateTime(now);
        
        stockOperationService.addStockOperation(operation);
        
        return Result.success();
    }

    /**
     * 更新库存信息
     * @param stock 库存信息
     * @return Result
     */
    @Override
    @Transactional
    public Result updateStock(Stock stock) {
        log.info("更新库存信息: {}", stock);
        
        // 检查库存记录是否存在
        Stock existStock = this.getById(stock.getId());
        if (existStock == null) {
            return Result.error("库存记录不存在");
        }
        
        // 获取用户上下文信息
        Integer operatorId = UserContext.getCurrentEmployeeId();
        String operatorName = UserContext.getCurrentUsername();
        if (operatorId == null || operatorName == null) {
            return Result.error("无法获取操作人信息");
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 如果修改了库存数量，记录调整操作
        if (existStock.getQuantity() != stock.getQuantity()) {
            // 生成操作编号
            String operationNo = generateOperationNo("ADJ");
            
            // 记录库存调整操作
            StockOperation operation = new StockOperation();
            operation.setId(redisIdGenerator.generateId("stock_operation"));
            operation.setOperationNo(operationNo);
            operation.setStockId(stock.getId());
            operation.setOperationType(2); // 2:库存调整
            operation.setQuantity(Math.abs(stock.getQuantity() - existStock.getQuantity()));
            operation.setBeforeQuantity(existStock.getQuantity());
            operation.setAfterQuantity(stock.getQuantity());
            operation.setOperatorId(operatorId);
            operation.setOperatorName(operatorName);
            operation.setRemark("库存数量调整");
            operation.setOperationTime(now);
            operation.setCreateTime(now);
            
            stockOperationService.addStockOperation(operation);
        }
        
        // 设置更新时间
        stock.setUpdateTime(now);
        
        // 更新库存信息
        this.updateById(stock);
        
        return Result.success();
    }

    /**
     * 查询库存详情
     * @param id 库存ID
     * @return Result<StockVO>
     */
    @Override
    public Result<StockVO> getStockById(int id) {
        log.info("查询库存详情, ID: {}", id);
        
        // 查询库存详情
        StockVO stockVO = this.baseMapper.selectStockById(id);
        if (stockVO == null) {
            return Result.error("库存记录不存在");
        }
        
        return Result.success(stockVO);
    }

    /**
     * 分页查询库存列表
     * @param currentPage 当前页码
     * @param pageSize 每页记录数
     * @param queryDTO 查询条件
     * @return Result<page<StockVO>>
     */
    @Override
    public Result<page<StockVO>> getStockList(int currentPage, int pageSize, StockQueryDTO queryDTO) {
        log.info("分页查询库存列表: 页码={}, 每页数量={}, 查询条件={}", currentPage, pageSize, queryDTO);
        
        // 创建分页参数
        Page<Stock> pageParam = new Page<>(currentPage, pageSize);
        
        // 执行查询
        IPage<StockVO> stockPage = this.baseMapper.selectStockPage(pageParam, queryDTO);
        
        // 封装结果
        page<StockVO> resultPage = new page<>();
        resultPage.setPageSize(pageSize);
        resultPage.setTotal((int) stockPage.getTotal());
        resultPage.setList(stockPage.getRecords());
        
        return Result.success(resultPage);
    }

    /**
     * 根据产品名称查询库存详情
     * @param productName 产品名称
     * @return Result<StockVO>
     */
    @Override
    public StockVO getStockByProductName(String productName) {
        log.info("根据产品名称查询库存详情: {}", productName);

        // 查询库存详情
        StockVO stockVO = stockMapper.selectStockByProductName(productName);
        return stockVO;
    }
    
    /**
     * 生成操作编号
     * @param prefix 前缀 (IN: 入库, OUT: 出库, ADJ: 调整)
     * @return 操作编号
     */
    private String generateOperationNo(String prefix) {
        // 使用Redis生成自增序列号
        String key = "xm:stock:operation_no:" + prefix;
        Long sequence = redisTemplate.opsForValue().increment(key);
        
        // 格式化为6位数字，不足前面补0
        String sequenceStr = String.format("%06d", Objects.requireNonNull(sequence));
        
        // 拼接年月日
        LocalDateTime now = LocalDateTime.now();
        String date = String.format("%d%02d%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        
        // 返回完整编号，如：IN20230501000001
        return prefix + date + sequenceStr;
    }
}