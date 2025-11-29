package org.dromara.common.encrypt.enumd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dromara.common.encrypt.core.encryptor.*;

/**
 * Enumeration of supported encryption algorithms.
 *
 * <p>Defines the available encryption algorithms and their corresponding encryptor implementations.
 * Each algorithm type is associated with a specific encryptor class.
 *
 * @author 老马
 * @version 4.6.0
 */
@Getter
@AllArgsConstructor
public enum AlgorithmType {

  /** Default algorithm, uses global configuration from YAML. */
  DEFAULT(null),

  /** BASE64 encoding algorithm (not true encryption, just encoding). */
  BASE64(Base64Encryptor.class),

  /** AES (Advanced Encryption Standard) symmetric encryption algorithm. */
  AES(AesEncryptor.class),

  /** RSA (Rivest-Shamir-Adleman) asymmetric encryption algorithm. */
  RSA(RsaEncryptor.class),

  /** SM2 (Chinese national cryptographic standard) asymmetric encryption algorithm. */
  SM2(Sm2Encryptor.class),

  /** SM4 (Chinese national cryptographic standard) symmetric encryption algorithm. */
  SM4(Sm4Encryptor.class);

  /** The encryptor class implementation for this algorithm type. */
  private final Class<? extends AbstractEncryptor> clazz;
}
