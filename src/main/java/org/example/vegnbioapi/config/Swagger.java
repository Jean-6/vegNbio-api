package org.example.vegnbioapi.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger {

    //swagger interface : http://localhost:8082/swagger-ui/index.html
    //docs : http://localhost:8082/api-docs

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("API Documentation")
                        .version("1.0.0")
                        .description("Documentation d'API REST - VegnBio")
                        .contact(new Contact()
                                .name("Jean-Jaures")
                                .email("oka.jeanjaures@gmail.com")
                                .url("")));
    }
}
