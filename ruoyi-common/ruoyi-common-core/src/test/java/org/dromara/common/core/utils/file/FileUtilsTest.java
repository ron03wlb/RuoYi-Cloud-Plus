package org.dromara.common.core.utils.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

/**
 * FileUtils 测试类
 *
 * @author Test Team
 */
@DisplayName("FileUtils 工具类测试")
class FileUtilsTest {

    @Nested
    @DisplayName("percentEncode 方法测试")
    class PercentEncodeTests {

        @Test
        @DisplayName("应该正确编码普通英文字符串")
        void shouldEncodeEnglishString() {
            // Act
            String result = FileUtils.percentEncode("test.txt");

            // Assert
            assertThat(result).isEqualTo("test.txt");
        }

        @Test
        @DisplayName("应该正确编码中文字符串")
        void shouldEncodeChineseString() {
            // Act
            String result = FileUtils.percentEncode("测试文件.txt");

            // Assert
            assertThat(result)
                    .contains("%E6%B5%8B%E8%AF%95%E6%96%87%E4%BB%B6")
                    .doesNotContain("测试文件");
        }

        @Test
        @DisplayName("应该将空格编码为 %20 而不是 +")
        void shouldEncodeSpaceAsPercent20() {
            // Act
            String result = FileUtils.percentEncode("test file.txt");

            // Assert
            assertThat(result).isEqualTo("test%20file.txt").doesNotContain("+");
        }

