package com.chazdada.singledatasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
@RestController
@Slf4j
public class SingleDatasourceApplication implements CommandLineRunner {

  @Autowired
  private DataSource dataSource;

  @Autowired
  private JdbcTemplate jdbcTemplate;


  public static void main(String[] args) {
    SpringApplication.run(SingleDatasourceApplication.class, args);
  }

  @RequestMapping("/hello")
  public String hello(){
    return "hello";
  }


  @Override
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
