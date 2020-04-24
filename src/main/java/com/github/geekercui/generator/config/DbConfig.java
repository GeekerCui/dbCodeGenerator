package com.github.geekercui.generator.config;

import com.github.geekercui.generator.dao.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;

@Configuration
public class DbConfig {

    @Value("${mainconf.database}")
    private String database;
    @Value("${mainconf.generator.wordPath}")
    private String docPath;


    @Resource
    private MySQLGeneratorDao mySQLGeneratorDao;
    @Resource
    private OracleGeneratorDao oracleGeneratorDao;
    @Resource
    private SQLServerGeneratorDao sqlServerGeneratorDao;
    @Resource
    private PostgreSQLGeneratorDao postgreSQLGeneratorDao;
    @Resource
    private DmGeneratorDao dmGeneratorDao;

    @Bean
    @Primary
    public GeneratorDao getGeneratorDao(){
        if("mysql".equalsIgnoreCase(database)){
            return mySQLGeneratorDao;
        } else if("oracle".equalsIgnoreCase(database)){
            return oracleGeneratorDao;
        } else if("sqlserver".equalsIgnoreCase(database)){
            return sqlServerGeneratorDao;
        } else if("postgresql".equalsIgnoreCase(database)){
            return postgreSQLGeneratorDao;
        } else if("dm".equalsIgnoreCase(database)){
            return dmGeneratorDao;
        } else if("kingbase".equalsIgnoreCase(database)){
            return dmGeneratorDao;
        } else {
            throw new RuntimeException("不支持当前数据库：" + database);
        }
    }
}
