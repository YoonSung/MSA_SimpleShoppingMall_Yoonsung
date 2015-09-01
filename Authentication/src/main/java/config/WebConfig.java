package config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.ByteUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import web.interceptor.AuthenticationInterceptor;

import javax.servlet.Filter;
import java.security.Key;

/**
 * Created by yoon on 15. 8. 5..
 */
@Configuration
@ComponentScan({"web"})
public class WebConfig extends WebMvcConfigurationSupport {

    public static final String TEST_COOKIE_VALUE = "abcdefg";

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns("/users");

    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Key secretKey() {
        return new AesKey(ByteUtil.randomBytes(16));
    }

    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
