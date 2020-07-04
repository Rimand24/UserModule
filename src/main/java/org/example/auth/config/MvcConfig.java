package org.example.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    String uploadPath;

    /**
     * custom login page
     */
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //bootstrap+css
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        //upload
        uploadPath = uploadPath.endsWith("/") ? uploadPath : uploadPath+"/";
        registry.addResourceHandler("/doc/**")
                .addResourceLocations("file:/" + uploadPath); //fixme windows
//                .addResourceLocations("file://" + uploadPath + "/"); //fixme linux
    }

}




