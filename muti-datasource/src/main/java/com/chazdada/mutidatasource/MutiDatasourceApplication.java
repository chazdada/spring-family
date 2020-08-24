package com.chazdada.mutidatasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        JdbcTemplateAutoConfiguration.class})
@Slf4j
public class MutiDatasourceApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(MutiDatasourceApplication.class, args);
  }


  @Bean
  @ConfigurationProperties("foo.datasource")
  public DataSourceProperties fooDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource fooDataSource() {
    DataSourceProperties dataSourceProperties = fooDataSourceProperties();
    log.info("foo datasource: {}", dataSourceProperties.getUrl());
    return dataSourceProperties.initializeDataSourceBuilder().build();
  }

  @Bean
  @Resource
  public PlatformTransactionManager fooTxManager(DataSource fooDataSource) {
    return new DataSourceTransactionManager(fooDataSource);
  }

  @Bean
  @ConfigurationProperties("bar.datasource")
  public DataSourceProperties barDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource barDataSource() {
    DataSourceProperties dataSourceProperties = barDataSourceProperties();
    log.info("bar datasource: {}", dataSourceProperties.getUrl());
    return dataSourceProperties.initializeDataSourceBuilder().build();
  }

  @Bean
  @Resource
  public PlatformTransactionManager barTxManager(DataSource barDataSource) {
    return new DataSourceTransactionManager(barDataSource);
  }

  @Autowired
  @Qualifier("fooDataSource")
  private DataSource dataSource;

  @Autowired
  private JdbcTemplate jdbcTemplate;


  @Bean
  public JdbcTemplate jdbcTemplate(){
    return new JdbcTemplate(dataSource);
  }

  public void run(String... args) throws Exception {
    showConnection();
    showData();
  }

  private void showConnection() throws SQLException {
    log.info(dataSource.toString());
    Connection conn = dataSource.getConnection();
    log.info(conn.toString());
    conn.close();
  }

  private void showData() {
    jdbcTemplate.queryForList("SELECT * FROM user")
            .forEach(row -> log.info(row.toString()));
  }
}
