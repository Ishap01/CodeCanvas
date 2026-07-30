package com.codecanvas.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentOpenAPI() {

        return new OpenAPI()

                .info(

                        new Info()

                                .title(
                                        "CodeCanvas Payment Service API"
                                )

                                .description(
                                        "Payment and Premium Membership APIs"
                                )

                                .version("1.0")

                                .contact(

                                        new Contact()

                                                .name("Rau Wagh")
                                                .email("your@email.com")

                                )

                );

    }

}