package com.imprenta.ordenes_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:/app/uploads}")
    private String uploadDir;

    @Value("${file.receipts-dir:/app/receipts}")
    private String receiptsDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");

        registry.addResourceHandler("/comprobantes/**")
                .addResourceLocations("file:" + receiptsDir + "/");
    }
}