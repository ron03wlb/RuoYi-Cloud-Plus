package org.dromara.common.core.exception.file;

import static org.assertj.core.api.Assertions.*;

import java.util.Locale;
import org.dromara.common.core.BaseIntegrationTest;
import org.dromara.common.core.exception.base.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 文件异常子类集成测试.
 *
 * <p>测试文件相关异常子类的功能，包括： - FileNameLengthLimitExceededException（文件名长度超限异常） -
 * FileSizeLimitExceededException（文件大小超限异常）
 *
 * <p>这些异常类继承自 FileException，依赖 MessageUtils 进行国际化消息处理， 因此需要 Spring 容器环境进行集成测试.
 *
 * @author Test Team
 * @date 2025-10-31
 */
@DisplayName("文件异常子类集成测试")
class FileSubExceptionsIntegrationTest extends BaseIntegrationTest {

  @Nested
  @DisplayName("FileNameLengthLimitExceededException（文件名长度超限异常）测试")
  class FileNameLengthLimitExceededExceptionTests {

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTests {

      @Test
      @DisplayName("应该通过默认文件名长度限制创建异常")
      void shouldCreateWithDefaultFileNameLength() {
        // Arrange
        int defaultFileNameLength = 100;

        // Act
        FileNameLengthLimitExceededException exception =
            new FileNameLengthLimitExceededException(defaultFileNameLength);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getModule()).isEqualTo("file");
        assertThat(exception.getCode()).isEqualTo("upload.filename.exceed.length");
        assertThat(exception.getArgs()).containsExactly(100);
      }

      @Test
      @DisplayName("应该支持不同的长度限制值")
      void shouldCreateWithDifferentLengthLimits() {
        // Test different length limits
        FileNameLengthLimitExceededException exception50 =
            new FileNameLengthLimitExceededException(50);
        FileNameLengthLimitExceededException exception200 =
            new FileNameLengthLimitExceededException(200);
        FileNameLengthLimitExceededException exception255 =
            new FileNameLengthLimitExceededException(255);

        // Assert
        assertThat(exception50.getArgs()).containsExactly(50);
        assertThat(exception200.getArgs()).containsExactly(200);
        assertThat(exception255.getArgs()).containsExactly(255);
      }

      @Test
      @DisplayName("应该支持零值长度限制")
      void shouldCreateWithZeroLength() {
        // Act
        FileNameLengthLimitExceededException exception =
            new FileNameLengthLimitExceededException(0);

        // Assert
        assertThat(exception.getArgs()).containsExactly(0);
      }

      @Test
      @DisplayName("应该支持负数长度限制（边界测试）")
      void shouldCreateWithNegativeLength() {
        // Act
        FileNameLengthLimitExceededException exception =
            new FileNameLengthLimitExceededException(-1);

        // Assert
        assertThat(exception.getArgs()).containsExactly(-1);
      }
    }

    @Nested
    @DisplayName("getMessage 方法测试 - MessageUtils 集成")
    class GetMessageTests {

      @Test
      @DisplayName("应该获取文件名长度超限消息（中文）")
      void shouldGetFileNameLengthExceededMessageInChinese() {
        // Arrange
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        FileNameLengthLimitExceededException exception =
            new FileNameLengthLimitExceededException(100);

        // Act
        String message = exception.getMessage();

        // Assert
        assertThat(message).isNotBlank();
        assertThat(message).contains("100");
      }

      @Test
      @DisplayName("应该正确格式化不同长度参数的消息")
      void shouldFormatMessageWithDifferentLengths() {
        // Arrange
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        FileNameLengthLimitExceededException exception50 =
            new FileNameLengthLimitExceededException(50);
        FileNameLengthLimitExceededException exception200 =
            new FileNameLengthLimitExceededException(200);

        // Act
        String message50 = exception50.getMessage();
        String message200 = exception200.getMessage();

        // Assert
        assertThat(message50).contains("50");
        assertThat(message200).contains("200");
        assertThat(message50).isNotEqualTo(message200);
      }
    }

