package org.rimand.doc.config;

import org.rimand.doc.config.configUtils.OSValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    String uploadPath;

    //custom login page
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //css+js
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        //bootstrap webjars
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
        //webjars cache
//        .setCacheControl(CacheControl.maxAge(30L, TimeUnit.DAYS).cachePublic())
//        .resourceChain(true)
//        .addResolver(new WebJarsResourceResolver())
        ;

        //upload
        uploadPath = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";
        String os = System.getProperty("os.name").toLowerCase();
        String resourceLocations;
        if (OSValidator.isWindows()) {
            resourceLocations = "file:/" + uploadPath;
        } else {
            resourceLocations = "file://" + uploadPath;
        }
        registry.addResourceHandler("/doc/**")
                .addResourceLocations(resourceLocations);
    }

}