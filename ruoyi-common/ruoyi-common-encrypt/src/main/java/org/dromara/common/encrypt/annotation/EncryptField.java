package org.dromara.common.encrypt.annotation;

import java.lang.annotation.*;
import org.dromara.common.encrypt.enumd.AlgorithmType;
import org.dromara.common.encrypt.enumd.EncodeType;

/**
 * Field-level encryption annotation for automatic data encryption and decryption.
 *
 * <p>This annotation can be applied to String fields in entity classes to enable automatic
 * encryption before database persistence and decryption after retrieval. It supports multiple
 * encryption algorithms including AES, RSA, SM2, SM4, and BASE64.
 *
 * @author 老马
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptField {

  /**
   * Specifies the encryption algorithm to use.
   *
   * @return the algorithm type (default: DEFAULT, which uses the global configuration)
   */
  AlgorithmType algorithm() default AlgorithmType.DEFAULT;

  /**
   * Specifies the secret key for symmetric encryption algorithms.
   *
   * <p>Required for AES and SM4 algorithms. If not specified, uses the global configuration.
   *
   * @return the secret key (default: empty string, uses global configuration)
   */
  String password() default "";

  /**
   * Specifies the public key for asymmetric encryption algorithms.
   *
   * <p>Required for RSA and SM2 algorithms. Used for encryption operations.
   *
   * @return the public key (default: empty string, uses global configuration)
   */
  String publicKey() default "";

  /**
   * Specifies the private key for asymmetric encryption algorithms.
   *
   * <p>Required for RSA and SM2 algorithms. Used for decryption operations.
   *
   * @return the private key (default: empty string, uses global configuration)
   */
  String privateKey() default "";

  /**
   * Specifies the encoding format for the encrypted output.
   *
   * <p>Determines whether the encrypted value should be encoded as BASE64 or HEX. This setting is
   * ignored for BASE64 algorithm.
   *
   * @return the encode type (default: DEFAULT, which uses the global configuration)
   */
  EncodeType encode() default EncodeType.DEFAULT;
}
