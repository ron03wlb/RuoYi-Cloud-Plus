package org.dromara.common.web.core;

import jakarta.servlet.http.HttpServletRequest;
import org.dromara.common.web.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * I18nLocaleResolver (国际化区域解析器) 单元测试
 * <p>
 * 用途: 从请求头content-language解析用户的语言区域
 * 测试范围: resolveLocale()方法的各种场景
 *
 * @author Test Team
 */
@DisplayName("I18nLocaleResolver (国际化区域解析器) 单元测试")
class I18nLocaleResolverTest extends BaseUnitTest {

    @InjectMocks
    private I18nLocaleResolver localeResolver;

    @Mock
    private HttpServletRequest request;

    @Nested
    @DisplayName("1. 标准语言格式测试")
    class StandardLanguageFormatTests {

        @Test
        @DisplayName("应该正确解析中文简体 (zh_CN)")
        void shouldResolveChineseSimplified() {
            // Arrange
            when(request.getHeader("content-language")).thenReturn("zh_CN");

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert
            assertThat(locale).isNotNull();
            assertThat(locale.getLanguage()).isEqualTo("zh");
            assertThat(locale.getCountry()).isEqualTo("CN");
        }

        @Test
        @DisplayName("应该正确解析中文繁体 (zh_TW)")
        void shouldResolveChineseTraditional() {
            // Arrange
            when(request.getHeader("content-language")).thenReturn("zh_TW");

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert
            assertThat(locale).isNotNull();
            assertThat(locale.getLanguage()).isEqualTo("zh");
            assertThat(locale.getCountry()).isEqualTo("TW");
        }

        @Test
        @DisplayName("应该正确解析英语美国 (en_US)")
        void shouldResolveEnglishUS() {
            // Arrange
            when(request.getHeader("content-language")).thenReturn("en_US");

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert
            assertThat(locale).isNotNull();
            assertThat(locale.getLanguage()).isEqualTo("en");
            assertThat(locale.getCountry()).isEqualTo("US");
        }

        @Test
        @DisplayName("应该正确解析英语英国 (en_GB)")
        void shouldResolveEnglishGB() {
            // Arrange
            when(request.getHeader("content-language")).thenReturn("en_GB");

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert
            assertThat(locale).isNotNull();
            assertThat(locale.getLanguage()).isEqualTo("en");
            assertThat(locale.getCountry()).isEqualTo("GB");
        }

        @Test
        @DisplayName("应该正确解析日语 (ja_JP)")
        void shouldResolveJapanese() {
            // Arrange
            when(request.getHeader("content-language")).thenReturn("ja_JP");

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert
            assertThat(locale).isNotNull();
            assertThat(locale.getLanguage()).isEqualTo("ja");
            assertThat(locale.getCountry()).isEqualTo("JP");
        }

        @Test
        @DisplayName("应该正确解析韩语 (ko_KR)")
        void shouldResolveKorean() {
            // Arrange
            when(request.getHeader("content-language")).thenReturn("ko_KR");

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert
            assertThat(locale).isNotNull();
            assertThat(locale.getLanguage()).isEqualTo("ko");
            assertThat(locale.getCountry()).isEqualTo("KR");
        }

        @Test
        @DisplayName("应该正确解析法语 (fr_FR)")
        void shouldResolveFrench() {
            // Arrange
            when(request.getHeader("content-language")).thenReturn("fr_FR");

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert
            assertThat(locale).isNotNull();
            assertThat(locale.getLanguage()).isEqualTo("fr");
            assertThat(locale.getCountry()).isEqualTo("FR");
        }

        @Test
        @DisplayName("应该正确解析德语 (de_DE)")
        void shouldResolveGerman() {
            // Arrange
            when(request.getHeader("content-language")).thenReturn("de_DE");

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert
            assertThat(locale).isNotNull();
            assertThat(locale.getLanguage()).isEqualTo("de");
            assertThat(locale.getCountry()).isEqualTo("DE");
        }
    }

    @Nested
    @DisplayName("2. 特殊情况测试")
    class SpecialCaseTests {

