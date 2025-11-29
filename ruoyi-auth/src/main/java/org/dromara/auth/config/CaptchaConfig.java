package org.dromara.auth.config;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import java.awt.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Captcha configuration for creating various types of verification codes.
 *
 * @author Lion Li
 */
@Configuration
public class CaptchaConfig {

  private static final int WIDTH = 160;
  private static final int HEIGHT = 60;
  private static final Color BACKGROUND = Color.LIGHT_GRAY;
  private static final Font FONT = new Font("Arial", Font.BOLD, 48);

  /**
   * Creates a circle captcha with circular interference patterns.
   *
   * @return configured CircleCaptcha instance
   */
  @Lazy
  @Bean
  public CircleCaptcha circleCaptcha() {
    CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(WIDTH, HEIGHT);
    captcha.setBackground(BACKGROUND);
    captcha.setFont(FONT);
    return captcha;
  }

  /**
   * Creates a line captcha with line interference patterns.
   *
   * @return configured LineCaptcha instance
   */
  @Lazy
  @Bean
  public LineCaptcha lineCaptcha() {
    LineCaptcha captcha = CaptchaUtil.createLineCaptcha(WIDTH, HEIGHT);
    captcha.setBackground(BACKGROUND);
    captcha.setFont(FONT);
    return captcha;
  }

  /**
   * Creates a shear captcha with distortion interference patterns.
   *
   * @return configured ShearCaptcha instance
   */
  @Lazy
  @Bean
  public ShearCaptcha shearCaptcha() {
    ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(WIDTH, HEIGHT);
    captcha.setBackground(BACKGROUND);
    captcha.setFont(FONT);
    return captcha;
  }
}
