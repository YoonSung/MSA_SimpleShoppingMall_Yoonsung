package config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import web.interceptor.AuthenticationInterceptor;

import javax.servlet.Filter;

/**
 * Created by yoon on 15. 7. 26..
 */
@Configuration
@ComponentScan({"web"})
public class WebConfig extends WebMvcConfigurationSupport {

    public static final String COOKIE_NAME = "MSA_COOKIE";
    public static final String TEST_COOKIE_VALUE = "abcdefg";

    public static final String RESOLVER_PREFIX = "/WEB-INF/jsp/";
    public static final String RESOLVER_SUFFIX = ".jsp";

    @Override
    protected void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(internalViewResolver());
        super.configureViewResolvers(registry);
    }

    @Override
    protected void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/stylesheet/*").addResourceLocations("/stylesheet/");
        super.addResourceHandlers(registry);
    }

    @Bean
    public ViewResolver internalViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix(RESOLVER_PREFIX);
        viewResolver.setSuffix(RESOLVER_SUFFIX);
        return viewResolver;
    }

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
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }
}
