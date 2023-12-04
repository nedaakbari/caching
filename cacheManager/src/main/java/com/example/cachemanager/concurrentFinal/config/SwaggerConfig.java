package com.example.cachemanager.concurrentFinal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author h.t Moghadam
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {

  private static final String title = "ConcurrencyLimit Documentation REST API";
  private static final String description = "This is a Endpoint for show Process ConcurrencyLimit details";
  private static final String SWAGGER_API_VERSION = "1.0";

  private ApiInfo getApiInfo() {
    return new ApiInfoBuilder()
        .title(title).description(description).version(SWAGGER_API_VERSION)
        .build();

  }

  @Bean
  public Docket productApi() {

    return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.
        basePackage("ir.mohaymen.shahkar.concurrencyLimit")).
        paths(PathSelectors.any()).
        build().apiInfo(getApiInfo());
  }

}
