//package org.example.auth.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.*;
//import org.springframework.web.servlet.view.InternalResourceViewResolver;
//
//@Configuration
//@EnableWebMvc
//public class MvcConfig implements WebMvcConfigurer {
//
////    @Bean
////    public ThymeleafViewResolver viewResolver(){
////        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
////        viewResolver.setTemplateEngine(templateEngine());
////        // NOTE 'order' and 'viewNames' are optional
////        viewResolver.setOrder(1);
////        viewResolver.setViewNames(new String[] {".html", ".xhtml"});
////        return viewResolver;
////    }
////    @Value("${upload.path}")
////    private String uploadPath;
////
////    public void addViewControllers(ViewControllerRegistry registry) {
////        registry.addViewController("/login").setViewName("login");
////    }
//
////    @Override
////    public void addResourceHandlers(ResourceHandlerRegistry registry) {
////        registry.addResourceHandler("/img/**")
////                .addResourceLocations("file:/" + uploadPath + "/"); //windows
//////        .addResourceLocations("file://" + uploadPath + "/"); //linux
////        registry.addResourceHandler("/static/**")
////                .addResourceLocations("classpath:/static/");
////    }
//
////    @Override
////    public void configureViewResolvers(ViewResolverRegistry registry) {
////        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
//////        resolver.setPrefix("/views/");
////        resolver.setSuffix(".html");
////        registry.viewResolver(resolver);
////    }
//}