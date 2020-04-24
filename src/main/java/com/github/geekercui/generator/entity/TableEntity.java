package com.github.geekercui.generator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableEntity implements Serializable {
    // select table_name, table_comment from tables where table_name like ('%{}%')""".format(name_like)
    /** 表名 */
    private String name;

    /** 表注释 */
    private String comment;

    /** 创建时间 */
    private Date createTime;

    /** 表引擎 */
    private String engine;

    /** 当前数据量级 */
    private String rowNum;

    /** 表的主键 */
    private ColumnEntity pk;

    /** 表的列名(不包含主键) */
    private List<ColumnEntity> columnList;

    //类名(第一个字母大写)，如：sys_user => SysUser
    private String className;
    //类名(第一个字母小写)，如：sys_user => sysUser
    private String classname;
}
