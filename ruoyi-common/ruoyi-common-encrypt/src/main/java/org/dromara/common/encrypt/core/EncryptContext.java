package org.dromara.common.encrypt.core;

import lombok.Data;
import org.dromara.common.encrypt.enumd.AlgorithmType;
import org.dromara.common.encrypt.enumd.EncodeType;

/**
 * Encryption context for passing necessary parameters to encryptors.
 *
 * <p>This class encapsulates all configuration parameters required for encryption and decryption
 * operations, including algorithm selection, keys, and encoding preferences.
 *
 * @author 老马
 * @version 4.6.0
 */
@Data
public class EncryptContext {

  /** The encryption algorithm to use. */
  private AlgorithmType algorithm;

  /** The secret key for symmetric encryption algorithms (AES, SM4). */
  private String password;

  /** The public key for asymmetric encryption algorithms (RSA, SM2). */
  private String publicKey;

  /** The private key for asymmetric encryption algorithms (RSA, SM2). */
  private String privateKey;

  /** The encoding format for encrypted output (BASE64 or HEX). */
  private EncodeType encode;
}
