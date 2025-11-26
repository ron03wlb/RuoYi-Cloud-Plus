package org.dromara.common.core.utils.file;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * MimeTypeUtils 测试类
 *
 * @author Test Team
 */
@DisplayName("MimeTypeUtils 测试")
class MimeTypeUtilsTest {

    @Nested
    @DisplayName("图片 MIME 类型常量测试")
    class ImageMimeTypeTest {

        @Test
        @DisplayName("应该正确定义 PNG MIME 类型")
        void shouldHaveCorrectPngMimeType() {
            assertThat(MimeTypeUtils.IMAGE_PNG).isEqualTo("image/png");
        }

        @Test
        @DisplayName("应该正确定义 JPG MIME 类型")
        void shouldHaveCorrectJpgMimeType() {
            assertThat(MimeTypeUtils.IMAGE_JPG).isEqualTo("image/jpg");
        }

        @Test
        @DisplayName("应该正确定义 JPEG MIME 类型")
        void shouldHaveCorrectJpegMimeType() {
            assertThat(MimeTypeUtils.IMAGE_JPEG).isEqualTo("image/jpeg");
        }

        @Test
        @DisplayName("应该正确定义 BMP MIME 类型")
        void shouldHaveCorrectBmpMimeType() {
            assertThat(MimeTypeUtils.IMAGE_BMP).isEqualTo("image/bmp");
        }

        @Test
        @DisplayName("应该正确定义 GIF MIME 类型")
        void shouldHaveCorrectGifMimeType() {
            assertThat(MimeTypeUtils.IMAGE_GIF).isEqualTo("image/gif");
        }
    }

    @Nested
    @DisplayName("文件扩展名数组常量测试")
    class FileExtensionArrayTest {

        @Test
        @DisplayName("应该包含所有图片扩展名")
        void shouldHaveAllImageExtensions() {
            assertThat(MimeTypeUtils.IMAGE_EXTENSION)
                    .isNotNull()
                    .hasSize(5)
                    .containsExactly("bmp", "gif", "jpg", "jpeg", "png");
        }

        @Test
        @DisplayName("应该包含所有 Flash 扩展名")
        void shouldHaveAllFlashExtensions() {
            assertThat(MimeTypeUtils.FLASH_EXTENSION)
                    .isNotNull()
                    .hasSize(2)
                    .containsExactly("swf", "flv");
        }

        @Test
        @DisplayName("应该包含所有媒体扩展名")
        void shouldHaveAllMediaExtensions() {
            assertThat(MimeTypeUtils.MEDIA_EXTENSION)
                    .isNotNull()
                    .hasSize(12)
                    .containsExactly(
                            "swf", "flv", "mp3", "wav", "wma", "wmv", "mid", "avi", "mpg", "asf",
                            "rm", "rmvb");
        }

        @Test
        @DisplayName("应该包含所有视频扩展名")
        void shouldHaveAllVideoExtensions() {
            assertThat(MimeTypeUtils.VIDEO_EXTENSION)
                    .isNotNull()
                    .hasSize(3)
                    .containsExactly("mp4", "avi", "rmvb");
        }

        @Test
        @DisplayName("应该包含所有默认允许的扩展名")
        void shouldHaveAllDefaultAllowedExtensions() {
            assertThat(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION)
                    .isNotNull()
                    .hasSize(22)
                    .contains(
                            // 图片
                            "bmp",
                            "gif",
                            "jpg",
                            "jpeg",
                            "png",
                            // Office文档
                            "doc",
                            "docx",
                            "xls",
                            "xlsx",
                            "ppt",
                            "pptx",
                            "html",
                            "htm",
                            "txt",
                            // 压缩文件
                            "rar",
                            "zip",
                            "gz",
                            "bz2",
                            // 视频
                            "mp4",
                            "avi",
                            "rmvb",
                            // PDF
                            "pdf");
        }
    }

    @Nested
    @DisplayName("扩展名分类验证测试")
    class ExtensionCategoryTest {

        @Test
        @DisplayName("IMAGE_EXTENSION 应该只包含图片格式")
        void shouldImageExtensionContainOnlyImageFormats() {
            assertThat(MimeTypeUtils.IMAGE_EXTENSION)
                    .allMatch(ext -> ext.matches("bmp|gif|jpg|jpeg|png"));
        }

