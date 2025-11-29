package org.dromara.common.encrypt.enumd;

/**
 * Enumeration of encoding types for encrypted output.
 *
 * <p>Defines the available encoding formats that can be used to represent encrypted binary data as
 * text strings.
 *
 * @author 老马
 * @version 4.6.0
 */
public enum EncodeType {

  /** Default encoding, uses global configuration from YAML. */
  DEFAULT,

  /** BASE64 encoding format. */
  BASE64,

  /** Hexadecimal (HEX) encoding format. */
  HEX;
}
