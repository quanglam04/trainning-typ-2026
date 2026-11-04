package com.courseshop.courseshop.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/** Bật các annotation cache của Spring; Redis được chọn trong application.properties. */
@Configuration
@EnableCaching
public class CacheConfig {
}
