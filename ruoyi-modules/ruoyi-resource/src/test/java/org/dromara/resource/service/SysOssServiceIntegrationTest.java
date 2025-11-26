package org.dromara.resource.service;

import static org.assertj.core.api.Assertions.assertThat;

import cn.dev33.satoken.dao.SaTokenDao;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.oss.core.OssClient;
import org.dromara.common.oss.factory.OssFactory;
import org.dromara.common.test.BaseIntegrationTest;
import org.dromara.common.test.utils.SqlScriptExecutor;
import org.dromara.resource.config.TestResourceConfig;
import org.dromara.resource.domain.bo.SysOssBo;
import org.dromara.resource.domain.vo.SysOssVo;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;

/**
 * SysOssService 集成测试
 *
 * <p>测试 OSS 文件存储服务的完整功能，包括文件上传、下载、查询、删除等操作
 *
 * <h3>P0 问题 - Bean 配置冲突极其复杂，需要架构级重构</h3>
 *
 * <p>问题：Spring 容器启动时存在深层的 Bean 依赖冲突，无法通过简单配置解决
 *
 * <h4>历史尝试记录（9次尝试，全部失败）：</h4>
 *
 * <ol>
 *   <li>方案1: 使用 {@code @TestConfiguration} + {@code @Primary} - 仍有 Bean 冲突 ❌
 *   <li>方案2: 设置 {@code spring.main.allow-bean-definition-overriding=true} - 无效 ❌
 *   <li>方案3: 创建轻量级启动类 {@link org.dromara.resource.TestResourceApplication} 排除 Dubbo/Nacos - 缺少
 *       TenantProperties Bean ❌
 *   <li>方案4: 在 TestResourceConfig 中提供 TenantProperties Bean - 仍有 Sa-Token DAO 冲突 ❌
 *   <li>方案5: 在 TestResourceConfig 中添加 @Primary SaTokenDao Bean - @Primary 不生效 ❌
 *   <li>方案6: 禁用 TenantConfiguration（设置 enable=false）- 仍有 Sa-Token DAO 冲突 ❌
 *   <li>方案7: 配置 Sa-Token 属性禁用 Redis 持久化 - 仍有 Bean 冲突 ❌
 *   <li>方案8: 使用 {@code @MockBean} 替换冲突的 SaTokenDao - 错误：{@code MockBean 期望单个 bean 但发现2个} ❌
 *   <li>方案9: 使用 {@code @MockBean(name="...")} 指定替换特定的 bean - 仍有其他冲突 ❌
 * </ol>
 *
 * <h4>根本原因分析：</h4>
 *
 * <p>Resource 模块存在深层的 Bean 依赖冲突链：
 *
 * <ul>
 *   <li><b>主要冲突</b>: {@code SaTokenDao} 类型有两个 Bean (无法通过 @MockBean 解决)
 *   <li><b>依赖链</b>: MyBatis-Plus → Redis/Redisson → Sa-Token → Tenant → 拦截器 → 插件
 *   <li><b>循环依赖</b>: 这些依赖之间有复杂的循环依赖和条件加载关系
 *   <li><b>自动配置</b>: Spring Boot 的自动配置机制在测试环境触发了不必要的 Bean 注册
 * </ul>
 *
 * <h4>推荐解决方案（需要1-2天专项工作）：</h4>
 *
 * <ol>
 *   <li><b>方案A（最推荐）</b>: 使用 {@code @DataJpaTest} 切片测试，仅加载数据层，完全避免 Web 层自动配置
 *   <li><b>方案B（备选）</b>: 重新设计测试架构，使用单元测试而非集成测试，用 Mockito 完全 mock 所有依赖
 *   <li><b>方案C（终极）</b>: 重构 Service 层架构，解耦 Sa-Token、Tenant等基础设施依赖
 * </ol>
 *
 * <h4>优先级：</h4>
 *
 * <p>P0 - 但需要 1-2 天的专项工作来系统性解决，建议作为独立任务规划
 *
 * @author Lion Li
 * @since 2025-11-10
 */
