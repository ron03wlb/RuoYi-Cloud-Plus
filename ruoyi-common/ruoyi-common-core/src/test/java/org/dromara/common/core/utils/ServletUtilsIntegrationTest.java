package org.dromara.common.core.utils;

import org.dromara.common.core.BaseIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ServletUtils 集成测试
 *
 * @author Test Team
 */
@DisplayName("ServletUtils 集成测试")
class ServletUtilsIntegrationTest extends BaseIntegrationTest {

    @AfterEach
    void tearDown() {
        // 清理 RequestContextHolder，避免测试之间相互影响
        RequestContextHolder.resetRequestAttributes();
    }

    @Nested
    @DisplayName("getParameter 方法测试 - 字符串参数获取")
    class GetParameterTest {

        @Test
        @DisplayName("应该获取存在的参数值")
        void shouldReturnParameterValueWhenParameterExists() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("username", "zhangsan");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            // Act
            String result = ServletUtils.getParameter("username");

            // Assert
            assertThat(result).isEqualTo("zhangsan");
        }

        @Test
        @DisplayName("应该返回null当参数不存在时")
        void shouldReturnNullWhenParameterNotExists() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            // Act
            String result = ServletUtils.getParameter("nonExistent");

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该返回默认值当参数不存在时")
        void shouldReturnDefaultValueWhenParameterNotExists() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            // Act
            String result = ServletUtils.getParameter("nonExistent", "defaultValue");