        @Test
        @DisplayName("VIDEO_EXTENSION 应该只包含视频格式")
        void shouldVideoExtensionContainOnlyVideoFormats() {
            assertThat(MimeTypeUtils.VIDEO_EXTENSION).allMatch(ext -> ext.matches("mp4|avi|rmvb"));
        }

        @Test
        @DisplayName("MEDIA_EXTENSION 应该包含音频和视频格式")
        void shouldMediaExtensionContainAudioAndVideoFormats() {
            assertThat(MimeTypeUtils.MEDIA_EXTENSION)
                    .contains("mp3", "wav", "wma") // 音频
                    .contains("avi", "mpg", "rmvb"); // 视频
        }

        @Test
        @DisplayName("DEFAULT_ALLOWED_EXTENSION 应该包含图片、文档、压缩、视频和PDF")
        void shouldDefaultAllowedExtensionIncludeAllCategories() {
            String[] extensions = MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION;

            // 包含图片格式
            assertThat(extensions).contains("jpg", "png", "gif");

            // 包含文档格式
            assertThat(extensions).contains("doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt");

            // 包含压缩格式
            assertThat(extensions).contains("rar", "zip", "gz", "bz2");

            // 包含视频格式
            assertThat(extensions).contains("mp4", "avi", "rmvb");

            // 包含PDF
            assertThat(extensions).contains("pdf");
        }
    }

    @Nested
    @DisplayName("扩展名覆盖范围测试")
    class ExtensionCoverageTest {

        @Test
        @DisplayName("IMAGE_EXTENSION 的所有扩展名都应该在 DEFAULT_ALLOWED_EXTENSION 中")
        void shouldAllImageExtensionsBeInDefaultAllowed() {
            assertThat(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION)
                    .contains(MimeTypeUtils.IMAGE_EXTENSION);
        }

        @Test
        @DisplayName("VIDEO_EXTENSION 的所有扩展名都应该在 DEFAULT_ALLOWED_EXTENSION 中")
        void shouldAllVideoExtensionsBeInDefaultAllowed() {
            assertThat(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION)
                    .contains(MimeTypeUtils.VIDEO_EXTENSION);
        }

        @Test
        @DisplayName("FLASH_EXTENSION 不应该在 DEFAULT_ALLOWED_EXTENSION 中")
        void shouldFlashExtensionsNotBeInDefaultAllowed() {
            assertThat(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION)
                    .doesNotContain(MimeTypeUtils.FLASH_EXTENSION);
        }

        @Test
        @DisplayName("MEDIA_EXTENSION 只有部分扩展名在 DEFAULT_ALLOWED_EXTENSION 中")
        void shouldOnlySomeMediaExtensionsBeInDefaultAllowed() {
            // mp4, avi, rmvb 在默认允许列表中
            assertThat(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION).contains("mp4", "avi", "rmvb");

            // mp3, wav, wma 等音频格式不在默认允许列表中
            assertThat(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION).doesNotContain("mp3", "wav", "wma");
        }
    }

    @Nested
    @DisplayName("常量不变性测试")
    class ConstantImmutabilityTest {

        @Test
        @DisplayName("图片 MIME 类型常量应该不为空")
        void shouldImageMimeTypesNotBeNull() {
            assertThat(MimeTypeUtils.IMAGE_PNG).isNotNull();
            assertThat(MimeTypeUtils.IMAGE_JPG).isNotNull();
            assertThat(MimeTypeUtils.IMAGE_JPEG).isNotNull();
            assertThat(MimeTypeUtils.IMAGE_BMP).isNotNull();
            assertThat(MimeTypeUtils.IMAGE_GIF).isNotNull();
        }

        @Test
        @DisplayName("扩展名数组常量应该不为空")
        void shouldExtensionArraysNotBeNull() {
            assertThat(MimeTypeUtils.IMAGE_EXTENSION).isNotNull();
            assertThat(MimeTypeUtils.FLASH_EXTENSION).isNotNull();
            assertThat(MimeTypeUtils.MEDIA_EXTENSION).isNotNull();
            assertThat(MimeTypeUtils.VIDEO_EXTENSION).isNotNull();
            assertThat(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION).isNotNull();
        }