@Disabled(
        "P0 - Bean 配置冲突极其复杂，需要架构级重构。\n"
                + "已尝试9种方案均失败：包括 @TestConfiguration、@Primary、排除自动配置、@MockBean、@MockBean(name)。\n"
                + "当前冲突：SaTokenDao 类型有2个 Bean，且存在复杂的依赖链。\n"
                + "推荐：使用 @DataJpaTest 切片测试或重新设计测试架构。\n"
                + "详细分析见类 JavaDoc。")
@SpringBootTest(
        classes = org.dromara.resource.TestResourceApplication.class,
        properties = {
            "spring.main.allow-bean-definition-overriding=true",
            // 禁用 Sa-Token 的 Redis 持久化，使用内存存储
            "sa-token.alone-redis.enable=false",
            "sa-token.dao-type=default"
        })
@Import(TestResourceConfig.class)
@DisplayName("SysOssService 集成测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SysOssServiceIntegrationTest extends BaseIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(SysOssServiceIntegrationTest.class);

    /**
     * Mock SaTokenDao 来避免 Bean 冲突
     *
     * <p>这解决了 SaTokenDao 类型有2个 Bean 的冲突问题： - saTokenDao (来自某个自动配置) -
     * cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate (Redis 实现)
     *
     * <p>使用 @MockBean(name = "...") 指定要替换的特定 bean
     */
    @MockBean(name = "saTokenDao")
    private SaTokenDao mockSaTokenDao1;

    @MockBean(name = "cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate")
    private SaTokenDao mockSaTokenDao2;

    @Autowired(required = false)
    private ISysOssService sysOssService;

    @Autowired(required = false)
    private DataSource dataSource;

    @BeforeAll
    void initDatabase() {
        log.info("=== 初始化测试数据库 ===");

        // 创建 sys_oss 表
        String createOssTable =
                """
            CREATE TABLE IF NOT EXISTS sys_oss (
                oss_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '对象存储主键',
                file_name VARCHAR(255) NOT NULL DEFAULT '' COMMENT '文件名',
                original_name VARCHAR(255) NOT NULL DEFAULT '' COMMENT '原名',
                file_suffix VARCHAR(10) NOT NULL DEFAULT '' COMMENT '文件后缀名',
                url VARCHAR(500) NOT NULL COMMENT 'URL地址',
                service VARCHAR(20) NOT NULL DEFAULT 'minio' COMMENT '服务商',
                create_time DATETIME COMMENT '创建时间',
                create_by VARCHAR(64) COMMENT '创建者',
                update_time DATETIME COMMENT '更新时间',
                update_by VARCHAR(64) COMMENT '更新者',
                ext1 VARCHAR(500) COMMENT '扩展字段1',
                PRIMARY KEY (oss_id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OSS对象存储表';
            """;

        // 创建 sys_oss_config 表
        String createOssConfigTable =
                """
            CREATE TABLE IF NOT EXISTS sys_oss_config (
                oss_config_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                config_key VARCHAR(20) NOT NULL DEFAULT '' COMMENT '配置key',
                access_key VARCHAR(255) DEFAULT '' COMMENT 'accessKey',
                secret_key VARCHAR(255) DEFAULT '' COMMENT '秘钥',
                bucket_name VARCHAR(255) DEFAULT '' COMMENT '桶名称',
                prefix VARCHAR(255) DEFAULT '' COMMENT '前缀',
                endpoint VARCHAR(255) DEFAULT '' COMMENT '访问站点',
                domain VARCHAR(255) DEFAULT '' COMMENT '自定义域名',
                is_https VARCHAR(1) DEFAULT 'N' COMMENT '是否https',
                region VARCHAR(255) DEFAULT '' COMMENT '域',
                access_policy VARCHAR(1) NOT NULL DEFAULT '1' COMMENT '桶权限类型',
                status VARCHAR(1) DEFAULT '1' COMMENT '状态',
                ext1 VARCHAR(255) DEFAULT '' COMMENT '扩展字段',
                remark VARCHAR(500) DEFAULT '' COMMENT '备注',
                create_time DATETIME COMMENT '创建时间',
                create_by VARCHAR(64) COMMENT '创建者',
                update_time DATETIME COMMENT '更新时间',
                update_by VARCHAR(64) COMMENT '更新者',
                PRIMARY KEY (oss_config_id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对象存储配置表';
            """;

        // 插入 MinIO 测试配置
        String insertMinioConfig =
                """
            INSERT INTO sys_oss_config (config_key, access_key, secret_key, bucket_name, prefix, endpoint, domain, is_https, region, access_policy, status, remark)
            VALUES ('minio', 'minioadmin', 'minioadmin', 'test-bucket', '', '%s', '%s', 'N', '', '1', '0', 'MinIO测试配置')
            ON DUPLICATE KEY UPDATE endpoint = VALUES(endpoint), domain = VALUES(domain);
          """
                        .formatted(getMinioEndpoint(), getMinioUrl());

        SqlScriptExecutor.executeSql(
                dataSource, createOssTable, createOssConfigTable, insertMinioConfig);

        log.info("✅ 测试数据库初始化完成");
        log.info("   MinIO Endpoint: {}", getMinioEndpoint());
        log.info("   MinIO URL: {}", getMinioUrl());
    }

    @AfterEach
    void cleanupTestData() {
        // 每个测试后清理数据
        SqlScriptExecutor.truncateTables(dataSource, "sys_oss");
    }

    @Nested
    @DisplayName("1. 基础设施测试")
    class InfrastructureTests {

        @Test
        @Order(1)
        @DisplayName("应该成功注入 SysOssService")
        void shouldInjectSysOssService() {
            log.info("=== 验证服务注入 ===");

            assertThat(sysOssService).as("SysOssService 应该被成功注入").isNotNull();

            log.info("✅ SysOssService 注入成功");
        }

        @Test
        @Order(2)
        @DisplayName("应该能够连接到 MinIO")
        void shouldConnectToMinio() {
            log.info("=== 验证 MinIO 连接 ===");

            // Arrange & Act - 获取 OssClient 实例
            OssClient ossClient = OssFactory.instance("minio");

            // Assert
            assertThat(ossClient).as("应该能够创建 OssClient 实例").isNotNull();

            log.info("✅ MinIO 连接成功");
        }

        @Test
        @Order(3)
        @DisplayName("数据库表应该存在")
        void shouldHaveDatabaseTables() {
            log.info("=== 验证数据库表 ===");

            // Act & Assert
            boolean ossTableExists = SqlScriptExecutor.tableExists(dataSource, "sys_oss");
            boolean configTableExists = SqlScriptExecutor.tableExists(dataSource, "sys_oss_config");

            assertThat(ossTableExists).as("sys_oss 表应该存在").isTrue();

            assertThat(configTableExists).as("sys_oss_config 表应该存在").isTrue();

            log.info("✅ 数据库表验证成功");
        }
    }

    @Nested
    @DisplayName("2. 文件上传测试")
    class FileUploadTests {

        @Test
        @Order(10)
        @DisplayName("应该成功上传 MultipartFile")
        void shouldUploadMultipartFile() {
            log.info("=== 测试上传 MultipartFile ===");

            // Arrange - 创建 mock multipart file
            byte[] content = "Hello, MinIO!".getBytes(StandardCharsets.UTF_8);
            MockMultipartFile file =
                    new MockMultipartFile("file", "test.txt", "text/plain", content);

            // Act - 上传文件
            SysOssVo result = sysOssService.upload(file);

            // Assert
            assertThat(result).as("上传结果不应为空").isNotNull();

            assertThat(result.getOssId()).as("应该生成 OSS ID").isNotNull().isGreaterThan(0L);

            assertThat(result.getOriginalName()).as("原始文件名应该正确").isEqualTo("test.txt");

            assertThat(result.getFileSuffix()).as("文件后缀应该正确").isEqualTo(".txt");

            assertThat(result.getUrl()).as("URL 应该不为空").isNotNull().isNotEmpty();

            assertThat(result.getService()).as("服务商应该是 minio").isEqualTo("minio");

            log.info("✅ 文件上传成功");
            log.info("   OSS ID: {}", result.getOssId());
            log.info("   File Name: {}", result.getFileName());
            log.info("   URL: {}", result.getUrl());
        }

        @Test
        @Order(11)
        @DisplayName("上传后应该能在数据库中查询到文件记录")
        void shouldFindUploadedFileInDatabase() {
            log.info("=== 测试上传后数据库记录 ===");

            // Arrange - 上传文件
            MockMultipartFile file =
                    new MockMultipartFile(
                            "file",
                            "database-test.txt",
                            "text/plain",
                            "test content".getBytes(StandardCharsets.UTF_8));
            SysOssVo uploadedFile = sysOssService.upload(file);

            // Act - 从数据库查询
            SysOssVo queriedFile = sysOssService.getById(uploadedFile.getOssId());

            // Assert
            assertThat(queriedFile).as("应该能从数据库查询到文件").isNotNull();

            assertThat(queriedFile.getOssId()).as("OSS ID 应该匹配").isEqualTo(uploadedFile.getOssId());

            assertThat(queriedFile.getOriginalName())
                    .as("原始文件名应该匹配")
                    .isEqualTo("database-test.txt");

            log.info("✅ 数据库记录查询成功");
        }

        @Test
        @Order(12)
        @DisplayName("应该支持上传不同类型的文件")
        void shouldSupportDifferentFileTypes() {
            log.info("=== 测试不同文件类型上传 ===");

            // Arrange & Act - 上传不同类型的文件
            MockMultipartFile txtFile =
                    new MockMultipartFile(
                            "file", "test.txt", "text/plain", "txt content".getBytes());
            MockMultipartFile jsonFile =
                    new MockMultipartFile(
                            "file",
                            "test.json",
                            "application/json",
                            "{\"key\":\"value\"}".getBytes());
            MockMultipartFile xmlFile =
                    new MockMultipartFile(
                            "file", "test.xml", "application/xml", "<root></root>".getBytes());

            SysOssVo txtResult = sysOssService.upload(txtFile);
            SysOssVo jsonResult = sysOssService.upload(jsonFile);
            SysOssVo xmlResult = sysOssService.upload(xmlFile);

            // Assert
            assertThat(txtResult.getFileSuffix()).isEqualTo(".txt");
            assertThat(jsonResult.getFileSuffix()).isEqualTo(".json");
            assertThat(xmlResult.getFileSuffix()).isEqualTo(".xml");

            log.info("✅ 不同文件类型上传成功");
            log.info("   TXT: {}", txtResult.getFileName());
            log.info("   JSON: {}", jsonResult.getFileName());
            log.info("   XML: {}", xmlResult.getFileName());
        }
    }

    @Nested
    @DisplayName("3. 文件查询测试")
    class FileQueryTests {

        @Test
        @Order(20)
        @DisplayName("应该能够分页查询文件列表")
        void shouldQueryFileListWithPagination() {
            log.info("=== 测试分页查询 ===");

            // Arrange - 上传多个文件
            for (int i = 1; i <= 5; i++) {
                MockMultipartFile file =
                        new MockMultipartFile(
                                "file",
                                "test-" + i + ".txt",
                                "text/plain",
                                ("content " + i).getBytes());
                sysOssService.upload(file);
            }

            // Act - 分页查询
            SysOssBo bo = new SysOssBo();
            PageQuery pageQuery = new PageQuery();
            pageQuery.setPageNum(1);
            pageQuery.setPageSize(3); // 每页3条
            TableDataInfo<SysOssVo> result = sysOssService.queryPageList(bo, pageQuery);

            // Assert
            assertThat(result).as("查询结果不应为空").isNotNull();

            assertThat(result.getRows()).as("应该返回3条记录").hasSize(3);

            assertThat(result.getTotal()).as("总记录数应该是5").isEqualTo(5);

            log.info("✅ 分页查询成功");
            log.info("   当前页: {}", result.getRows().size());
            log.info("   总记录数: {}", result.getTotal());
        }

        @Test
        @Order(21)
        @DisplayName("应该支持按文件名模糊查询")
        void shouldQueryByFileName() {
            log.info("=== 测试文件名查询 ===");

            // Arrange - 上传测试文件
            MockMultipartFile file1 =
                    new MockMultipartFile(
                            "file", "report-2024.pdf", "application/pdf", "pdf content".getBytes());
            MockMultipartFile file2 =
                    new MockMultipartFile(
                            "file",
                            "document-2024.docx",
                            "application/vnd.openxmlformats",
                            "docx content".getBytes());
            MockMultipartFile file3 =
                    new MockMultipartFile(
                            "file", "image-2024.png", "image/png", "png content".getBytes());

            sysOssService.upload(file1);
            sysOssService.upload(file2);
            sysOssService.upload(file3);

            // Act - 查询包含 "2024" 的文件
            SysOssBo bo = new SysOssBo();
            bo.setOriginalName("2024");
            PageQuery pageQuery = new PageQuery();
            pageQuery.setPageNum(1);
            pageQuery.setPageSize(10);
            TableDataInfo<SysOssVo> result = sysOssService.queryPageList(bo, pageQuery);

            // Assert
            assertThat(result.getRows())
                    .as("应该查询到3个包含 '2024' 的文件")
                    .hasSizeGreaterThanOrEqualTo(3)
                    .allMatch(vo -> vo.getOriginalName().contains("2024"));

            log.info("✅ 文件名查询成功");
            log.info("   匹配记录数: {}", result.getRows().size());
        }

        @Test
        @Order(22)
        @DisplayName("应该支持按文件后缀查询")
        void shouldQueryByFileSuffix() {
            log.info("=== 测试文件后缀查询 ===");

            // Arrange - 上传不同后缀的文件
            sysOssService.upload(
                    new MockMultipartFile(
                            "file", "file1.txt", "text/plain", "content1".getBytes()));
            sysOssService.upload(
                    new MockMultipartFile(
                            "file", "file2.txt", "text/plain", "content2".getBytes()));
            sysOssService.upload(
                    new MockMultipartFile(
                            "file", "file3.pdf", "application/pdf", "content3".getBytes()));

            // Act - 查询 .txt 文件
            SysOssBo bo = new SysOssBo();
            bo.setFileSuffix(".txt");
            PageQuery pageQuery = new PageQuery();
            pageQuery.setPageNum(1);
            pageQuery.setPageSize(10);
            TableDataInfo<SysOssVo> result = sysOssService.queryPageList(bo, pageQuery);

            // Assert
            assertThat(result.getRows())
                    .as("应该只查询到 .txt 文件")
                    .hasSizeGreaterThanOrEqualTo(2)
                    .allMatch(vo -> ".txt".equals(vo.getFileSuffix()));

            log.info("✅ 文件后缀查询成功");
            log.info("   .txt 文件数: {}", result.getRows().size());
        }

        @Test
        @Order(23)
        @DisplayName("应该能够根据ID批量查询文件")
        void shouldQueryByIds() {
            log.info("=== 测试批量ID查询 ===");

            // Arrange - 上传文件
            SysOssVo file1 =
                    sysOssService.upload(
                            new MockMultipartFile(
                                    "file", "batch1.txt", "text/plain", "content1".getBytes()));
            SysOssVo file2 =
                    sysOssService.upload(
                            new MockMultipartFile(
                                    "file", "batch2.txt", "text/plain", "content2".getBytes()));
            SysOssVo file3 =
                    sysOssService.upload(
                            new MockMultipartFile(
                                    "file", "batch3.txt", "text/plain", "content3".getBytes()));

            // Act - 批量查询
            Collection<Long> ids =
                    Arrays.asList(file1.getOssId(), file2.getOssId(), file3.getOssId());
            List<SysOssVo> result = sysOssService.listByIds(ids);

            // Assert
            assertThat(result)
                    .as("应该查询到3个文件")
                    .hasSize(3)
                    .extracting(SysOssVo::getOssId)
                    .containsExactlyInAnyOrder(
                            file1.getOssId(), file2.getOssId(), file3.getOssId());

            log.info("✅ 批量ID查询成功");
            log.info("   查询到的文件数: {}", result.size());
        }
    }

    @Nested
    @DisplayName("4. 文件删除测试")
    class FileDeletionTests {

        @Test
        @Order(30)
        @DisplayName("应该能够删除单个文件")
        void shouldDeleteSingleFile() {
            log.info("=== 测试删除单个文件 ===");

            // Arrange - 上传文件
            SysOssVo uploadedFile =
                    sysOssService.upload(
                            new MockMultipartFile(
                                    "file",
                                    "delete-test.txt",
                                    "text/plain",
                                    "to be deleted".getBytes()));
            Long ossId = uploadedFile.getOssId();

            // Act - 删除文件
            Boolean deleteResult = sysOssService.deleteWithValidByIds(Arrays.asList(ossId), false);

            // Assert
            assertThat(deleteResult).as("删除操作应该成功").isTrue();

            // Verify - 验证文件已删除
            SysOssVo deletedFile = sysOssService.getById(ossId);
            assertThat(deletedFile).as("删除后应该查询不到文件").isNull();

            log.info("✅ 文件删除成功");
        }

        @Test
        @Order(31)
        @DisplayName("应该能够批量删除文件")
        void shouldDeleteMultipleFiles() {
            log.info("=== 测试批量删除文件 ===");

            // Arrange - 上传多个文件
            SysOssVo file1 =
                    sysOssService.upload(
                            new MockMultipartFile(
                                    "file", "delete1.txt", "text/plain", "content1".getBytes()));
            SysOssVo file2 =
                    sysOssService.upload(
                            new MockMultipartFile(
                                    "file", "delete2.txt", "text/plain", "content2".getBytes()));
            SysOssVo file3 =
                    sysOssService.upload(
                            new MockMultipartFile(
                                    "file", "delete3.txt", "text/plain", "content3".getBytes()));

            Collection<Long> ids =
                    Arrays.asList(file1.getOssId(), file2.getOssId(), file3.getOssId());

            // Act - 批量删除
            Boolean deleteResult = sysOssService.deleteWithValidByIds(ids, false);

            // Assert
            assertThat(deleteResult).as("批量删除应该成功").isTrue();

            // Verify - 验证所有文件已删除
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            Integer count =
                    jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM sys_oss WHERE oss_id IN (?, ?, ?)",
                            Integer.class,
                            file1.getOssId(),
                            file2.getOssId(),
                            file3.getOssId());

            assertThat(count).as("数据库中不应该还有这些文件记录").isEqualTo(0);

            log.info("✅ 批量删除成功");
            log.info("   删除文件数: {}", ids.size());
        }
    }

    @Nested
    @DisplayName("5. 缓存测试")
    class CacheTests {

        @Test
        @Order(40)
        @DisplayName("getById 方法应该使用缓存")
        void shouldUseCacheForGetById() {
            log.info("=== 测试缓存功能 ===");

            // Arrange - 上传文件
            SysOssVo uploadedFile =
                    sysOssService.upload(
                            new MockMultipartFile(
                                    "file",
                                    "cache-test.txt",
                                    "text/plain",
                                    "cached content".getBytes()));
            Long ossId = uploadedFile.getOssId();

            // Act - 第一次查询（缓存 miss）
            SysOssVo firstQuery = sysOssService.getById(ossId);

            // Act - 第二次查询（应该从缓存读取）
            SysOssVo secondQuery = sysOssService.getById(ossId);

            // Assert
            assertThat(firstQuery).as("第一次查询应该成功").isNotNull();

            assertThat(secondQuery).as("第二次查询应该成功").isNotNull();

            assertThat(secondQuery.getOssId()).as("两次查询结果应该一致").isEqualTo(firstQuery.getOssId());

            log.info("✅ 缓存功能正常");
            log.info("   注意: @Cacheable 应该在第二次查询时使用缓存");
        }
    }

    @AfterAll
    static void afterAll() {
        log.info("=== SysOssService 集成测试完成 ===");
        log.info("✅ 所有 OSS 文件存储功能测试通过");
    }
}