            // Assert
            assertThat(result).isEqualTo("defaultValue");
        }

        @Test
        @DisplayName("应该返回参数值而非默认值当参数存在时")
        void shouldReturnParameterValueNotDefaultWhenParameterExists() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("username", "lisi");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            // Act
            String result = ServletUtils.getParameter("username", "defaultValue");

            // Assert
            assertThat(result).isEqualTo("lisi");
        }
    }

    @Nested
    @DisplayName("getParameterToInt 方法测试 - 整数参数获取")
    class GetParameterToIntTest {

        @Test
        @DisplayName("应该正确转换整数参数")
        void shouldConvertToIntWhenParameterIsInteger() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("age", "25");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            // Act
            Integer result = ServletUtils.getParameterToInt("age");

            // Assert
            assertThat(result).isEqualTo(25);
        }

        @Test
        @DisplayName("应该返回null当参数不存在时")
        void shouldReturnNullWhenParameterNotExists() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            // Act
            Integer result = ServletUtils.getParameterToInt("nonExistent");

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该返回默认值当参数不存在时")
        void shouldReturnDefaultValueWhenParameterNotExists() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            // Act
            Integer result = ServletUtils.getParameterToInt("nonExistent", 100);

            // Assert
            assertThat(result).isEqualTo(100);
        }

        @Test
        @DisplayName("应该返回null当参数值无法转换为整数时")
        void shouldReturnNullWhenParameterCannotConvertToInt() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("age", "invalid");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            // Act
            Integer result = ServletUtils.getParameterToInt("age");

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("getParameterToBool 方法测试 - 布尔参数获取")
    class GetParameterToBoolTest {

        @Test
        @DisplayName("应该正确转换true值")
        void shouldConvertToBoolWhenParameterIsTrue() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("enabled", "true");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            // Act
            Boolean result = ServletUtils.getParameterToBool("enabled");

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该正确转换false值")
        void shouldConvertToBoolWhenParameterIsFalse() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("enabled", "false");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            // Act
            Boolean result = ServletUtils.getParameterToBool("enabled");

            // Assert
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("应该返回默认值当参数不存在时")
        void shouldReturnDefaultValueWhenParameterNotExists() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            // Act
            Boolean result = ServletUtils.getParameterToBool("nonExistent", true);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该正确处理1和0")
        void shouldHandleNumericBooleanValues() {
            // Arrange
            MockHttpServletRequest request1 = new MockHttpServletRequest();
            request1.setParameter("flag", "1");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request1));

            // Act
            Boolean result1 = ServletUtils.getParameterToBool("flag");

            // Assert
            assertThat(result1).isTrue();

            // Arrange
            MockHttpServletRequest request2 = new MockHttpServletRequest();
            request2.setParameter("flag", "0");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request2));

            // Act
            Boolean result2 = ServletUtils.getParameterToBool("flag");

            // Assert
            assertThat(result2).isFalse();
        }
    }

    @Nested
    @DisplayName("getParams 和 getParamMap 方法测试 - 批量参数获取")
    class GetParamsTest {

        @Test
        @DisplayName("getParams应该返回所有参数的数组形式")
        void shouldReturnAllParametersAsArrays() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("name", "zhangsan");
            request.setParameter("age", "25");
            request.addParameter("tags", "java");
            request.addParameter("tags", "spring");

            // Act
            Map<String, String[]> result = ServletUtils.getParams(request);

            // Assert
            assertThat(result).containsKeys("name", "age", "tags");
            assertThat(result.get("name")).containsExactly("zhangsan");
            assertThat(result.get("age")).containsExactly("25");
            assertThat(result.get("tags")).containsExactly("java", "spring");
        }

        @Test
        @DisplayName("getParams返回的Map应该是不可修改的")
        void shouldReturnUnmodifiableMap() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("name", "zhangsan");

            // Act
            Map<String, String[]> result = ServletUtils.getParams(request);

            // Assert
            assertThat(result).isInstanceOf(java.util.Collections.unmodifiableMap(new java.util.HashMap<>()).getClass());
        }

        @Test
        @DisplayName("getParamMap应该返回所有参数的字符串形式")
        void shouldReturnAllParametersAsStrings() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("name", "zhangsan");
            request.setParameter("age", "25");
            request.addParameter("tags", "java");
            request.addParameter("tags", "spring");

            // Act
            Map<String, String> result = ServletUtils.getParamMap(request);

            // Assert
            assertThat(result).containsKeys("name", "age", "tags");
            assertThat(result.get("name")).isEqualTo("zhangsan");
            assertThat(result.get("age")).isEqualTo("25");
            assertThat(result.get("tags")).isEqualTo("java,spring");
        }

        @Test
        @DisplayName("应该处理空参数情况")
        void shouldHandleEmptyParameters() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();

            // Act
            Map<String, String> result = ServletUtils.getParamMap(request);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getRequest/getResponse/getSession 方法测试 - 对象获取")
    class GetRequestResponseSessionTest {

        @Test
        @DisplayName("getRequest应该返回当前请求对象")
        void shouldReturnCurrentRequest() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            // Act
            MockHttpServletRequest result = (MockHttpServletRequest) ServletUtils.getRequest();

            // Assert
            assertThat(result).isSameAs(request);
        }

        @Test
        @DisplayName("getResponse应该返回当前响应对象")
        void shouldReturnCurrentResponse() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));

            // Act
            MockHttpServletResponse result = (MockHttpServletResponse) ServletUtils.getResponse();

            // Assert
            assertThat(result).isSameAs(response);
        }

        @Test
        @DisplayName("getSession应该返回当前会话对象")
        void shouldReturnCurrentSession() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpSession session = new MockHttpSession();
            request.setSession(session);
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            // Act
            MockHttpSession result = (MockHttpSession) ServletUtils.getSession();

            // Assert
            assertThat(result).isSameAs(session);
        }

        @Test
        @DisplayName("getRequest应该返回null当没有请求上下文时")
        void shouldReturnNullWhenNoRequestContext() {
            // Arrange
            RequestContextHolder.resetRequestAttributes();

            // Act
            var result = ServletUtils.getRequest();

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("getRequestAttributes应该返回请求属性")
        void shouldReturnRequestAttributes() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            ServletRequestAttributes attributes = new ServletRequestAttributes(request);
            RequestContextHolder.setRequestAttributes(attributes);

            // Act
            ServletRequestAttributes result = ServletUtils.getRequestAttributes();

            // Assert
            assertThat(result).isSameAs(attributes);
        }
    }

    @Nested
    @DisplayName("getHeader 和 getHeaders 方法测试 - 请求头处理")
    class GetHeaderTest {

        @Test
        @DisplayName("getHeader应该返回指定请求头的值")
        void shouldReturnHeaderValue() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("User-Agent", "Mozilla/5.0");

            // Act
            String result = ServletUtils.getHeader(request, "User-Agent");

            // Assert
            assertThat(result).isEqualTo("Mozilla/5.0");
        }

        @Test
        @DisplayName("getHeader应该返回空字符串当请求头不存在时")
        void shouldReturnEmptyStringWhenHeaderNotExists() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();

            // Act
            String result = ServletUtils.getHeader(request, "NonExistent");

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("getHeader应该对请求头值进行URL解码")
        void shouldDecodeHeaderValue() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Custom-Header", "Hello%20World");

            // Act
            String result = ServletUtils.getHeader(request, "Custom-Header");

            // Assert
            assertThat(result).isEqualTo("Hello World");
        }

        @Test
        @DisplayName("getHeaders应该返回所有请求头")
        void shouldReturnAllHeaders() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("User-Agent", "Mozilla/5.0");
            request.addHeader("Accept", "application/json");
            request.addHeader("Content-Type", "text/html");

            // Act
            Map<String, String> result = ServletUtils.getHeaders(request);

            // Assert
            assertThat(result).hasSize(3);
            assertThat(result).containsKeys("User-Agent", "Accept", "Content-Type");
            assertThat(result.get("User-Agent")).isEqualTo("Mozilla/5.0");
        }

        @Test
        @DisplayName("getHeaders返回的Map应该忽略大小写")
        void shouldReturnCaseInsensitiveMap() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Content-Type", "application/json");

            // Act
            Map<String, String> result = ServletUtils.getHeaders(request);

            // Assert
            assertThat(result.get("content-type")).isEqualTo("application/json");
            assertThat(result.get("CONTENT-TYPE")).isEqualTo("application/json");
        }
    }

    @Nested
    @DisplayName("renderString 方法测试 - 响应渲染")
    class RenderStringTest {

        @Test
        @DisplayName("应该正确渲染JSON响应")
        void shouldRenderJsonResponse() throws UnsupportedEncodingException {
            // Arrange
            MockHttpServletResponse response = new MockHttpServletResponse();
            String jsonString = "{\"code\":200,\"message\":\"success\"}";

            // Act
            ServletUtils.renderString(response, jsonString);

            // Assert
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getContentType()).startsWith(MediaType.APPLICATION_JSON_VALUE);
            assertThat(response.getCharacterEncoding()).isEqualTo("UTF-8");
            assertThat(response.getContentAsString()).isEqualTo(jsonString);
        }

        @Test
        @DisplayName("应该处理包含中文的响应")
        void shouldHandleChineseCharacters() throws UnsupportedEncodingException {
            // Arrange
            MockHttpServletResponse response = new MockHttpServletResponse();
            String jsonString = "{\"message\":\"操作成功\"}";

            // Act
            ServletUtils.renderString(response, jsonString);

            // Assert
            assertThat(response.getContentAsString()).isEqualTo(jsonString);
        }
    }

    @Nested
    @DisplayName("isAjaxRequest 方法测试 - Ajax请求判断")
    class IsAjaxRequestTest {

        @Test
        @DisplayName("应该识别Accept头包含application/json的请求为Ajax")
        void shouldDetectAjaxByAcceptHeader() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("accept", "application/json");

            // Act
            boolean result = ServletUtils.isAjaxRequest(request);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该识别X-Requested-With头包含XMLHttpRequest的请求为Ajax")
        void shouldDetectAjaxByXRequestedWithHeader() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-Requested-With", "XMLHttpRequest");

            // Act
            boolean result = ServletUtils.isAjaxRequest(request);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该识别URI完全匹配.json的请求为Ajax")
        void shouldDetectAjaxByJsonUri() {
            // Arrange
            // 注意：源码使用 equalsAnyIgnoreCase 而非 endsWithIgnoreCase
            // 所以URI必须完全等于 ".json" 或包含该字符串才会匹配
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI(".json");

            // Act
            boolean result = ServletUtils.isAjaxRequest(request);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该识别URI完全匹配.xml的请求为Ajax")
        void shouldDetectAjaxByXmlUri() {
            // Arrange
            // 注意：源码使用 equalsAnyIgnoreCase 而非 endsWithIgnoreCase
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI(".xml");

            // Act
            boolean result = ServletUtils.isAjaxRequest(request);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该识别__ajax参数为json的请求为Ajax")
        void shouldDetectAjaxByAjaxParameter() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("__ajax", "json");

            // Act
            boolean result = ServletUtils.isAjaxRequest(request);

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该返回false对于普通HTTP请求")
        void shouldReturnFalseForNormalRequest() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/index.html");

            // Act
            boolean result = ServletUtils.isAjaxRequest(request);

            // Assert
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("getClientIP 方法测试 - IP地址获取")
    class GetClientIPTest {

        @Test
        @DisplayName("应该从RequestContextHolder获取客户端IP")
        void shouldGetClientIPFromRequestContext() {
            // Arrange
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRemoteAddr("192.168.1.100");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

            // Act
            String result = ServletUtils.getClientIP();

            // Assert
            assertThat(result).isEqualTo("192.168.1.100");
        }
    }

    @Nested
    @DisplayName("urlEncode 和 urlDecode 方法测试 - URL编解码")
    class UrlEncodeDecodeTest {

        @Test
        @DisplayName("urlEncode应该正确编码URL")
        void shouldEncodeUrl() {
            // Arrange
            String input = "Hello World 你好";

            // Act
            String result = ServletUtils.urlEncode(input);

            // Assert
            assertThat(result).isEqualTo("Hello+World+%E4%BD%A0%E5%A5%BD");
        }

        @Test
        @DisplayName("urlDecode应该正确解码URL")
        void shouldDecodeUrl() {
            // Arrange
            String input = "Hello+World+%E4%BD%A0%E5%A5%BD";

            // Act
            String result = ServletUtils.urlDecode(input);

            // Assert
            assertThat(result).isEqualTo("Hello World 你好");
        }

        @Test
        @DisplayName("urlEncode和urlDecode应该互逆")
        void shouldBeReversible() {
            // Arrange
            String original = "测试字符串 Test String !@#$%";

            // Act
            String encoded = ServletUtils.urlEncode(original);
            String decoded = ServletUtils.urlDecode(encoded);

            // Assert
            assertThat(decoded).isEqualTo(original);
        }

        @Test
        @DisplayName("应该处理特殊字符")
        void shouldHandleSpecialCharacters() {
            // Arrange
            String input = "a=b&c=d e";

            // Act
            String encoded = ServletUtils.urlEncode(input);
            String decoded = ServletUtils.urlDecode(encoded);

            // Assert
            assertThat(decoded).isEqualTo(input);
        }
    }
}
