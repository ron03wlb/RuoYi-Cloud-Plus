package org.dromara.common.test.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.StreamUtils;

/**
 * SQL 脚本执行器.
 *
 * <p>提供在集成测试中执行 SQL 脚本的工具方法，用于：
 *
 * <ul>
 *   <li>初始化数据库表结构
 *   <li>插入测试数据
 *   <li>清理测试数据
 * </ul>
 *
 * <h3>使用示例：</h3>
 *
 * <pre>{@code
 * // 执行单个 SQL 脚本
 * SqlScriptExecutor.execute(dataSource, "classpath:db/schema.sql");
 *
 * // 执行多个 SQL 脚本
 * SqlScriptExecutor.executeBatch(dataSource,
 *     "classpath:db/schema.sql",
 *     "classpath:db/data.sql"
 * );
 *
 * // 执行 SQL 语句
 * SqlScriptExecutor.executeSql(dataSource,
 *     "CREATE TABLE test (id INT PRIMARY KEY)",
 *     "INSERT INTO test VALUES (1)"
 * );
 * }</pre>
 *
 * @author Lion Li
 * @since 2025-11-10
 */
public class SqlScriptExecutor {

  private static final Logger log = LoggerFactory.getLogger(SqlScriptExecutor.class);

  /**
   * 执行 SQL 脚本文件.
   *
   * @param dataSource 数据源
   * @param scriptPath 脚本路径（支持 classpath: 前缀）
   */
  public static void execute(DataSource dataSource, String scriptPath) {
    log.info("执行 SQL 脚本: {}", scriptPath);

    try {
      Resource resource = resolveResource(scriptPath);
      if (!resource.exists()) {
        throw new IllegalArgumentException("SQL 脚本不存在: " + scriptPath);
      }

      ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
      populator.addScript(resource);
      populator.setContinueOnError(false);
      populator.setSeparator(";");
      populator.execute(dataSource);

      log.info("SQL 脚本执行成功: {}", scriptPath);
    } catch (Exception e) {
      log.error("执行 SQL 脚本失败: {}", scriptPath, e);
      throw new RuntimeException("执行 SQL 脚本失败: " + scriptPath, e);
    }
  }

  /**
   * 批量执行 SQL 脚本文件.
   *
   * @param dataSource 数据源
   * @param scriptPaths 脚本路径列表
   */
  public static void executeBatch(DataSource dataSource, String... scriptPaths) {
    log.info("批量执行 {} 个 SQL 脚本", scriptPaths.length);

    for (String scriptPath : scriptPaths) {
      execute(dataSource, scriptPath);
    }

    log.info("批量执行 SQL 脚本完成");
  }

  /**
   * 执行 SQL 语句.
   *
   * @param dataSource 数据源
   * @param sqlStatements SQL 语句列表
   */
  public static void executeSql(DataSource dataSource, String... sqlStatements) {
    log.info("执行 {} 条 SQL 语句", sqlStatements.length);

    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement()) {

      for (String sql : sqlStatements) {
        if (sql == null || sql.trim().isEmpty()) {
          continue;
        }

        log.debug("执行 SQL: {}", sql.substring(0, Math.min(100, sql.length())));
        stmt.execute(sql);
      }

      log.info("SQL 语句执行成功");
    } catch (SQLException e) {
      log.error("执行 SQL 语句失败", e);
      throw new RuntimeException("执行 SQL 语句失败", e);
    }
  }

  /**
   * 读取 SQL 脚本内容.
   *
   * @param scriptPath 脚本路径
   * @return SQL 脚本内容
   */
  public static String readScript(String scriptPath) {
    try {
      Resource resource = resolveResource(scriptPath);
      if (!resource.exists()) {
        throw new IllegalArgumentException("SQL 脚本不存在: " + scriptPath);
      }

      return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      log.error("读取 SQL 脚本失败: {}", scriptPath, e);
      throw new RuntimeException("读取 SQL 脚本失败: " + scriptPath, e);
    }
  }

  /**
   * 解析资源路径.
   *
   * @param path 路径
   * @return Resource 对象
   */
  private static Resource resolveResource(String path) {
    if (path.startsWith("classpath:")) {
      return new ClassPathResource(path.substring("classpath:".length()));
    } else {
      return new ClassPathResource(path);
    }
  }

  /**
   * 清空表数据.
   *
   * @param dataSource 数据源
   * @param tableNames 表名列表
   */
  public static void truncateTables(DataSource dataSource, String... tableNames) {
    log.info("清空 {} 个表的数据", tableNames.length);

    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement()) {

      for (String tableName : tableNames) {
        // 使用 CASCADE 自动处理外键约束（PostgreSQL 兼容）
        String sql = "TRUNCATE TABLE " + tableName + " CASCADE";
        log.debug("执行: {}", sql);
        stmt.execute(sql);
      }

      log.info("表数据清空成功");
    } catch (SQLException e) {
      log.error("清空表数据失败", e);
      throw new RuntimeException("清空表数据失败", e);
    }
  }

  /**
   * 删除表数据.
   *
   * @param dataSource 数据源
   * @param tableNames 表名列表
   */
  public static void deleteFromTables(DataSource dataSource, String... tableNames) {
    log.info("删除 {} 个表的数据", tableNames.length);

    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement()) {

      for (String tableName : tableNames) {
        String sql = "DELETE FROM " + tableName;
        log.debug("执行: {}", sql);
        stmt.execute(sql);
      }

      log.info("表数据删除成功");
    } catch (SQLException e) {
      log.error("删除表数据失败", e);
      throw new RuntimeException("删除表数据失败", e);
    }
  }

  /**
   * 检查表是否存在.
   *
   * @param dataSource 数据源
   * @param tableName 表名
   * @return true-存在，false-不存在
   */
  public static boolean tableExists(DataSource dataSource, String tableName) {
    try (Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement()) {

      String sql = "SELECT 1 FROM " + tableName + " LIMIT 1";
      stmt.execute(sql);
      return true;
    } catch (SQLException e) {
      return false;
    }
  }
}
