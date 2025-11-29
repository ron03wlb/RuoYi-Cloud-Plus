package org.dromara.common.encrypt.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import org.dromara.common.encrypt.core.EncryptorManager;
import org.dromara.common.encrypt.interceptor.MybatisDecryptInterceptor;
import org.dromara.common.encrypt.interceptor.MybatisEncryptInterceptor;
import org.dromara.common.encrypt.properties.EncryptorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for MyBatis field-level encryption and decryption.
 *
 * <p>This configuration is activated when the property "mybatis-encryptor.enable" is set to true.
 * It sets up interceptors for automatic encryption before database writes and decryption after
 * reads.
 *
 * @author 老马
 * @version 4.6.0
 */
@AutoConfiguration(after = MybatisPlusAutoConfiguration.class)
@EnableConfigurationProperties({EncryptorProperties.class, MybatisPlusProperties.class})
@ConditionalOnClass(MybatisPlusAutoConfiguration.class)
@ConditionalOnProperty(value = "mybatis-encryptor.enable", havingValue = "true")
public class EncryptorAutoConfiguration {

  @Autowired private EncryptorProperties properties;

  /**
   * Creates the encryptor manager that handles encryption operations.
   *
   * <p>Scans the specified type aliases package for entity classes with encrypted fields and caches
   * the field metadata for performance.
   *
   * @param mybatisPlusProperties the MyBatis Plus properties containing the type aliases package
   * @return the configured encryptor manager
   */
  @Bean
  public EncryptorManager encryptorManager(MybatisPlusProperties mybatisPlusProperties) {
    return new EncryptorManager(mybatisPlusProperties.getTypeAliasesPackage());
  }

  /**
   * Creates the MyBatis interceptor for automatic field encryption before database writes.
   *
   * @param encryptorManager the encryptor manager for handling encryption operations
   * @return the configured encryption interceptor
   */
  @Bean
  public MybatisEncryptInterceptor mybatisEncryptInterceptor(EncryptorManager encryptorManager) {
    return new MybatisEncryptInterceptor(encryptorManager, properties);
  }

  /**
   * Creates the MyBatis interceptor for automatic field decryption after database reads.
   *
   * @param encryptorManager the encryptor manager for handling decryption operations
   * @return the configured decryption interceptor
   */
  @Bean
  public MybatisDecryptInterceptor mybatisDecryptInterceptor(EncryptorManager encryptorManager) {
    return new MybatisDecryptInterceptor(encryptorManager, properties);
  }
}
