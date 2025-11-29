package org.dromara.common.encrypt.core.encryptor;

import org.dromara.common.encrypt.core.EncryptContext;
import org.dromara.common.encrypt.core.IEncryptor;

/**
 * Abstract base class for all encryptor implementations.
 *
 * <p>This class provides a common constructor for all encryptors to receive encryption
 * configuration through the {@link EncryptContext}.
 *
 * @author 老马
 * @version 4.6.0
 */
public abstract class AbstractEncryptor implements IEncryptor {

  /**
   * Constructs an encryptor with the given encryption context.
   *
   * <p>Subclasses should validate configuration parameters and perform necessary initialization in
   * their constructors.
   *
   * @param context the encryption context containing algorithm and key configurations
   */
  public AbstractEncryptor(EncryptContext context) {
    // User configuration validation and injection
  }
}
