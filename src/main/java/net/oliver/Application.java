package net.oliver;

import net.oliver.sodi.http.ItakaShop;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class Application {

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    /**
     * 跨域过滤器
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig()); // 4
        return new CorsFilter(source);
    }



    public static void main(String[] args) {

        SpringApplication springApplication = new SpringApplication(Application.class);
//        springApplication.addListeners(new SodiApplicationListener());
        ConfigurableApplicationContext ctx = springApplication.run(args);
        ItakaShop.username = ctx.getEnvironment().getProperty("itaka.username");
        ItakaShop.passwd =ctx.getEnvironment().getProperty("itaka.passwd");
    }
}
