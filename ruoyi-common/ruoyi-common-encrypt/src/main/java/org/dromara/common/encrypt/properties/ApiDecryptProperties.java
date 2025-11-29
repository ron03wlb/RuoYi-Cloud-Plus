package org.dromara.common.encrypt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for API request/response encryption and decryption.
 *
 * <p>These properties are bound from the "api-decrypt" prefix in application configuration.
 *
 * @author wdhcr
 */
@Data
@ConfigurationProperties(prefix = "api-decrypt")
public class ApiDecryptProperties {

  /** Enable/disable API encryption feature. */
  private Boolean enabled;

  /** Header name containing the encrypted AES key. */
  private String headerFlag;

  /** RSA public key for encrypting response data. */
  private String publicKey;

  /** RSA private key for decrypting request data. */
  private String privateKey;
}