        @Test
        @DisplayName("扩展名数组应该不为空数组")
        void shouldExtensionArraysNotBeEmpty() {
            assertThat(MimeTypeUtils.IMAGE_EXTENSION).isNotEmpty();
            assertThat(MimeTypeUtils.FLASH_EXTENSION).isNotEmpty();
            assertThat(MimeTypeUtils.MEDIA_EXTENSION).isNotEmpty();
            assertThat(MimeTypeUtils.VIDEO_EXTENSION).isNotEmpty();
            assertThat(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION).isNotEmpty();
        }

        @Test
        @DisplayName("扩展名数组中不应该包含 null 或空字符串")
        void shouldExtensionArraysNotContainNullOrEmpty() {
            assertThat(MimeTypeUtils.IMAGE_EXTENSION)
                    .doesNotContainNull()
                    .allMatch(s -> !s.isEmpty());
            assertThat(MimeTypeUtils.FLASH_EXTENSION)
                    .doesNotContainNull()
                    .allMatch(s -> !s.isEmpty());
            assertThat(MimeTypeUtils.MEDIA_EXTENSION)
                    .doesNotContainNull()
                    .allMatch(s -> !s.isEmpty());
            assertThat(MimeTypeUtils.VIDEO_EXTENSION)
                    .doesNotContainNull()
                    .allMatch(s -> !s.isEmpty());
            assertThat(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION)
                    .doesNotContainNull()
                    .allMatch(s -> !s.isEmpty());
        }

        @Test
        @DisplayName("扩展名应该都是小写字母")
        void shouldAllExtensionsBeLowerCase() {
            assertThat(MimeTypeUtils.IMAGE_EXTENSION).allMatch(s -> s.equals(s.toLowerCase()));
            assertThat(MimeTypeUtils.FLASH_EXTENSION).allMatch(s -> s.equals(s.toLowerCase()));
            assertThat(MimeTypeUtils.MEDIA_EXTENSION).allMatch(s -> s.equals(s.toLowerCase()));
            assertThat(MimeTypeUtils.VIDEO_EXTENSION).allMatch(s -> s.equals(s.toLowerCase()));
            assertThat(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION)
                    .allMatch(s -> s.equals(s.toLowerCase()));
        }

        @Test
        @DisplayName("扩展名数组中不应该有重复元素")
        void shouldExtensionArraysNotHaveDuplicates() {
            assertThat(MimeTypeUtils.IMAGE_EXTENSION).doesNotHaveDuplicates();
            assertThat(MimeTypeUtils.FLASH_EXTENSION).doesNotHaveDuplicates();
            assertThat(MimeTypeUtils.MEDIA_EXTENSION).doesNotHaveDuplicates();
            assertThat(MimeTypeUtils.VIDEO_EXTENSION).doesNotHaveDuplicates();
            assertThat(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION).doesNotHaveDuplicates();
        }
    }

    @Nested
    @DisplayName("特定格式验证测试")
    class SpecificFormatTest {

        @Test
        @DisplayName("应该区分 JPG 和 JPEG")
        void shouldDistinguishJpgFromJpeg() {
            assertThat(MimeTypeUtils.IMAGE_JPG).isNotEqualTo(MimeTypeUtils.IMAGE_JPEG);
            assertThat(MimeTypeUtils.IMAGE_EXTENSION).contains("jpg", "jpeg");
        }

        @Test
        @DisplayName("Office 文档格式应该包含新旧两种格式")
        void shouldIncludeBothOldAndNewOfficeFormats() {
            assertThat(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION)
                    .contains("doc", "docx") // Word
                    .contains("xls", "xlsx") // Excel
                    .contains("ppt", "pptx"); // PowerPoint
        }

        @Test
        @DisplayName("压缩文件应该包含常见压缩格式")
        void shouldIncludeCommonCompressionFormats() {
            assertThat(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION).contains("rar", "zip", "gz", "bz2");
        }

        @Test
        @DisplayName("HTML 和 HTM 应该都被支持")
        void shouldSupportBothHtmlAndHtm() {
            assertThat(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION).contains("html", "htm");
        }
    }
}
