package com.demo.folder.config;

//import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.demo.folder.security.JwtTokenProvider;

@Configuration
public class FeignClientConfig {

//    private final JwtTokenProvider jwtTokenProvider;
//
//    public FeignClientConfig(JwtTokenProvider jwtTokenProvider) {
//        this.jwtTokenProvider = jwtTokenProvider;
//    }
//
//    @Bean
//    public RequestInterceptor requestInterceptor() {
//        return requestTemplate -> {
//            String token = jwtTokenProvider.generateToken("microservice1");
//            requestTemplate.header("Authorization", "Bearer " + token);
//        };
//    }
}
