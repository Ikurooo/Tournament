package at.ac.tuwien.sepr.assignment.individual.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for handling CORS (Cross-Origin Resource Sharing)
 * settings in the application.
 * This configuration is active only when the active profile is not "prod".
 * It effectively disables CORS, allowing requests from any origin during development.
 * Disabling CORS in production environments is not recommended for security reasons.
 */
@Configuration
@Profile("!prod")
public class WebConfig implements WebMvcConfigurer {

  /**
   * Configure CORS mappings to allow requests from any origin and with specified HTTP methods.
   *
   * @param registry The CORS registry to configure
   *
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedMethods("GET", "POST", "OPTIONS", "HEAD", "DELETE", "PUT", "PATCH");
  }
}
