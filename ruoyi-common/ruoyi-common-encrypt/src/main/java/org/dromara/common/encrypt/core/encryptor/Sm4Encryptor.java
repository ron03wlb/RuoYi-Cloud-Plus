package org.dromara.common.encrypt.core.encryptor;

import org.dromara.common.encrypt.core.EncryptContext;
import org.dromara.common.encrypt.enumd.AlgorithmType;
import org.dromara.common.encrypt.enumd.EncodeType;
import org.dromara.common.encrypt.utils.EncryptUtils;

/**
 * SM4 encryption algorithm implementation.
 *
 * <p>Provides SM4 (Chinese national cryptographic standard) symmetric encryption and decryption
 * with support for both BASE64 and HEX encoding formats.
 *
 * @author 老马
 * @version 4.6.0
 */
public class Sm4Encryptor extends AbstractEncryptor {

  private final EncryptContext context;

  /**
   * Constructs an SM4 encryptor with the given encryption context.
   *
   * @param context the encryption context containing the secret key
   */
  public Sm4Encryptor(EncryptContext context) {
    super(context);
    this.context = context;
  }

  /**
   * Gets the encryption algorithm type.
   *
   * @return AlgorithmType.SM4
   */
  @Override
  public AlgorithmType algorithm() {
    return AlgorithmType.SM4;
  }

  /**
   * Encrypts the given string value using SM4 algorithm.
   *
   * @param value the plaintext string to encrypt
   * @param encodeType the encoding format (BASE64 or HEX)
   * @return the encrypted string encoded in the specified format
   */
  @Override
  public String encrypt(String value, EncodeType encodeType) {
    if (encodeType == EncodeType.HEX) {
      return EncryptUtils.encryptBySm4Hex(value, context.getPassword());
    } else {
      return EncryptUtils.encryptBySm4(value, context.getPassword());
    }
  }

  /**
   * Decrypts the given encrypted string value using SM4 algorithm.
   *
   * @param value the encrypted string to decrypt
   * @return the decrypted plaintext string
   */
  @Override
  public String decrypt(String value) {
    return EncryptUtils.decryptBySm4(value, context.getPassword());
  }
}
