package com.sparta.slackservice.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI slackServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Slack Service API")
                        .description("Slack 메시지 전송 및 관리 기능을 제공하는 서비스입니다.")
                        .version("v1.0.0"))
                .servers(List.of(
                        new Server().url("http://localhost:8086").description("Local Server")
                ));
    }
}

