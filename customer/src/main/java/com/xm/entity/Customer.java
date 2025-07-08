package com.xm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xm.handler.ListStringTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Customer implements Serializable {
    @TableId(type = IdType.AUTO)
    private int id; // 客户ID
    private String name; // 客户姓名
    private String contactPerson; // 联系人
    private String phone; // 联系电话
    
    // 使用自定义TypeHandler处理JSON与List<String>的转换
    @TableField(typeHandler = ListStringTypeHandler.class)
    private List<String> email; // 邮箱列表
    
    private String address; // 地址
    private String source; // 客户来源
    private int level; // 客户等级
    private int status; // 客户状态（1: 潜在客户, 2: 意向客户, 3: VIP客户, 4: 已成交客户）
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
    private String remark; // 备注
}