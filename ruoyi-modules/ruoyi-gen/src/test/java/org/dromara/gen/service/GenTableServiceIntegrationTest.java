package org.dromara.gen.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.test.BaseIntegrationTest;
import org.dromara.common.test.utils.SqlScriptExecutor;
import org.dromara.gen.config.TestSaTokenConfig;
import org.dromara.gen.domain.GenTable;
import org.dromara.gen.domain.GenTableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

/**
 * GenTableService 集成测试
 * <p>
 * 测试代码生成服务的核心功能：
 * <ul>
 *     <li>数据库表查询</li>
 *     <li>表信息导入</li>
 *     <li>字段信息查询</li>
 *     <li>代码生成（Velocity）</li>
 *     <li>代码预览</li>
 *     <li>代码下载（ZIP）</li>
 * </ul>
 *
 * <p><strong>⚠️ 当前状态：已禁用（@Disabled）</strong></p>
 *
 * <p><strong>问题描述：</strong></p>
 * <ul>
 *     <li>测试在 Spring Boot 上下文启动阶段挂起，无法完成</li>
 *     <li>即使添加 @Timeout 注解，测试仍会挂起不停止</li>
 *     <li>可能原因：Testcontainers 初始化、Dubbo 配置、或 Nacos 连接问题</li>
 * </ul>
 *
 * <p><strong>已尝试的解决方案：</strong></p>
 * <ol>
 *     <li>❌ 添加类级别 @Timeout(120, TimeUnit.SECONDS) - 无效，测试仍挂起</li>
 *     <li>❌ 保持 PER_CLASS 生命周期以重用容器 - 无改善</li>
 *     <li>❌ 简化配置（禁用 Nacos/Dubbo） - 已在 properties 中配置</li>
 * </ol>
 *
 * <p><strong>建议的解决方案：</strong></p>
 * <ul>
 *     <li><strong>方案 A（推荐）：</strong>拆分为 5 个独立的测试类
 *         <ul>
 *             <li>GenTableInfrastructureTest.java - 基础设施测试（3 tests）</li>
 *             <li>GenTableDatabaseQueryTest.java - 数据库查询（3 tests）</li>
 *             <li>GenTableImportTest.java - 表导入（3 tests）</li>
 *             <li>GenTableInfoQueryTest.java - 表信息查询（3 tests）</li>
 *             <li>GenTableCodeGenerationTest.java - 代码生成（5 tests）</li>
 *         </ul>
 *     </li>
 *     <li><strong>方案 B：</strong>使用 H2 内存数据库替代 Testcontainers MySQL</li>
 *     <li><strong>方案 C：</strong>完全移除 Dubbo 依赖，使用纯 Spring Boot 配置</li>
 *     <li><strong>方案 D：</strong>改为手动测试或端到端测试</li>
 * </ul>
 *
 * <p><strong>下一步行动：</strong></p>
 * <ul>
 *     <li>实施方案 A：将此测试类拆分为 5 个独立的小测试类</li>
 *     <li>每个测试类使用 PER_METHOD 生命周期</li>
 *     <li>在方法级别添加 @Timeout 注解</li>
 *     <li>简化每个测试类的数据准备</li>
 * </ul>
 *
 * @author Lion Li
 * @since 2025-11-10
 */
@Disabled("测试在 Spring 上下文启动时挂起。" +
    "原因：Testcontainers/Dubbo/Nacos 初始化问题。" +
    "解决方案：需要拆分为多个独立的小测试类，每个类专注一个功能领域。" +
    "参考：docs/INTEGRATION-TEST-TRACKER.md")
