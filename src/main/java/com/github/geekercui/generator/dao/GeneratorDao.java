
package com.github.geekercui.generator.dao;

import com.github.geekercui.generator.entity.ColumnEntity;
import com.github.geekercui.generator.entity.TableEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GeneratorDao {
    
    List<TableEntity> queryList(@Param("tableName") String tableWildName);

    TableEntity queryTable(@Param("tableName") String tableName);

    List<ColumnEntity> queryColumns(@Param("tableName") String tableName);
}
