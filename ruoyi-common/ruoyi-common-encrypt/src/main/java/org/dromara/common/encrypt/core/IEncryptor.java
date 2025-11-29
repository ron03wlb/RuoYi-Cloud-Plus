package org.dromara.common.encrypt.core;

import org.dromara.common.encrypt.enumd.AlgorithmType;
import org.dromara.common.encrypt.enumd.EncodeType;

/**
 * Encryptor interface for encryption and decryption operations.
 *
 * <p>This interface defines the contract for all encryption implementations. Each implementation
 * handles a specific encryption algorithm (AES, RSA, SM2, SM4, BASE64).
 *
 * @author 老马
 * @version 4.6.0
 */
public interface IEncryptor {

  /**
   * Gets the encryption algorithm type supported by this encryptor.
   *
   * @return the algorithm type
   */
  AlgorithmType algorithm();

  /**
   * Encrypts the given string value using the specified encoding format.
   *
   * @param value the plaintext string to encrypt
   * @param encodeType the encoding format for the encrypted output (BASE64 or HEX)
   * @return the encrypted string encoded in the specified format
   */
  String encrypt(String value, EncodeType encodeType);

  /**
   * Decrypts the given encrypted string value.
   *
   * @param value the encrypted string to decrypt
   * @return the decrypted plaintext string
   */
  String decrypt(String value);
}
