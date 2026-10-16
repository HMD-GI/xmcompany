package com.xm.page;

import lombok.Data;

import java.util.List;

@Data
public class page<T> {
    public int pageSize; // 每页显示的条数
    public int total; // 总条数

    public List<T> list;  // 当前页的数据
}
