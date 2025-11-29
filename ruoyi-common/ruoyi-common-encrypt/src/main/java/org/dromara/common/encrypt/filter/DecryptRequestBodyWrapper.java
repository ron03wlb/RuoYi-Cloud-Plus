package org.dromara.common.encrypt.filter;

import cn.hutool.core.io.IoUtil;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.encrypt.utils.EncryptUtils;
import org.springframework.http.MediaType;

/**
 * HTTP request wrapper for automatic request body decryption.
 *
 * <p>This wrapper decrypts the request body using a two-layer encryption scheme: RSA for key
 * exchange and AES for the actual data encryption.
 *
 * @author wdhcr
 */
public class DecryptRequestBodyWrapper extends HttpServletRequestWrapper {

  private final byte[] body;

  /**
   * Constructs a request wrapper that decrypts the request body.
   *
   * <p>Decryption process: 1. Extract RSA-encrypted AES key from request header 2. Decrypt AES key
   * using RSA private key 3. Decode the AES key from BASE64 4. Decrypt request body using the AES
   * key
   *
   * @param request the original HTTP request
   * @param privateKey the RSA private key for decrypting the AES key
   * @param headerFlag the header name containing the encrypted AES key
   * @throws IOException if an I/O error occurs during decryption
   */
  public DecryptRequestBodyWrapper(HttpServletRequest request, String privateKey, String headerFlag)
      throws IOException {
    super(request);
    // Extract AES password encrypted with RSA
    String headerRsa = request.getHeader(headerFlag);
    String decryptAes = EncryptUtils.decryptByRsa(headerRsa, privateKey);
    // Decrypt AES password
    String aesPassword = EncryptUtils.decryptByBase64(decryptAes);
    request.setCharacterEncoding(Constants.UTF8);
    byte[] readBytes = IoUtil.readBytes(request.getInputStream(), false);
    String requestBody = new String(readBytes, StandardCharsets.UTF_8);
    // Decrypt body using AES
    String decryptBody = EncryptUtils.decryptByAes(requestBody, aesPassword);
    body = decryptBody.getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public BufferedReader getReader() {
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }

  @Override
  public int getContentLength() {
    return body.length;
  }

  @Override
  public long getContentLengthLong() {
    return body.length;
  }

  @Override
  public String getContentType() {
    return MediaType.APPLICATION_JSON_VALUE;
  }

  @Override
  public ServletInputStream getInputStream() {
    final ByteArrayInputStream bais = new ByteArrayInputStream(body);
    return new ServletInputStream() {
      @Override
      public int read() {
        return bais.read();
      }

      @Override
      public int available() {
        return body.length;
      }

      @Override
      public boolean isFinished() {
        return false;
      }

      @Override
      public boolean isReady() {
        return false;
      }

      @Override
      public void setReadListener(ReadListener readListener) {}
    };
  }
}