    @Nested
    @DisplayName("继承特性测试")
    class InheritanceTests {

      @Test
      @DisplayName("应该继承自 FileException")
      void shouldInheritFromFileException() {
        // Arrange
        FileNameLengthLimitExceededException exception =
            new FileNameLengthLimitExceededException(100);

        // Assert
        assertThat(exception).isInstanceOf(FileException.class);
      }

      @Test
      @DisplayName("应该继承自 BaseException")
      void shouldInheritFromBaseException() {
        // Arrange
        FileNameLengthLimitExceededException exception =
            new FileNameLengthLimitExceededException(100);

        // Assert
        assertThat(exception).isInstanceOf(BaseException.class);
      }

      @Test
      @DisplayName("应该继承自 RuntimeException")
      void shouldInheritFromRuntimeException() {
        // Arrange
        FileNameLengthLimitExceededException exception =
            new FileNameLengthLimitExceededException(100);

        // Assert
        assertThat(exception).isInstanceOf(RuntimeException.class);
      }
    }

    @Nested
    @DisplayName("序列化测试")
    class SerializationTests {

      @Test
      @DisplayName("应该定义 serialVersionUID")
      void shouldDefineSerialVersionUID() {
        // Assert
        assertThatCode(
                () -> {
                  java.lang.reflect.Field field =
                      FileNameLengthLimitExceededException.class.getDeclaredField(
                          "serialVersionUID");
                  field.setAccessible(true);
                  long serialVersionUID = field.getLong(null);
                  assertThat(serialVersionUID).isEqualTo(1L);
                })
            .doesNotThrowAnyException();
      }
    }

    @Nested
    @DisplayName("真实业务场景测试")
    class RealWorldScenarioTests {

      @Test
      @DisplayName("场景: 上传文件名超过系统限制（100字符）")
      void shouldHandleFileNameExceedsSystemLimit() {
        // Arrange
        int systemLimit = 100;
        String longFileName = "a".repeat(150) + ".jpg"; // 154字符

        // Act
        Throwable thrown =
            catchThrowable(
                () -> {
                  if (longFileName.length() > systemLimit) {
                    throw new FileNameLengthLimitExceededException(systemLimit);
                  }
                });

        // Assert
        assertThat(thrown)
            .isInstanceOf(FileNameLengthLimitExceededException.class)
            .hasFieldOrPropertyWithValue("code", "upload.filename.exceed.length");
      }

      @Test
      @DisplayName("场景: 文件名长度刚好等于限制（边界情况）")
      void shouldNotThrowWhenFileNameEqualsLimit() {
        // Arrange
        int systemLimit = 100;
        // 生成刚好96字符的文件名（加上.jpg后刚好100字符）
        String fileName = "a".repeat(96) + ".jpg"; // 96 + 4 = 100字符

        // Act & Assert
        assertThatCode(
                () -> {
                  if (fileName.length() <= systemLimit) {
                    // 不抛异常
                  } else {
                    throw new FileNameLengthLimitExceededException(systemLimit);
                  }
                })
            .doesNotThrowAnyException();

        // 验证文件名长度
        assertThat(fileName.length()).isEqualTo(100);
      }

