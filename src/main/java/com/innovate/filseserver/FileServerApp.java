package com.innovate.filseserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.easysoft.commons.annotation.EnableMybatisDataSource;
import com.easysoft.logs.annotation.EnableAuditLog;
import com.innovate.filseserver.interceptor.LoginInterceptor;

/**
 * 启动类
 * @author Founder
 */
@SpringBootApplication
@EnableMybatisDataSource
@EnableAuditLog
public class FileServerApp {
	
	public static void main(String[] args) {
		SpringApplication.run(FileServerApp.class, args);
	}

	/**
	 * 
	 * Title:自定义的配置
	 * @author IvanHsu
	 * @date 2019年5月23日
	 */
	@Configuration
	public class CustomerConfig implements WebMvcConfigurer{

		/*
		 * （非 Javadoc）
		* Title: addCorsMappings
		* Description: 设置跨域限制的问题 Company: easysoft.ltd
		* @param registry
		* @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry)
		 */
		@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/**").allowedOrigins("*").allowCredentials(true)
					.allowedMethods("GET", "POST", "DELETE", "PUT").maxAge(3600);
		}
		
		/*
		 * （非 Javadoc）
		* Title: addViewControllers
		* Description:  设置登录 默认页面
		* @param registry
		* @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry)
		 */
		@Override
	    public void addViewControllers(ViewControllerRegistry registry) {
	    	//login页面在 templates 文件夹下
	        registry.addViewController("/").setViewName("login");
	        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	    }
		
		/*
		 * （非 Javadoc）
		* Title: addInterceptors
		* Description: 静态验证过滤
		* @param registry
		* @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
		 */
		@Override
	    public void addInterceptors(InterceptorRegistry registry) {
	        registry.addInterceptor(new LoginInterceptor())
	        	.addPathPatterns("/**")
	        	.excludePathPatterns("/", 
	        						"/login",
	        						"/getVerify",
	        						"/file_server/delete",
	        						"/file_server/getFileById",
	        						"/file_server/downloadZip",
	        						"/file_server/downloadSingle",
	        						"/file_server/getFileByObjectId",
	        						"/file_server/preUpload",
	        						"/file_server/upload",
	        						"/static/**");
	    }
		
		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
	        registry.addResourceHandler("/static/**")
	        		.addResourceLocations("classpath:/static/");
		}
	}

}
