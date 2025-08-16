package com.xm.tools;

import com.xm.entity.Customer;
import com.xm.entity.Employee;
import com.xm.result.Result;
import com.xm.service.CustomerService;
import com.xm.service.EmployeeService;
import com.xm.vo.CustomerVO;
import com.xm.vo.EmployeeVO;
import com.xm.page.page;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class XmAiTools {

    @Autowired
    private CustomerService customerService; // 注入客户服务

    @Autowired
    private EmployeeService employeeService; // 注入员工服务

    /**
     * 根据客户ID查询客户详细信息
     * @param customerId 客户ID
     * @return 客户详细信息
     */
    @Tool(name = "查询客户信息", value = "根据客户ID查询客户详细信息")
    public Map<String, Object> queryCustomerById(int customerId) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            // 调用客户服务查询客户信息
            Result<Customer> result = customerService.getCustomerById(customerId);
            
            if (result.getCode() == 1 && result.getData() != null) {
                // 查询成功
                resultMap.put("success", true);
                resultMap.put("data", result.getData());
                resultMap.put("message", "客户信息查询成功");
            } else {
                // 查询失败
                resultMap.put("success", false);
                resultMap.put("message", "未找到对应的客户信息");
            }
        } catch (Exception e) {
            // 异常处理
            resultMap.put("success", false);
            resultMap.put("message", "客户信息查询异常: " + e.getMessage());
        }
        
        return resultMap;
    }

    /**
     * 分页查询客户列表
     * @param currentPage 当前页码
     * @param pageSize 每页显示数量
     * @return 客户列表分页数据
     */
    @Tool(name = "查询客户列表", value = "分页查询客户列表信息")
    public Map<String, Object> queryCustomerList(int currentPage, int pageSize) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            // 参数校验
            if (currentPage <= 0) {
                currentPage = 1; // 默认第一页
            }
            
            if (pageSize <= 0) {
                pageSize = 10; // 默认每页10条
            }
            
            // 调用客户服务查询客户列表
            Result<page<CustomerVO>> result = customerService.getCustomerList(currentPage, pageSize);
            
            if (result.getCode() == 1 && result.getData() != null) {
                // 查询成功
                resultMap.put("success", true);
                resultMap.put("data", result.getData());
                resultMap.put("message", "客户列表查询成功");
            } else {
                // 查询失败
                resultMap.put("success", false);
                resultMap.put("message", "客户列表查询失败");
            }
        } catch (Exception e) {
            // 异常处理
            resultMap.put("success", false);
            resultMap.put("message", "客户列表查询异常: " + e.getMessage());
        }
        
        return resultMap;
    }

    /**
     * 根据员工ID查询员工详细信息
     * @param employeeId 员工ID
     * @return 员工详细信息
     */
    @Tool(name = "查询员工信息", value = "根据员工ID查询员工详细信息")
    public Map<String, Object> queryEmployeeById(int employeeId) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            // 直接使用MyBatis-Plus提供的方法查询员工信息
            Employee employee = employeeService.getById(employeeId);
            
            if (employee != null) {
                // 查询成功
                resultMap.put("success", true);
                resultMap.put("data", employee);
                resultMap.put("message", "员工信息查询成功");
            } else {
                // 查询失败
                resultMap.put("success", false);
                resultMap.put("message", "未找到对应的员工信息");
            }
        } catch (Exception e) {
            // 异常处理
            resultMap.put("success", false);
            resultMap.put("message", "员工信息查询异常: " + e.getMessage());
        }
        
        return resultMap;
    }

    /**
     * 分页查询员工列表
     * @param currentPage 当前页码
     * @param pageSize 每页显示数量
     * @return 员工列表分页数据
     */
    @Tool(name = "查询员工列表", value = "分页查询员工列表信息")
    public Map<String, Object> queryEmployeeList(int currentPage, int pageSize) {
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            // 参数校验
            if (currentPage <= 0) {
                currentPage = 1; // 默认第一页
            }
            
            if (pageSize <= 0) {
                pageSize = 10; // 默认每页10条
            }
            
            // 调用员工服务查询员工列表
            Result<page<EmployeeVO>> result = employeeService.getEmployeeList(currentPage, pageSize);
            
            if (result.getCode() == 1 && result.getData() != null) {
                // 查询成功
                resultMap.put("success", true);
                resultMap.put("data", result.getData());
                resultMap.put("message", "员工列表查询成功");
            } else {
                // 查询失败
                resultMap.put("success", false);
                resultMap.put("message", "员工列表查询失败");
            }
        } catch (Exception e) {
            // 异常处理
            resultMap.put("success", false);
            resultMap.put("message", "员工列表查询异常: " + e.getMessage());
        }
        
        return resultMap;
    }
}
