package com.zzl.open.dreambi.conf;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * ---------------------------
 * |@author |zonglin.zhang    |
 * ---------------------------
 * |@version|v1.0             |
 * ---------------------------
 * |@date   |2021/7/1          |
 * ---------------------------
 **/
@EnableTransactionManagement
@MapperScan(basePackages = "com.zzl.open.dreambi.mapper",sqlSessionTemplateRef = "primarySqlSessionTemplate")
@Configuration
public class PrimarySqlSessionFactoryConfig {
    private static final String TYPE_ALIAS_PACKAGE = "com.zzl.open.dreambi.model";
    private static final String MYBATIS_MAPPER_LOCATION = "classpath:/mybatis/*.xml";

    @Bean(name = "primaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.primary")
    @Primary
    public DataSource primaryDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "primaryTransactionManager")
    @Primary
    public PlatformTransactionManager annotationDrivenTransactionManager(@Qualifier("primaryDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }


    /**
     * 创建sqlSessionFactory
     * 设置mapper 映射路径
     * 设置datasource数据源
     *
     * @return
     * @throws Exception
     */
    @Bean(name = "primarySqlSessionFactory")
    @Primary
    public SqlSessionFactory createSqlSessionFactoryBean(@Qualifier("primaryDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        /** 设置datasource */
        sqlSessionFactoryBean.setDataSource(dataSource);
        /** 设置typeAlias 包扫描路径 */
        sqlSessionFactoryBean.setTypeAliasesPackage(TYPE_ALIAS_PACKAGE);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(MYBATIS_MAPPER_LOCATION));
        // Configuration 对象，用于设置mybatis的settings属性。
        org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();
        // 开启javaBean 成员变量名映射为映射到数据库列名的时候，驼峰命名法到下划线分割命名法则的自动转换功能
        config.setMapUnderscoreToCamelCase(true);
        sqlSessionFactoryBean.setConfiguration(config);
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "primarySqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("primarySqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
