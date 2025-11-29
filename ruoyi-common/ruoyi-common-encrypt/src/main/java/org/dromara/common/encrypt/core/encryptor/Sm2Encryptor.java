package org.dromara.common.encrypt.core.encryptor;

import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.encrypt.core.EncryptContext;
import org.dromara.common.encrypt.enumd.AlgorithmType;
import org.dromara.common.encrypt.enumd.EncodeType;
import org.dromara.common.encrypt.utils.EncryptUtils;

/**
 * SM2 encryption algorithm implementation.
 *
 * <p>Provides SM2 (Chinese national cryptographic standard) asymmetric encryption and decryption
 * with support for both BASE64 and HEX encoding formats. Requires both public and private keys for
 * operation.
 *
 * @author 老马
 * @version 4.6.0
 */
public class Sm2Encryptor extends AbstractEncryptor {

  private final EncryptContext context;

  /**
   * Constructs an SM2 encryptor with the given encryption context.
   *
   * <p>Validates that both public and private keys are provided.
   *
   * @param context the encryption context containing public and private keys
   * @throws IllegalArgumentException if either public or private key is missing
   */
  public Sm2Encryptor(EncryptContext context) {
    super(context);
    String privateKey = context.getPrivateKey();
    String publicKey = context.getPublicKey();
    if (StringUtils.isAnyEmpty(privateKey, publicKey)) {
      throw new IllegalArgumentException("SM2公私钥均需要提供，公钥加密，私钥解密。");
    }
    this.context = context;
  }

  /**
   * Gets the encryption algorithm type.
   *
   * @return AlgorithmType.SM2
   */
  @Override
  public AlgorithmType algorithm() {
    return AlgorithmType.SM2;
  }

  /**
   * Encrypts the given string value using SM2 public key.
   *
   * @param value the plaintext string to encrypt
   * @param encodeType the encoding format (BASE64 or HEX)
   * @return the encrypted string encoded in the specified format
   */
  @Override
  public String encrypt(String value, EncodeType encodeType) {
    if (encodeType == EncodeType.HEX) {
      return EncryptUtils.encryptBySm2Hex(value, context.getPublicKey());
    } else {
      return EncryptUtils.encryptBySm2(value, context.getPublicKey());
    }
  }

  /**
   * Decrypts the given encrypted string value using SM2 private key.
   *
   * @param value the encrypted string to decrypt
   * @return the decrypted plaintext string
   */
  @Override
  public String decrypt(String value) {
    return EncryptUtils.decryptBySm2(value, context.getPrivateKey());
  }
}