      @Test
      @DisplayName("场景: 数据库字段长度限制（255字符）")
      void shouldHandleDatabaseFieldLengthLimit() {
        // Arrange
        int databaseFieldLimit = 255;

        // Act
        Throwable thrown =
            catchThrowable(
                () -> {
                  throw new FileNameLengthLimitExceededException(databaseFieldLimit);
                });

        // Assert
        assertThat(thrown).isInstanceOf(FileNameLengthLimitExceededException.class);
        assertThat(((FileNameLengthLimitExceededException) thrown).getArgs()).containsExactly(255);
      }
    }
  }

  @Nested
  @DisplayName("FileSizeLimitExceededException（文件大小超限异常）测试")
  class FileSizeLimitExceededExceptionTests {

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTests {

      @Test
      @DisplayName("应该通过默认文件大小限制创建异常")
      void shouldCreateWithDefaultMaxSize() {
        // Arrange
        long defaultMaxSize = 10485760L; // 10MB

        // Act
        FileSizeLimitExceededException exception =
            new FileSizeLimitExceededException(defaultMaxSize);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getModule()).isEqualTo("file");
        assertThat(exception.getCode()).isEqualTo("upload.exceed.maxSize");
        assertThat(exception.getArgs()).containsExactly(10485760L);
      }

      @Test
      @DisplayName("应该支持不同的大小限制值")
      void shouldCreateWithDifferentSizeLimits() {
        // Test different size limits
        FileSizeLimitExceededException exception1MB =
            new FileSizeLimitExceededException(1048576L); // 1MB
        FileSizeLimitExceededException exception50MB =
            new FileSizeLimitExceededException(52428800L); // 50MB
        FileSizeLimitExceededException exception100MB =
            new FileSizeLimitExceededException(104857600L); // 100MB

        // Assert
        assertThat(exception1MB.getArgs()).containsExactly(1048576L);
        assertThat(exception50MB.getArgs()).containsExactly(52428800L);
        assertThat(exception100MB.getArgs()).containsExactly(104857600L);
      }

      @Test
      @DisplayName("应该支持零值大小限制")
      void shouldCreateWithZeroSize() {
        // Act
        FileSizeLimitExceededException exception = new FileSizeLimitExceededException(0L);

        // Assert
        assertThat(exception.getArgs()).containsExactly(0L);
      }

      @Test
      @DisplayName("应该支持负数大小限制（边界测试）")
      void shouldCreateWithNegativeSize() {
        // Act
        FileSizeLimitExceededException exception = new FileSizeLimitExceededException(-1L);

        // Assert
        assertThat(exception.getArgs()).containsExactly(-1L);
      }
    }

    @Nested
    @DisplayName("getMessage 方法测试 - MessageUtils 集成")
    class GetMessageTests {

      @Test
      @DisplayName("应该获取文件大小超限消息（中文）")
      void shouldGetFileSizeExceededMessageInChinese() {
        // Arrange
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        FileSizeLimitExceededException exception = new FileSizeLimitExceededException(10485760L);

        // Act
        String message = exception.getMessage();

        // Assert - 数字可能被格式化为带千位分隔符
        assertThat(message).isNotBlank();
        assertThat(message).containsAnyOf("10485760", "10,485,760");
      }

      @Test
      @DisplayName("应该正确格式化不同大小参数的消息")
      void shouldFormatMessageWithDifferentSizes() {
        // Arrange
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        FileSizeLimitExceededException exception1MB = new FileSizeLimitExceededException(1048576L);
        FileSizeLimitExceededException exception100MB =
            new FileSizeLimitExceededException(104857600L);

        // Act
        String message1MB = exception1MB.getMessage();
        String message100MB = exception100MB.getMessage();

        // Assert - 数字可能被格式化为带千位分隔符
        assertThat(message1MB).containsAnyOf("1048576", "1,048,576");
        assertThat(message100MB).containsAnyOf("104857600", "104,857,600");
        assertThat(message1MB).isNotEqualTo(message100MB);
      }
    }

    @Nested
    @DisplayName("继承特性测试")
    class InheritanceTests {

      @Test
      @DisplayName("应该继承自 FileException")
      void shouldInheritFromFileException() {
        // Arrange
        FileSizeLimitExceededException exception = new FileSizeLimitExceededException(10485760L);

        // Assert
        assertThat(exception).isInstanceOf(FileException.class);
      }

      @Test
      @DisplayName("应该继承自 BaseException")
      void shouldInheritFromBaseException() {
        // Arrange
        FileSizeLimitExceededException exception = new FileSizeLimitExceededException(10485760L);

        // Assert
        assertThat(exception).isInstanceOf(BaseException.class);
      }

      @Test
      @DisplayName("应该继承自 RuntimeException")
      void shouldInheritFromRuntimeException() {
        // Arrange
        FileSizeLimitExceededException exception = new FileSizeLimitExceededException(10485760L);

        // Assert
        assertThat(exception).isInstanceOf(RuntimeException.class);
      }
    }

    @Nested
    @DisplayName("序列化测试")
    class SerializationTests {

      @Test
      @DisplayName("应该定义 serialVersionUID")
      void shouldDefineSerialVersionUID() {
        // Assert
        assertThatCode(
                () -> {
                  java.lang.reflect.Field field =
                      FileSizeLimitExceededException.class.getDeclaredField("serialVersionUID");
                  field.setAccessible(true);
                  long serialVersionUID = field.getLong(null);
                  assertThat(serialVersionUID).isEqualTo(1L);
                })
            .doesNotThrowAnyException();
      }
    }

    @Nested
    @DisplayName("真实业务场景测试")
    class RealWorldScenarioTests {

      @Test
      @DisplayName("场景: 上传图片超过10MB限制")
      void shouldHandleImageExceedsSizeLimit() {
        // Arrange
        long maxImageSize = 10 * 1024 * 1024L; // 10MB
        long actualImageSize = 15 * 1024 * 1024L; // 15MB

        // Act
        Throwable thrown =
            catchThrowable(
                () -> {
                  if (actualImageSize > maxImageSize) {
                    throw new FileSizeLimitExceededException(maxImageSize);
                  }
                });

        // Assert
        assertThat(thrown)
            .isInstanceOf(FileSizeLimitExceededException.class)
            .hasFieldOrPropertyWithValue("code", "upload.exceed.maxSize");
      }

      @Test
      @DisplayName("场景: 上传视频超过100MB限制")
      void shouldHandleVideoExceedsSizeLimit() {
        // Arrange
        long maxVideoSize = 100 * 1024 * 1024L; // 100MB

        // Act
        Throwable thrown =
            catchThrowable(
                () -> {
                  throw new FileSizeLimitExceededException(maxVideoSize);
                });

        // Assert
        assertThat(thrown).isInstanceOf(FileSizeLimitExceededException.class);
        assertThat(((FileSizeLimitExceededException) thrown).getArgs())
            .containsExactly(100 * 1024 * 1024L);
      }

      @Test
      @DisplayName("场景: 文件大小刚好等于限制（边界情况）")
      void shouldNotThrowWhenFileSizeEqualsLimit() {
        // Arrange
        long maxSize = 10 * 1024 * 1024L; // 10MB
        long fileSize = 10 * 1024 * 1024L; // 刚好10MB

        // Act & Assert
        assertThatCode(
                () -> {
                  if (fileSize <= maxSize) {
                    // 不抛异常
                  } else {
                    throw new FileSizeLimitExceededException(maxSize);
                  }
                })
            .doesNotThrowAnyException();
      }

      @Test
      @DisplayName("场景: 不同文件类型有不同的大小限制")
      void shouldHandleDifferentSizeLimitsForDifferentFileTypes() {
        // Arrange
        long imageSizeLimit = 10 * 1024 * 1024L; // 10MB
        long videoSizeLimit = 100 * 1024 * 1024L; // 100MB
        long documentSizeLimit = 50 * 1024 * 1024L; // 50MB

        // Act
        FileSizeLimitExceededException imageException =
            new FileSizeLimitExceededException(imageSizeLimit);
        FileSizeLimitExceededException videoException =
            new FileSizeLimitExceededException(videoSizeLimit);
        FileSizeLimitExceededException documentException =
            new FileSizeLimitExceededException(documentSizeLimit);

        // Assert
        assertThat(imageException.getArgs()).containsExactly(imageSizeLimit);
        assertThat(videoException.getArgs()).containsExactly(videoSizeLimit);
        assertThat(documentException.getArgs()).containsExactly(documentSizeLimit);
      }
    }
  }

  @Nested
  @DisplayName("两种异常的区别测试")
  class DifferenceBetweenExceptionsTests {

    @Test
    @DisplayName("应该有不同的错误码")
    void shouldHaveDifferentErrorCodes() {
      // Arrange
      FileNameLengthLimitExceededException nameException =
          new FileNameLengthLimitExceededException(100);
      FileSizeLimitExceededException sizeException = new FileSizeLimitExceededException(10485760L);

      // Assert
      assertThat(nameException.getCode()).isEqualTo("upload.filename.exceed.length");
      assertThat(sizeException.getCode()).isEqualTo("upload.exceed.maxSize");
      assertThat(nameException.getCode()).isNotEqualTo(sizeException.getCode());
    }

    @Test
    @DisplayName("应该有不同的参数类型")
    void shouldHaveDifferentParameterTypes() {
      // Arrange
      FileNameLengthLimitExceededException nameException =
          new FileNameLengthLimitExceededException(100);
      FileSizeLimitExceededException sizeException = new FileSizeLimitExceededException(10485760L);

      // Assert
      // FileNameLengthLimitExceededException 使用 int
      assertThat(nameException.getArgs()[0]).isInstanceOf(Integer.class);
      // FileSizeLimitExceededException 使用 long
      assertThat(sizeException.getArgs()[0]).isInstanceOf(Long.class);
    }

    @Test
    @DisplayName("两者都应该继承自 FileException")
    void shouldBothInheritFromFileException() {
      // Arrange
      FileNameLengthLimitExceededException nameException =
          new FileNameLengthLimitExceededException(100);
      FileSizeLimitExceededException sizeException = new FileSizeLimitExceededException(10485760L);

      // Assert
      assertThat(nameException).isInstanceOf(FileException.class);
      assertThat(sizeException).isInstanceOf(FileException.class);
    }
  }

  @Nested
  @DisplayName("异常抛出和捕获测试")
  class ThrowAndCatchTests {

    @Test
    @DisplayName("应该能正确抛出和捕获 FileNameLengthLimitExceededException")
    void shouldThrowAndCatchFileNameLengthException() {
      // Assert
      assertThatThrownBy(
              () -> {
                throw new FileNameLengthLimitExceededException(100);
              })
          .isInstanceOf(FileNameLengthLimitExceededException.class)
          .isInstanceOf(FileException.class)
          .hasFieldOrPropertyWithValue("code", "upload.filename.exceed.length");
    }

    @Test
    @DisplayName("应该能正确抛出和捕获 FileSizeLimitExceededException")
    void shouldThrowAndCatchFileSizeException() {
      // Assert
      assertThatThrownBy(
              () -> {
                throw new FileSizeLimitExceededException(10485760L);
              })
          .isInstanceOf(FileSizeLimitExceededException.class)
          .isInstanceOf(FileException.class)
          .hasFieldOrPropertyWithValue("code", "upload.exceed.maxSize");
    }

    @Test
    @DisplayName("应该能通过 FileException 类型捕获文件异常")
    void shouldCatchAsFileException() {
      // Arrange & Act
      Throwable nameException =
          catchThrowable(
              () -> {
                throw new FileNameLengthLimitExceededException(100);
              });

      Throwable sizeException =
          catchThrowable(
              () -> {
                throw new FileSizeLimitExceededException(10485760L);
              });

      // Assert
      assertThat(nameException).isInstanceOf(FileException.class);
      assertThat(sizeException).isInstanceOf(FileException.class);
    }

    @Test
    @DisplayName("应该能通过 RuntimeException 类型捕获文件异常")
    void shouldCatchAsRuntimeException() {
      // Arrange & Act
      Throwable nameException =
          catchThrowable(
              () -> {
                throw new FileNameLengthLimitExceededException(100);
              });

      Throwable sizeException =
          catchThrowable(
              () -> {
                throw new FileSizeLimitExceededException(10485760L);
              });

      // Assert
      assertThat(nameException).isInstanceOf(RuntimeException.class);
      assertThat(sizeException).isInstanceOf(RuntimeException.class);
    }
  }
}
