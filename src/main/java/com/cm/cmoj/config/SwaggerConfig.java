package com.cm.cmoj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * author DingTao
 * Date 2025/1/8 11:43
 *
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebSecurityConfigurerAdapter {
     @Override
     public void configure(HttpSecurity http) throws Exception {
          http.authorizeRequests().antMatchers("/**").permitAll().anyRequest().authenticated().and().csrf()
                  .disable();
     }
}
