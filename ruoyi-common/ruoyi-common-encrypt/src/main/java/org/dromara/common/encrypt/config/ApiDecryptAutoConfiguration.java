package org.dromara.common.encrypt.config;

import jakarta.servlet.DispatcherType;
import org.dromara.common.encrypt.filter.CryptoFilter;
import org.dromara.common.encrypt.properties.ApiDecryptProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for API request/response encryption and decryption.
 *
 * <p>This configuration is activated when the property "api-decrypt.enabled" is set to true. It
 * registers a servlet filter to handle encryption and decryption of HTTP requests and responses.
 *
 * @author wdhcr
 */
@AutoConfiguration
@EnableConfigurationProperties(ApiDecryptProperties.class)
@ConditionalOnProperty(value = "api-decrypt.enabled", havingValue = "true")
public class ApiDecryptAutoConfiguration {

  /**
   * Creates and registers the crypto filter for handling encryption/decryption of HTTP traffic.
   *
   * <p>The filter is registered with the highest precedence to ensure it processes requests before
   * other filters.
   *
   * @param properties the API decryption properties containing encryption keys and configuration
   * @return the configured crypto filter
   */
  @Bean
  @FilterRegistration(
      name = "cryptoFilter",
      urlPatterns = "/*",
      order = FilterRegistrationBean.HIGHEST_PRECEDENCE,
      dispatcherTypes = DispatcherType.REQUEST)
  public CryptoFilter cryptoFilter(ApiDecryptProperties properties) {
    return new CryptoFilter(properties);
  }
}