        @Test
        @DisplayName("当没有content-language头时应该返回系统默认Locale")
        void shouldReturnDefaultLocaleWhenNoHeader() {
            // Arrange
            when(request.getHeader("content-language")).thenReturn(null);

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert
            assertThat(locale).isNotNull();
            assertThat(locale).isEqualTo(Locale.getDefault());
        }

        @Test
        @DisplayName("当content-language为空字符串时应该返回系统默认Locale")
        void shouldReturnDefaultLocaleWhenEmptyHeader() {
            // Arrange
            when(request.getHeader("content-language")).thenReturn("");

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert
            assertThat(locale).isNotNull();
            assertThat(locale).isEqualTo(Locale.getDefault());
        }
    }

    @Nested
    @DisplayName("3. setLocale方法测试")
    class SetLocaleTests {

        @Test
        @DisplayName("setLocale方法应该存在但不执行任何操作")
        void setLocaleShouldDoNothing() {
            // Arrange
            Locale testLocale = Locale.CHINA;

            // Act - 不应该抛出异常
            assertThatCode(() ->
                localeResolver.setLocale(request, null, testLocale)
            ).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("4. 真实业务场景测试")
    class RealWorldScenarioTests {

        @Test
        @DisplayName("场景: 中国用户访问系统")
        void shouldHandleChineseUserRequest() {
            // Arrange - 模拟来自中国的用户请求
            when(request.getHeader("content-language")).thenReturn("zh_CN");

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert - 应该返回中文简体
            assertThat(locale.getLanguage()).isEqualTo("zh");
            assertThat(locale.getCountry()).isEqualTo("CN");
            assertThat(locale.toString()).isEqualTo("zh_CN");
        }

        @Test
        @DisplayName("场景: 美国用户访问系统")
        void shouldHandleUSUserRequest() {
            // Arrange - 模拟来自美国的用户请求
            when(request.getHeader("content-language")).thenReturn("en_US");

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert - 应该返回英语美国
            assertThat(locale.getLanguage()).isEqualTo("en");
            assertThat(locale.getCountry()).isEqualTo("US");
            assertThat(locale.toString()).isEqualTo("en_US");
        }

        @Test
        @DisplayName("场景: 未指定语言的用户访问系统")
        void shouldHandleRequestWithoutLanguagePreference() {
            // Arrange - 模拟未设置语言偏好的请求
            when(request.getHeader("content-language")).thenReturn(null);

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert - 应该使用系统默认语言
            assertThat(locale).isEqualTo(Locale.getDefault());
        }
    }

    @Nested
    @DisplayName("5. 方法调用验证测试")
    class MethodInvocationTests {

        @Test
        @DisplayName("resolveLocale应该调用getHeader方法")
        void resolveLocaleShouldCallGetHeader() {
            // Arrange
            when(request.getHeader("content-language")).thenReturn("zh_CN");

            // Act
            localeResolver.resolveLocale(request);

            // Assert
            verify(request, times(1)).getHeader("content-language");
        }

        @Test
        @DisplayName("当语言为空时不应该调用split")
        void shouldNotSplitWhenLanguageIsNull() {
            // Arrange
            when(request.getHeader("content-language")).thenReturn(null);

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert - 直接返回默认值，不应该抛出NullPointerException
            assertThat(locale).isNotNull();
        }
    }

    @Nested
    @DisplayName("6. Locale对象属性验证")
    class LocalePropertyValidationTests {

        @Test
        @DisplayName("返回的Locale对象应该包含正确的语言和国家代码")
        void localeShouldContainCorrectLanguageAndCountry() {
            // Arrange
            when(request.getHeader("content-language")).thenReturn("es_ES");

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert
            assertThat(locale).isNotNull();
            assertThat(locale.getLanguage()).isEqualTo("es");
            assertThat(locale.getCountry()).isEqualTo("ES");
            assertThat(locale.getVariant()).isEmpty();
        }

        @Test
        @DisplayName("Locale对象toString应该返回正确格式")
        void localeToStringShouldReturnCorrectFormat() {
            // Arrange
            when(request.getHeader("content-language")).thenReturn("pt_BR");

            // Act
            Locale locale = localeResolver.resolveLocale(request);

            // Assert
            assertThat(locale.toString()).isEqualTo("pt_BR");
        }
    }
}
