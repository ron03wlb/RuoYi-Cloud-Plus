package org.dromara.common.encrypt.core.encryptor;

import org.dromara.common.encrypt.core.EncryptContext;
import org.dromara.common.encrypt.enumd.AlgorithmType;
import org.dromara.common.encrypt.enumd.EncodeType;
import org.dromara.common.encrypt.utils.EncryptUtils;

/**
 * BASE64 encoding algorithm implementation.
 *
 * <p>Provides BASE64 encoding and decoding operations. Note that BASE64 is not a true encryption
 * algorithm but rather an encoding scheme.
 *
 * @author 老马
 * @version 4.6.0
 */
public class Base64Encryptor extends AbstractEncryptor {

  /**
   * Constructs a BASE64 encryptor with the given encryption context.
   *
   * @param context the encryption context (no keys required for BASE64)
   */
  public Base64Encryptor(EncryptContext context) {
    super(context);
  }

  /**
   * Gets the encryption algorithm type.
   *
   * @return AlgorithmType.BASE64
   */
  @Override
  public AlgorithmType algorithm() {
    return AlgorithmType.BASE64;
  }

  /**
   * Encodes the given string value using BASE64.
   *
   * @param value the plaintext string to encode
   * @param encodeType the encoding format (ignored for BASE64)
   * @return the BASE64 encoded string
   */
  @Override
  public String encrypt(String value, EncodeType encodeType) {
    return EncryptUtils.encryptByBase64(value);
  }

  /**
   * Decodes the given BASE64 encoded string value.
   *
   * @param value the BASE64 encoded string to decode
   * @return the decoded plaintext string
   */
  @Override
  public String decrypt(String value) {
    return EncryptUtils.decryptByBase64(value);
  }
}
