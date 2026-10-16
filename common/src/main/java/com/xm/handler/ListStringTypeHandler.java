package com.xm.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义List<String>类型处理器
 * 用于处理数据库JSON字段与Java List<String>的转换
 */
@MappedTypes(List.class)
public class ListStringTypeHandler extends BaseTypeHandler<List<String>> {
    
    // Jackson对象映射器，用于JSON序列化和反序列化
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        // 将List<String>转换为JSON字符串
        String json = null;
        try {
            json = OBJECT_MAPPER.writeValueAsString(parameter);
            // 输出转换后的JSON内容，方便调试
            System.out.println("转换为JSON: " + json);
        } catch (JsonProcessingException e) {
            System.err.println("List转JSON失败: " + e.getMessage());
            json = "[]"; // 出错时设置为空数组
        }
        
        // 设置参数到PreparedStatement
        if (jdbcType == null) {
            ps.setString(i, json);
        } else {
            ps.setObject(i, json, jdbcType.TYPE_CODE);
        }
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 从结果集中获取JSON字符串并转换为List<String>
        String json = rs.getString(columnName);
        System.out.println("从数据库获取的JSON: " + json); // 调试输出
        return parseJsonToList(json);
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // 从结果集中获取JSON字符串并转换为List<String>
        String json = rs.getString(columnIndex);
        System.out.println("从数据库获取的JSON(索引): " + json); // 调试输出
        return parseJsonToList(json);
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 从存储过程结果中获取JSON字符串并转换为List<String>
        String json = cs.getString(columnIndex);
        System.out.println("从存储过程获取的JSON: " + json); // 调试输出
        return parseJsonToList(json);
    }
    
    /**
     * 将JSON字符串解析为List<String>
     */
    private List<String> parseJsonToList(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            // 使用Jackson将JSON字符串转换为List<String>
            return OBJECT_MAPPER.readValue(json, 
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (JsonProcessingException e) {
            System.err.println("JSON转List失败: " + json + ", 错误: " + e.getMessage());
            return new ArrayList<>(); // 出错时返回空列表
        }
    }
} 