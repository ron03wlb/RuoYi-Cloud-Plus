package org.dromara.gateway.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.dromara.gateway.utils.WebFluxUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * 黑名单过滤器.
 *
 * @author ruoyi
 */
@Component
public class BlackListUrlFilter extends AbstractGatewayFilterFactory<BlackListUrlFilter.Config> {
  /**
   * 应用黑名单过滤器配置.
   *
   * @param config 黑名单配置对象
   * @return GatewayFilter 网关过滤器实例
   */
  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      String url = exchange.getRequest().getURI().getPath();
      if (config.matchBlacklist(url)) {
        return WebFluxUtils.webFluxResponseWriter(exchange.getResponse(), "请求地址不允许访问");
      }

      return chain.filter(exchange);
    };
  }

  /** 构造函数，初始化黑名单过滤器. */
  public BlackListUrlFilter() {
    super(Config.class);
  }

  /** 黑名单配置类. */
  public static class Config {
    private List<String> blacklistUrl;

    private List<Pattern> blacklistUrlPattern = new ArrayList<>();

    /**
     * 检查URL是否匹配黑名单.
     *
     * @param url 要检查的URL
     * @return 如果URL匹配黑名单返回true，否则返回false
     */
    public boolean matchBlacklist(String url) {
      return !blacklistUrlPattern.isEmpty()
          && blacklistUrlPattern.stream().anyMatch(p -> p.matcher(url).find());
    }

    /**
     * 获取黑名单URL列表.
     *
     * @return 黑名单URL列表
     */
    public List<String> getBlacklistUrl() {
      return blacklistUrl;
    }

    /**
     * 设置黑名单URL列表并编译为正则表达式模式.
     *
     * @param blacklistUrl 黑名单URL列表
     */
    public void setBlacklistUrl(List<String> blacklistUrl) {
      this.blacklistUrl = blacklistUrl;
      this.blacklistUrlPattern.clear();
      this.blacklistUrl.forEach(
          url -> {
            this.blacklistUrlPattern.add(
                Pattern.compile(url.replaceAll("\\*\\*", "(.*?)"), Pattern.CASE_INSENSITIVE));
          });
    }
  }
}
