package com.github.geekercui.generator.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnEntity implements Serializable {
    // select t1.column_name,t1.column_type, t2.column_comment, t1.column_default, t1.is_nullable

    /** 属性 */
    private String name;

    /** 类型 */
    private String type;

    /** 注释 */
    private String comment;

    /** 默认值 */
    private Object defaultValue;

    /** 是否为空 */
    private String isNullable;

    /** 主键 */
    private String key;


    //属性名称(第一个字母大写)，如：user_name => UserName
    private String attrName;
    //属性名称(第一个字母小写)，如：user_name => userName
    private String attrname;
    //auto_increment
    private String extra;

}
