package org.dromara.common.doc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.doc.config.properties.SpringDocProperties;
import org.dromara.common.doc.handler.OpenApiHandler;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 接口文档配置.
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@AutoConfiguration(before = SpringDocConfiguration.class)
@EnableConfigurationProperties(SpringDocProperties.class)
@ConditionalOnProperty(
    name = "springdoc.api-docs.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SpringDocAutoConfiguration {

  private final ServerProperties serverProperties;

  @Value("${spring.application.name}")
  private String appName;

  /**
   * Creates and configures the OpenAPI specification bean. This method initializes the OpenAPI
   * documentation with information from configuration properties, including document metadata,
   * external documentation, tags, paths, and security schemes.
   *
   * @param properties the SpringDoc configuration properties
   * @return configured OpenAPI instance
   */
  @Bean
  @ConditionalOnMissingBean(OpenAPI.class)
  public OpenAPI openApi(SpringDocProperties properties) {
    OpenAPI openApi = new OpenAPI();
    // 文档基本信息
    SpringDocProperties.InfoProperties infoProperties = properties.getInfo();
    Info info = convertInfo(infoProperties);
    openApi.info(info);
    // 扩展文档信息
    openApi.externalDocs(properties.getExternalDocs());
    openApi.tags(properties.getTags());
    openApi.paths(properties.getPaths());
    if (properties.getComponents() != null) {
      openApi.components(properties.getComponents());
      Set<String> keySet = properties.getComponents().getSecuritySchemes().keySet();
      List<SecurityRequirement> list = new ArrayList<>();
      SecurityRequirement securityRequirement = new SecurityRequirement();
      keySet.forEach(securityRequirement::addList);
      list.add(securityRequirement);
      openApi.security(list);
    }
    return openApi;
  }

  /**
   * Converts SpringDoc info properties to OpenAPI Info object. Maps configuration properties to the
   * OpenAPI Info model for documentation metadata.
   *
   * @param infoProperties the info properties from configuration
   * @return OpenAPI Info object with mapped values
   */
  private Info convertInfo(SpringDocProperties.InfoProperties infoProperties) {
    Info info = new Info();
    info.setTitle(infoProperties.getTitle());
    info.setDescription(infoProperties.getDescription());
    info.setContact(infoProperties.getContact());
    info.setLicense(infoProperties.getLicense());
    info.setVersion(infoProperties.getVersion());
    return info;
  }

  /**
   * Creates a custom OpenAPI handler service. This bean provides enhanced OpenAPI processing
   * capabilities with customized tag handling.
   *
   * @param openAPI optional OpenAPI configuration
   * @param securityParser security service for handling security requirements
   * @param springDocConfigProperties SpringDoc configuration properties
   * @param propertyResolverUtils utility for resolving property placeholders
   * @param openApiBuilderCustomisers optional list of OpenAPI builder customizers
   * @param serverBaseUrlCustomisers optional list of server base URL customizers
   * @param javadocProvider optional Javadoc provider for documentation extraction
   * @return custom OpenAPIService implementation
   */
  @Bean
  public OpenAPIService openApiBuilder(
      Optional<OpenAPI> openAPI,
      SecurityService securityParser,
      SpringDocConfigProperties springDocConfigProperties,
      PropertyResolverUtils propertyResolverUtils,
      Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomisers,
      Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomisers,
      Optional<JavadocProvider> javadocProvider) {
    return new OpenApiHandler(
        openAPI,
        securityParser,
        springDocConfigProperties,
        propertyResolverUtils,
        openApiBuilderCustomisers,
        serverBaseUrlCustomisers,
        javadocProvider);
  }

  /**
   * Creates an OpenAPI customizer that processes the generated OpenAPI specification. This
   * customizer adds the gateway-forwarded service prefix to all API paths to ensure correct routing
   * through the API gateway.
   *
   * @return OpenApiCustomizer that modifies API paths with the forwarded prefix
   */
  @Bean
  public OpenApiCustomizer openApiCustomizer() {
    // 对所有路径增加前置上下文路径
    return openApi -> {
      HttpServletRequest request = ServletUtils.getRequest();
      // 从请求头获取gateway转发的服务前缀
      String prefix = StringUtils.blankToDefault(request.getHeader("X-Forwarded-Prefix"), "");
      Paths oldPaths = openApi.getPaths();
      if (oldPaths instanceof PlusPaths) {
        return;
      }
      PlusPaths newPaths = new PlusPaths();
      oldPaths.forEach((k, v) -> newPaths.addPathItem(prefix + k, v));
      openApi.setPaths(newPaths);
    };
  }
}
