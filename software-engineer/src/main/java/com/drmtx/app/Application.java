package com.drmtx.app;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@EnableAutoConfiguration
@SpringBootApplication
@EntityScan(basePackageClasses=Application.class)
@Configuration
public class Application {

  private static final Logger _logger = LogManager.getLogger(Application.class);

  @Bean
  protected ServletContextListener listener() {
    return new ServletContextListener() {

      @Override
      public void contextInitialized(ServletContextEvent sce) {
        _logger.info("ServletContext initialized");
      }

      @Override
      public void contextDestroyed(ServletContextEvent sce) {
        _logger.info("ServletContext destroyed");
      }

    };
  }

  public static void main(String[] args) throws Exception {
    ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);
  }
}