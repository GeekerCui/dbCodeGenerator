package com.github.geekercui.generator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * SwaggerConfig
 */
@Configuration
@EnableSwagger2
public class Swagger2Configuration {

	/**
	 * 
	 * @return
	 */
	@Bean
	public Docket accessToken() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("api")// 定义组
				.select() // 选择那些路径和api会生成document
				.apis(RequestHandlerSelectors.basePackage("com.github.geekercui.generator")) // 拦截的包路径
				.paths(regex("/.*"))// 拦截的接口路径
				.build() // 创建
				.apiInfo(apiInfo()); // 配置说明
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()//
				.title("框架项目")// 标题
				.description("框架项目接口")// 描述
				.version("1.0")// 版本
				.build();
	}
}
