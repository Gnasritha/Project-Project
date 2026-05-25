package com.aaseya.momthathel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI momthathlelOpenAPI() {
        io.swagger.v3.oas.models.info.License apiLicense =
                new io.swagger.v3.oas.models.info.License()
                        .name("Private — Aaseya")
                        .url("https://aaseya.com");

        return new OpenAPI()
                .info(new Info()
                        .title("Momthathel — Mobile Inspection API")
                        .description("REST APIs for the Momthathel Field Inspector mobile application. "
                                + "Covers inspection visit creation, license verification, and mapped inspection type retrieval.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Aaseya Development Team")
                                .email("dev@aaseya.com"))
                        .license(apiLicense))
                .servers(List.of(
                        new Server().url("http://localhost:8090").description("Local Development"),
                        new Server().url("https://api.momthathel.aaseya.com").description("Production")
                ));
    }
}
