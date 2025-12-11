package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
		System.out.println("汽车租赁系统后端服务已启动！");
		System.out.println("访问 http://localhost:8080 查看测试管理面板");
        System.out.println("访问  http://localhost:3010/login  查看前端登录页面");
	}

	@Bean
	public WebMvcConfigurer forwardToTestPage() {
		return new WebMvcConfigurer() {
			@Override
			public void addViewControllers(ViewControllerRegistry registry) {
				// 将根路径重定向到测试管理面板
				registry.addViewController("/").setViewName("redirect:/test-admin-dashboard.html");
			}
		};
	}
}