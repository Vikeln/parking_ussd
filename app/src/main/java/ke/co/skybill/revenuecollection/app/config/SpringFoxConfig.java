package ke.co.skybill.revenuecollection.app.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.AuthorizationScopeBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;

import static com.google.common.collect.Lists.newArrayList;

@Configuration
@EnableSwagger2
public class SpringFoxConfig extends WebMvcConfigurationSupport {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry
                .addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry
                .addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


    @Bean
    public Docket api(){
        AuthorizationScope[] authScopes = new AuthorizationScope[1];
        authScopes[0] = new AuthorizationScopeBuilder().scope("global").description("full access").build();
        SecurityReference securityReference = SecurityReference.builder().reference("Authorization-key")
                .scopes(authScopes).build();

        ArrayList<SecurityContext> securityContexts = newArrayList(
                SecurityContext.builder().securityReferences(newArrayList(securityReference)).build());

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("ke.co.skybill.revenuecollection.app.controllers"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiEndpointsInfo())
                .securitySchemes(Arrays.asList(apiKey()))
                .securityContexts(securityContexts);

    }

    private ApiInfo apiEndpointsInfo() {
        return new ApiInfoBuilder().title("SKYBILL REVENUE COLLECTION")
                .description("App Service REST API")
                .contact(new Contact("Skylab Sytems Limited", "www.skylabsystems.co.ke", "edward@skylabsystems.co.ke"))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .version("1.0.0")
                .build();
    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
                .clientId("12345")
                .clientSecret("12345")
                .scopeSeparator("Bearer ")
                .useBasicAuthenticationWithAccessCodeGrant(true)
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("Authorization-key", "Authorization", "header");
    }

}