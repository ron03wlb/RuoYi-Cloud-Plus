package org.dromara.common.core.config;

import com.fasterxml.jackson.databind.Module;
import io.vavr.jackson.datatype.VavrModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Vavr Jackson 序列化配置
 *
 * <p>配置 Jackson 支持 Vavr 类型的序列化和反序列化，包括：
 *
 * <ul>
 *   <li>Option - 序列化为值或 null
 *   <li>Either - 序列化为包含 left 或 right 的 JSON 对象
 *   <li>Try - 序列化为成功值或异常信息
 *   <li>Vavr Collections - 序列化为 JSON 数组
 * </ul>
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * // Option 序列化
 * Option<String> some = Option.of("value"); // -> "value"
 * Option<String> none = Option.none();      // -> null
 *
 * // Either 序列化
 * Either<String, Integer> right = Either.right(42);
 * // -> {"right": 42}
 * Either<String, Integer> left = Either.left("error");
 * // -> {"left": "error"}
 *
 * // Vavr List 序列化
 * io.vavr.collection.List<String> list = List.of("a", "b", "c");
 * // -> ["a", "b", "c"]
 * }</pre>
 *
 * @author RuoYi-Cloud-Plus
 * @see io.vavr.jackson.datatype.VavrModule
 * @since 2.5.0
 */
@Configuration
public class VavrJacksonConfig {

  /**
   * 注册 Vavr Jackson 模块
   *
   * <p>该模块提供了 Vavr 所有核心类型的序列化器和反序列化器。
   *
   * @return Vavr Jackson Module
   */
  @Bean
  public Module vavrModule() {
    return new VavrModule();
  }
}