        @ParameterizedTest
        @CsvSource({
            "文档.docx, %E6%96%87%E6%A1%A3.docx",
            "报告 2024.pdf, %E6%8A%A5%E5%91%8A%202024.pdf",
            "图片(1).jpg, %E5%9B%BE%E7%89%87%281%29.jpg"
        })
        @DisplayName("应该正确编码包含特殊字符的文件名")
        void shouldEncodeSpecialCharacters(String input, String expected) {
            // Act
            String result = FileUtils.percentEncode(input);

            // Assert
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("应该正确编码包含多个空格的字符串")
        void shouldEncodeMultipleSpaces() {
            // Act
            String result = FileUtils.percentEncode("test   file   name.txt");

            // Assert
            assertThat(result).isEqualTo("test%20%20%20file%20%20%20name.txt");
        }

        @Test
        @DisplayName("应该正确编码特殊符号")
        void shouldEncodeSpecialSymbols() {
            // Act
            String result = FileUtils.percentEncode("file&name=test.txt");

            // Assert
            assertThat(result)
                    .contains("%26") // &
                    .contains("%3D"); // =
        }

        @Test
        @DisplayName("应该正确编码带括号的文件名")
        void shouldEncodeParentheses() {
            // Act
            String result = FileUtils.percentEncode("file(2023).txt");

            // Assert
            assertThat(result).isEqualTo("file%282023%29.txt");
        }

        @Test
        @DisplayName("应该处理空字符串")
        void shouldHandleEmptyString() {
            // Act
            String result = FileUtils.percentEncode("");

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null输入应该抛出NullPointerException")
        void shouldThrowNullPointerExceptionForNullInput() {
            // Act & Assert
            assertThatThrownBy(() -> FileUtils.percentEncode(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("应该正确编码包含换行符的字符串")
        void shouldEncodeNewLine() {
            // Act
            String result = FileUtils.percentEncode("line1\nline2");

            // Assert
            assertThat(result).contains("%0A");
        }

        @Test
        @DisplayName("应该正确编码包含制表符的字符串")
        void shouldEncodeTab() {
            // Act
            String result = FileUtils.percentEncode("col1\tcol2");

            // Assert
            assertThat(result).contains("%09");
        }

        @Test
        @DisplayName("应该正确编码 Unicode 字符")
        void shouldEncodeUnicodeCharacters() {
            // Act
            String result = FileUtils.percentEncode("emoji😀.txt");

            // Assert
            assertThat(result).startsWith("emoji").contains("%F0%9F%98%80").endsWith(".txt");
        }

        @Test
        @DisplayName("应该正确编码长文件名")
        void shouldEncodeLongFileName() {
            // Arrange
            String longName = "这是一个非常长的文件名包含很多中文字符用于测试百分号编码功能的正确性.txt";

            // Act
            String result = FileUtils.percentEncode(longName);

            // Assert
            assertThat(result)
                    .isNotEmpty()
                    .startsWith("%")
                    .endsWith(".txt")
                    .hasSizeGreaterThan(longName.length());
        }
    }

    @Nested
    @DisplayName("setAttachmentResponseHeader 方法测试")
    class SetAttachmentResponseHeaderTests {

        @Test
        @DisplayName("应该设置正确的 Content-Disposition 响应头")
        void shouldSetCorrectContentDispositionHeader() {
            // Arrange
            HttpServletResponse response = mock(HttpServletResponse.class);
            String fileName = "test.txt";

            // Act
            FileUtils.setAttachmentResponseHeader(response, fileName);

            // Assert
            ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

            // setHeader 被调用两次：一次是 Content-disposition，一次是 download-filename
            verify(response, times(2)).setHeader(headerCaptor.capture(), valueCaptor.capture());

            // 获取所有捕获的值
            var headers = headerCaptor.getAllValues();
            var values = valueCaptor.getAllValues();

            // 验证 Content-disposition 头
            int contentDispIndex = headers.indexOf("Content-disposition");
            assertThat(contentDispIndex).isNotNegative();
            assertThat(values.get(contentDispIndex))
                    .startsWith("attachment; filename=")
                    .contains("filename*=utf-8''");
        }

        @Test
        @DisplayName("应该为中文文件名设置正确的编码")
        void shouldSetCorrectEncodingForChineseFileName() {
            // Arrange
            HttpServletResponse response = mock(HttpServletResponse.class);
            String fileName = "测试文件.txt";

            // Act
            FileUtils.setAttachmentResponseHeader(response, fileName);

            // Assert
            ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
            verify(response).setHeader(eq("Content-disposition"), valueCaptor.capture());

            String contentDisposition = valueCaptor.getValue();
            assertThat(contentDisposition)
                    .contains("%E6%B5%8B%E8%AF%95%E6%96%87%E4%BB%B6.txt")
                    .contains("filename*=utf-8''");
        }

        @Test
        @DisplayName("应该设置 Access-Control-Expose-Headers 响应头")
        void shouldSetAccessControlExposeHeaders() {
            // Arrange
            HttpServletResponse response = mock(HttpServletResponse.class);
            String fileName = "test.txt";

            // Act
            FileUtils.setAttachmentResponseHeader(response, fileName);

            // Assert
            verify(response)
                    .addHeader(
                            "Access-Control-Expose-Headers",
                            "Content-Disposition,download-filename");
        }

        @Test
        @DisplayName("应该设置 download-filename 响应头")
        void shouldSetDownloadFileNameHeader() {
            // Arrange
            HttpServletResponse response = mock(HttpServletResponse.class);
            String fileName = "report.pdf";

            // Act
            FileUtils.setAttachmentResponseHeader(response, fileName);

            // Assert
            verify(response).setHeader(eq("download-filename"), eq("report.pdf"));
        }

        @Test
        @DisplayName("应该为带空格的文件名设置正确的编码")
        void shouldSetCorrectEncodingForFileNameWithSpaces() {
            // Arrange
            HttpServletResponse response = mock(HttpServletResponse.class);
            String fileName = "my file.txt";

            // Act
            FileUtils.setAttachmentResponseHeader(response, fileName);

            // Assert
            ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
            verify(response).setHeader(eq("download-filename"), valueCaptor.capture());

            assertThat(valueCaptor.getValue())
                    .isEqualTo("my%20file.txt")
                    .doesNotContain(" ")
                    .doesNotContain("+");
        }

        @Test
        @DisplayName("应该验证所有响应头都被设置")
        void shouldVerifyAllHeadersAreSet() {
            // Arrange
            HttpServletResponse response = mock(HttpServletResponse.class);
            String fileName = "document.docx";

            // Act
            FileUtils.setAttachmentResponseHeader(response, fileName);

            // Assert
            verify(response, times(1)).addHeader(anyString(), anyString());
            verify(response, times(2)).setHeader(anyString(), anyString());
            verifyNoMoreInteractions(response);
        }

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "测试.txt",
                    "report 2024.pdf",
                    "image(1).jpg",
                    "文档 (副本).docx",
                    "data&file.csv"
                })
        @DisplayName("应该正确处理各种文件名格式")
        void shouldHandleVariousFileNameFormats(String fileName) {
            // Arrange
            HttpServletResponse response = mock(HttpServletResponse.class);

            // Act
            FileUtils.setAttachmentResponseHeader(response, fileName);

            // Assert
            verify(response).addHeader(eq("Access-Control-Expose-Headers"), anyString());
            verify(response).setHeader(eq("Content-disposition"), anyString());
            verify(response).setHeader(eq("download-filename"), anyString());
        }

        @Test
        @DisplayName("应该为 Content-Disposition 设置完整的RFC 5987格式")
        void shouldSetRFC5987CompliantContentDisposition() {
            // Arrange
            HttpServletResponse response = mock(HttpServletResponse.class);
            String fileName = "文件.txt";

            // Act
            FileUtils.setAttachmentResponseHeader(response, fileName);

            // Assert
            ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
            verify(response).setHeader(eq("Content-disposition"), valueCaptor.capture());

            String contentDisposition = valueCaptor.getValue();
            // RFC 5987 格式: attachment; filename=xxx;filename*=utf-8''xxx
            assertThat(contentDisposition)
                    .matches("attachment; filename=[^;]+;filename\\*=utf-8''.*");
        }

        @Test
        @DisplayName("应该处理特殊字符 & 和 = 的编码")
        void shouldHandleSpecialCharactersInFileName() {
            // Arrange
            HttpServletResponse response = mock(HttpServletResponse.class);
            String fileName = "data&params=test.txt";

            // Act
            FileUtils.setAttachmentResponseHeader(response, fileName);

            // Assert
            ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
            verify(response).setHeader(eq("download-filename"), valueCaptor.capture());

            assertThat(valueCaptor.getValue())
                    .contains("%26") // &
                    .contains("%3D"); // =
        }
    }
}