@SpringBootTest(
    classes = org.dromara.gen.config.TestApplication.class,
    properties = {
        "spring.profiles.active=test",
        "spring.cloud.nacos.config.enabled=false",
        "spring.cloud.nacos.discovery.enabled=false",
        "spring.main.allow-bean-definition-overriding=true"
    }
)
@Import(TestSaTokenConfig.class)
@DisplayName("GenTableService 集成测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Timeout(value = 120, unit = TimeUnit.SECONDS)
class GenTableServiceIntegrationTest extends BaseIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(GenTableServiceIntegrationTest.class);

    @Autowired
    private IGenTableService genTableService;

    @Autowired
    private DataSource dataSource;

    /**
     * 初始化测试数据库
     */
    @BeforeAll
    void initDatabase() {
        log.info("=== 初始化测试数据库 ===");
        // 执行数据库初始化脚本
        SqlScriptExecutor.execute(dataSource, "classpath:init-test-tables.sql");
        log.info("✅ 测试数据库初始化完成");
    }

    @BeforeEach
    void setUp() {
        log.info("=== 测试开始 ===");
    }

    @AfterEach
    void tearDown() {
        log.info("=== 测试结束 ===");
    }

    @Nested
    @DisplayName("1. 基础设施测试")
    @Order(1)
    class InfrastructureTests {

        @Test
        @DisplayName("应该成功加载 Spring 上下文")
        void shouldLoadSpringContext() {
            // Assert
            assertThat(genTableService).isNotNull();
            assertThat(dataSource).isNotNull();
            log.info("✅ GenTableService 和 DataSource 成功注入");
        }

        @Test
        @DisplayName("应该成功连接到数据库")
        void shouldConnectToDatabase() {
            // Arrange
            String jdbcUrl = getMysqlJdbcUrl();

            // Assert
            assertThat(jdbcUrl).isNotNull();
            assertThat(jdbcUrl).contains("jdbc:mysql://");
            log.info("✅ 数据库连接成功: {}", jdbcUrl);
        }

        @Test
        @DisplayName("应该成功初始化测试表")
        void shouldInitializeTestTables() {
            // Act - 检查测试表是否存在
            boolean testUserExists = SqlScriptExecutor.tableExists(dataSource, "test_user");
            boolean testProductExists = SqlScriptExecutor.tableExists(dataSource, "test_product");
            boolean genTableExists = SqlScriptExecutor.tableExists(dataSource, "gen_table");
            boolean genTableColumnExists = SqlScriptExecutor.tableExists(dataSource, "gen_table_column");

            // Assert
            assertThat(testUserExists).as("test_user 表应该存在").isTrue();
            assertThat(testProductExists).as("test_product 表应该存在").isTrue();
            assertThat(genTableExists).as("gen_table 表应该存在").isTrue();
            assertThat(genTableColumnExists).as("gen_table_column 表应该存在").isTrue();

            log.info("✅ 测试表初始化成功");
        }
    }

    @Nested
    @DisplayName("2. 数据库表查询测试")
    @Order(2)
    class DatabaseTableQueryTests {

        @Test
        @DisplayName("应该能够查询数据库表列表")
        void shouldQueryDatabaseTableList() {
            // Arrange
            GenTable queryParam = new GenTable();
            queryParam.setDataName("master");
            PageQuery pageQuery = new PageQuery(10, 1);

            // Act
            TableDataInfo<GenTable> result = genTableService.selectPageDbTableList(queryParam, pageQuery);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getRows()).isNotEmpty();

            log.info("✅ 查询到 {} 张表", result.getTotal());
            result.getRows().forEach(table ->
                log.info("   表名: {}, 描述: {}", table.getTableName(), table.getTableComment())
            );
        }

        @Test
        @DisplayName("应该能够按表名查询特定表")
        void shouldQueryTableByNames() {
            // Arrange
            String[] tableNames = {"test_user"};

            // Act
            List<GenTable> tables = genTableService.selectDbTableListByNames(tableNames, "master");

            // Assert
            assertThat(tables).isNotNull();
            assertThat(tables).hasSizeGreaterThanOrEqualTo(1);

            GenTable testUserTable = tables.stream()
                .filter(t -> "test_user".equals(t.getTableName()))
                .findFirst()
                .orElse(null);

            assertThat(testUserTable).isNotNull();
            assertThat(testUserTable.getTableComment()).contains("测试用户表");

            log.info("✅ 查询表成功: {} - {}", testUserTable.getTableName(), testUserTable.getTableComment());
        }

        @Test
        @DisplayName("应该能够查询多张表")
        void shouldQueryMultipleTables() {
            // Arrange
            String[] tableNames = {"test_user", "test_product"};

            // Act
            List<GenTable> tables = genTableService.selectDbTableListByNames(tableNames, "master");

            // Assert
            assertThat(tables).isNotNull();
            assertThat(tables).hasSizeGreaterThanOrEqualTo(2);

            List<String> foundTableNames = tables.stream()
                .map(GenTable::getTableName)
                .toList();

            assertThat(foundTableNames).contains("test_user", "test_product");

            log.info("✅ 查询到 {} 张表: {}", tables.size(), foundTableNames);
        }
    }

    @Nested
    @DisplayName("3. 表导入测试")
    @Order(3)
    class TableImportTests {

        @BeforeEach
        void cleanupGenTables() {
            // 清理之前可能导入的表
            SqlScriptExecutor.deleteFromTables(dataSource, "gen_table_column", "gen_table");
        }

        @Test
        @DisplayName("应该能够导入数据库表")
        void shouldImportDatabaseTable() {
            // Arrange
            String[] tableNames = {"test_user"};
            List<GenTable> tablesToImport = genTableService.selectDbTableListByNames(tableNames, "master");
            assertThat(tablesToImport).isNotEmpty();

            // Act
            genTableService.importGenTable(tablesToImport, "master");

            // Assert
            GenTable searchParam = new GenTable();
            searchParam.setTableName("test_user");
            TableDataInfo<GenTable> result = genTableService.selectPageGenTableList(searchParam, new PageQuery(10, 1));

            assertThat(result.getRows()).isNotEmpty();
            GenTable importedTable = result.getRows().get(0);
            assertThat(importedTable.getTableName()).isEqualTo("test_user");
            assertThat(importedTable.getTableComment()).contains("测试用户表");

            log.info("✅ 表导入成功: {} - {}", importedTable.getTableName(), importedTable.getTableComment());
        }

        @Test
        @DisplayName("导入的表应该包含正确的字段信息")
        void shouldImportTableWithCorrectColumns() {
            // Arrange
            String[] tableNames = {"test_product"};
            List<GenTable> tablesToImport = genTableService.selectDbTableListByNames(tableNames, "master");
            assertThat(tablesToImport).isNotEmpty();

            genTableService.importGenTable(tablesToImport, "master");

            // Find the imported table ID
            GenTable searchParam = new GenTable();
            searchParam.setTableName("test_product");
            TableDataInfo<GenTable> result = genTableService.selectPageGenTableList(searchParam, new PageQuery(10, 1));
            assertThat(result.getRows()).isNotEmpty();
            Long tableId = result.getRows().get(0).getTableId();

            // Act
            List<GenTableColumn> columns = genTableService.selectGenTableColumnListByTableId(tableId);

            // Assert
            assertThat(columns).isNotEmpty();

            List<String> columnNames = columns.stream()
                .map(GenTableColumn::getColumnName)
                .toList();

            assertThat(columnNames).contains("id", "product_name", "product_code", "price", "stock");

            log.info("✅ 导入了 {} 个字段: {}", columns.size(), columnNames);
        }

        @Test
        @DisplayName("主键字段应该被正确识别")
        void shouldIdentifyPrimaryKeyCorrectly() {
            // Arrange
            String[] tableNames = {"test_user"};
            List<GenTable> tablesToImport = genTableService.selectDbTableListByNames(tableNames, "master");
            genTableService.importGenTable(tablesToImport, "master");

            GenTable searchParam = new GenTable();
            searchParam.setTableName("test_user");
            TableDataInfo<GenTable> result = genTableService.selectPageGenTableList(searchParam, new PageQuery(10, 1));
            assertThat(result.getRows()).isNotEmpty();
            Long tableId = result.getRows().get(0).getTableId();

            // Act
            List<GenTableColumn> columns = genTableService.selectGenTableColumnListByTableId(tableId);

            // Assert
            GenTableColumn pkColumn = columns.stream()
                .filter(col -> "1".equals(col.getIsPk()))
                .findFirst()
                .orElse(null);

            assertThat(pkColumn).isNotNull();
            assertThat(pkColumn.getColumnName()).isEqualTo("id");
            assertThat(pkColumn.getIsPk()).isEqualTo("1");

            log.info("✅ 主键字段: {} (isPk={}, isIncrement={})",
                pkColumn.getColumnName(), pkColumn.getIsPk(), pkColumn.getIsIncrement());
        }
    }

    @Nested
    @DisplayName("4. 表信息查询测试")
    @Order(4)
    class TableInfoQueryTests {

        @BeforeEach
        void setupTables() {
            // 确保有测试数据
            SqlScriptExecutor.deleteFromTables(dataSource, "gen_table_column", "gen_table");

            String[] tableNames = {"test_user", "test_product"};
            List<GenTable> tablesToImport = genTableService.selectDbTableListByNames(tableNames, "master");
            genTableService.importGenTable(tablesToImport, "master");
        }

        @Test
        @DisplayName("应该能够通过ID查询表信息")
        void shouldQueryTableById() {
            // Arrange
            GenTable searchParam = new GenTable();
            searchParam.setTableName("test_user");
            TableDataInfo<GenTable> result = genTableService.selectPageGenTableList(searchParam, new PageQuery(10, 1));
            assertThat(result.getRows()).isNotEmpty();
            Long tableId = result.getRows().get(0).getTableId();

            // Act
            GenTable genTable = genTableService.selectGenTableById(tableId);

            // Assert
            assertThat(genTable).isNotNull();
            assertThat(genTable.getTableName()).isEqualTo("test_user");
            assertThat(genTable.getTableComment()).contains("测试用户表");

            log.info("✅ 查询表信息成功: {} - {}", genTable.getTableName(), genTable.getTableComment());
        }

        @Test
        @DisplayName("查询的表应该包含字段列表")
        void shouldIncludeColumnsInTableQuery() {
            // Arrange
            GenTable searchParam = new GenTable();
            searchParam.setTableName("test_user");
            TableDataInfo<GenTable> result = genTableService.selectPageGenTableList(searchParam, new PageQuery(10, 1));
            assertThat(result.getRows()).isNotEmpty();
            Long tableId = result.getRows().get(0).getTableId();

            // Act
            GenTable genTable = genTableService.selectGenTableById(tableId);

            // Assert
            assertThat(genTable).isNotNull();
            assertThat(genTable.getColumns()).isNotNull();
            assertThat(genTable.getColumns()).isNotEmpty();
            assertThat(genTable.getColumns()).hasSizeGreaterThan(5);

            log.info("✅ 表包含 {} 个字段", genTable.getColumns().size());
        }

        @Test
        @DisplayName("应该能够查询所有已导入的表")
        void shouldQueryAllImportedTables() {
            // Act
            List<GenTable> allTables = genTableService.selectGenTableAll();

            // Assert
            assertThat(allTables).isNotNull();
            assertThat(allTables).hasSizeGreaterThanOrEqualTo(2);

            List<String> tableNamesList = allTables.stream()
                .map(GenTable::getTableName)
                .toList();

            assertThat(tableNamesList).contains("test_user", "test_product");

            log.info("✅ 查询到 {} 张已导入的表: {}", allTables.size(), tableNamesList);
        }
    }

    @Nested
    @DisplayName("5. 代码生成测试（Velocity）")
    @Order(5)
    class CodeGenerationTests {

        private Long testTableId;

        @BeforeEach
        void setupTable() {
            // 清理并导入测试表
            SqlScriptExecutor.deleteFromTables(dataSource, "gen_table_column", "gen_table");

            String[] tableNames = {"test_user"};
            List<GenTable> tablesToImport = genTableService.selectDbTableListByNames(tableNames, "master");
            genTableService.importGenTable(tablesToImport, "master");

            // 获取表ID
            GenTable searchParam = new GenTable();
            searchParam.setTableName("test_user");
            TableDataInfo<GenTable> result = genTableService.selectPageGenTableList(searchParam, new PageQuery(10, 1));
            testTableId = result.getRows().get(0).getTableId();
        }

        @Test
        @DisplayName("应该能够预览生成的代码")
        void shouldPreviewGeneratedCode() {
            // Act
            Map<String, String> previewCode = genTableService.previewCode(testTableId);

            // Assert
            assertThat(previewCode).isNotNull();
            assertThat(previewCode).isNotEmpty();

            // 验证生成的文件
            assertThat(previewCode).containsKeys(
                "domain/TestUser.java",
                "mapper/TestUserMapper.java",
                "service/ITestUserService.java",
                "service/impl/TestUserServiceImpl.java",
                "controller/TestUserController.java"
            );

            log.info("✅ 生成了 {} 个代码文件", previewCode.size());
            previewCode.keySet().forEach(fileName ->
                log.info("   - {}", fileName)
            );

            // 验证 Controller 代码包含必要的注解
            String controllerCode = previewCode.get("controller/TestUserController.java");
            assertThat(controllerCode)
                .contains("@RestController")
                .contains("@RequestMapping");

            log.info("✅ 代码预览验证通过");
        }

        @Test
        @DisplayName("生成的实体类应该包含所有字段")
        void shouldGenerateEntityWithAllFields() {
            // Act
            Map<String, String> previewCode = genTableService.previewCode(testTableId);
            String domainCode = previewCode.get("domain/TestUser.java");

            // Assert
            assertThat(domainCode).isNotNull();
            assertThat(domainCode)
                .contains("class TestUser")
                .contains("private Long id")
                .contains("private String username")
                .contains("private String email")
                .contains("private String phone");

            log.info("✅ 实体类生成正确，包含所有字段");
        }

        @Test
        @DisplayName("生成的 Mapper 应该继承 BaseMapper")
        void shouldGenerateMapperExtendsBaseMapper() {
            // Act
            Map<String, String> previewCode = genTableService.previewCode(testTableId);
            String mapperCode = previewCode.get("mapper/TestUserMapper.java");

            // Assert
            assertThat(mapperCode).isNotNull();
            assertThat(mapperCode)
                .contains("interface TestUserMapper")
                .contains("extends BaseMapperPlus");

            log.info("✅ Mapper 接口生成正确");
        }

        @Test
        @DisplayName("应该能够下载生成的代码（ZIP）")
        void shouldDownloadGeneratedCodeAsZip() {
            // Act
            byte[] zipBytes = genTableService.downloadCode(testTableId);

            // Assert
            assertThat(zipBytes).isNotNull();
            assertThat(zipBytes.length).isGreaterThan(0);

            // ZIP 文件应该以 PK 开头（ZIP 文件头）
            assertThat(zipBytes[0]).isEqualTo((byte) 0x50); // 'P'
            assertThat(zipBytes[1]).isEqualTo((byte) 0x4B); // 'K'

            log.info("✅ 生成的 ZIP 文件大小: {} bytes", zipBytes.length);
        }
    }

    @AfterAll
    static void cleanup() {
        log.info("=== 所有测试完成 ===");
    }
}
