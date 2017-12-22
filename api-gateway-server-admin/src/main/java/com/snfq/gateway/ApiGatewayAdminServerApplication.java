package com.snfq.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.snfq.gateway.filter.AuthorizationFilter;
import com.snfq.gateway.filter.AuthorizationProperties;
import com.snfq.gateway.filter.CrossDomainProperties;
import com.snfq.gateway.filter.RemoteIpFilter;
import com.snfq.gateway.filter.SignVerifyFilter;
import com.snfq.gateway.filter.SignVerifyProperties;

@SpringCloudApplication
@EnableZuulProxy
@EnableConfigurationProperties({ SignVerifyProperties.class, CrossDomainProperties.class,
                                 AuthorizationProperties.class })
@EnableFeignClients
@ComponentScan(basePackages = "com.snfq.gateway")
public class ApiGatewayAdminServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayAdminServerApplication.class, args);
    }

    @Bean
    public SignVerifyFilter signFilter(SignVerifyProperties properties) {
        return new SignVerifyFilter(properties.getSignKey(), properties.getExcludeURIs(),
            properties.getIsFilterOpen());
    }

    @Bean
    public AuthorizationFilter authorizationFilter(AuthorizationProperties properties) {
        return new AuthorizationFilter(properties.getExcludeURIs(), properties.getIsFilterOpen());
    }

    @Bean
    public RemoteIpFilter remoteIpFilter() {
        return new RemoteIpFilter();
    }

    //   @Bean
    //    public SystemLogFilter systemLogFiter(SystemLogProperties properties) {
    //        return new SystemLogFilter( properties.getExcludeURIs(), properties.getIsFilterOpen());
    //    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /** 
     * 跨域过滤器 
     * @return 
     */
    @Bean
    public CorsFilter corsFilter(CrossDomainProperties crossDomainProperties) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(crossDomainProperties.getOrigin());
        corsConfiguration.addAllowedHeader(crossDomainProperties.getHeader());
        corsConfiguration.addAllowedMethod(crossDomainProperties.getMethod());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // 4  
        return new CorsFilter(source);
    }

}
