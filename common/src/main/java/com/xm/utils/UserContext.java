package com.xm.utils;

import lombok.Data;

/**
 * 用户上下文工具类，基于ThreadLocal存储当前用户信息
 */
public class UserContext {
    
    // ThreadLocal存储当前登录用户信息
    private static final ThreadLocal<UserInfo> userThreadLocal = new ThreadLocal<>();
    
    /**
     * 设置当前线程的用户信息
     * @param userInfo 用户信息
     */
    public static void setUserInfo(UserInfo userInfo) {
        userThreadLocal.set(userInfo);
    }
    
    /**
     * 获取当前线程的用户信息
     * @return 用户信息
     */
    public static UserInfo getUserInfo() {
        return userThreadLocal.get();
    }
    
    /**
     * 清除当前线程的用户信息
     */
    public static void removeUserInfo() {
        userThreadLocal.remove();
    }
    
    /**
     * 获取当前登录用户ID
     * @return 员工ID
     */
    public static Integer getCurrentEmployeeId() {
        UserInfo userInfo = getUserInfo();
        return userInfo != null ? userInfo.getEmployeeId() : null;
    }
    
    /**
     * 获取当前登录用户角色
     * @return 用户角色
     */
    public static String getCurrentUserRole() {
        UserInfo userInfo = getUserInfo();
        return userInfo != null ? userInfo.getRole() : null;
    }
    
    /**
     * 获取当前登录用户名
     * @return 用户名
     */
    public static String getCurrentUsername() {
        UserInfo userInfo = getUserInfo();
        return userInfo != null ? userInfo.getUsername() : null;
    }
    
    /**
     * 用户信息实体类
     */
    @Data
    public static class UserInfo {
        private Integer employeeId; // 员工ID
        private String username; // 用户名
        private String role; // 用户角色
    }
} 