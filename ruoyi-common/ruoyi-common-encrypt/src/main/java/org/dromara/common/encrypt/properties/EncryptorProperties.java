package org.dromara.common.encrypt.properties;

import lombok.Data;
import org.dromara.common.encrypt.enumd.AlgorithmType;
import org.dromara.common.encrypt.enumd.EncodeType;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for MyBatis field-level encryption and decryption.
 *
 * <p>These properties are bound from the "mybatis-encryptor" prefix in application configuration
 * and provide default values for fields not explicitly configured with {@link
 * org.dromara.common.encrypt.annotation.EncryptField}.
 *
 * @author 老马
 * @version 4.6.0
 */
@Data
@ConfigurationProperties(prefix = "mybatis-encryptor")
public class EncryptorProperties {

  /** Enable/disable MyBatis encryption feature. */
  private Boolean enable;

  /** Default encryption algorithm to use. */
  private AlgorithmType algorithm;

  /** Default secret key for symmetric encryption algorithms (AES, SM4). */
  private String password;

  /** Default public key for asymmetric encryption algorithms (RSA, SM2). */
  private String publicKey;

  /** Default private key for asymmetric encryption algorithms (RSA, SM2). */
  private String privateKey;

  /** Default encoding format for encrypted output (BASE64 or HEX). */
  private EncodeType encode;
}
