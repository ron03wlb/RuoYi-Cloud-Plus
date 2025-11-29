package org.dromara.common.encrypt.core.encryptor;

import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.encrypt.core.EncryptContext;
import org.dromara.common.encrypt.enumd.AlgorithmType;
import org.dromara.common.encrypt.enumd.EncodeType;
import org.dromara.common.encrypt.utils.EncryptUtils;

/**
 * RSA encryption algorithm implementation.
 *
 * <p>Provides RSA (Rivest-Shamir-Adleman) asymmetric encryption and decryption with support for
 * both BASE64 and HEX encoding formats. Requires both public and private keys for operation.
 *
 * @author 老马
 * @version 4.6.0
 */
public class RsaEncryptor extends AbstractEncryptor {

  private final EncryptContext context;

  /**
   * Constructs an RSA encryptor with the given encryption context.
   *
   * <p>Validates that both public and private keys are provided.
   *
   * @param context the encryption context containing public and private keys
   * @throws IllegalArgumentException if either public or private key is missing
   */
  public RsaEncryptor(EncryptContext context) {
    super(context);
    String privateKey = context.getPrivateKey();
    String publicKey = context.getPublicKey();
    if (StringUtils.isAnyEmpty(privateKey, publicKey)) {
      throw new IllegalArgumentException("RSA公私钥均需要提供，公钥加密，私钥解密。");
    }
    this.context = context;
  }

  /**
   * Gets the encryption algorithm type.
   *
   * @return AlgorithmType.RSA
   */
  @Override
  public AlgorithmType algorithm() {
    return AlgorithmType.RSA;
  }

  /**
   * Encrypts the given string value using RSA public key.
   *
   * @param value the plaintext string to encrypt
   * @param encodeType the encoding format (BASE64 or HEX)
   * @return the encrypted string encoded in the specified format
   */
  @Override
  public String encrypt(String value, EncodeType encodeType) {
    if (encodeType == EncodeType.HEX) {
      return EncryptUtils.encryptByRsaHex(value, context.getPublicKey());
    } else {
      return EncryptUtils.encryptByRsa(value, context.getPublicKey());
    }
  }

  /**
   * Decrypts the given encrypted string value using RSA private key.
   *
   * @param value the encrypted string to decrypt
   * @return the decrypted plaintext string
   */
  @Override
  public String decrypt(String value) {
    return EncryptUtils.decryptByRsa(value, context.getPrivateKey());
  }
}
