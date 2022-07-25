package com.amiao.demo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DemoData {
    //学生序号
    //设置excel表头名称
    //index代表excel表格字段索引
    @ExcelProperty(value = "学生序号",index = 0)
    private Integer sno;

    //学生名称
    //设置excel表头名称
    //index代表excel表格字段索引
    @ExcelProperty(value = "学生姓名",index = 1)
    private String sname;
}
