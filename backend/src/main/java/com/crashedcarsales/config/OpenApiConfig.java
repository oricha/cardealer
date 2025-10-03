package com.crashedcarsales.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.openapi.title:Crashed Car Sales API}")
    private String title;

    @Value("${app.openapi.description:Public API for accessing car listings and dealer information}")
    private String description;

    @Value("${app.openapi.version:1.0.0}")
    private String version;

    @Value("${app.openapi.contact.name:Crashed Car Sales Support}")
    private String contactName;

    @Value("${app.openapi.contact.email:support@crashedcarsales.com}")
    private String contactEmail;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version)
                        .contact(new Contact()
                                .name(contactName)
                                .email(contactEmail))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080/api").description("Development server"),
                        new Server().url("https://api.crashedcarsales.com").description("Production server")
                ));
    }
}