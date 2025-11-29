package org.dromara.common.mybatis.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * DataBaseType 枚举类测试.
 *
 * @author Test Team
 */
@DisplayName("DataBaseType 枚举类测试")
class DataBaseTypeTest {

  @Nested
  @DisplayName("1. 枚举常量测试")
  class EnumConstantsTests {

    @Test
    @DisplayName("应该包含4个数据库类型")
    void shouldHaveFourDatabaseTypes() {
      // Act
      DataBaseType[] types = DataBaseType.values();

      // Assert
      assertThat(types).hasSize(4);
    }

    @Test
    @DisplayName("MySQL 枚举常量验证")
    void shouldHaveMySqlConstant() {
      // Assert
      assertThat(DataBaseType.MY_SQL).isNotNull();
      assertThat(DataBaseType.MY_SQL.getType()).isEqualTo("MySQL");
    }

    @Test
    @DisplayName("Oracle 枚举常量验证")
    void shouldHaveOracleConstant() {
      // Assert
      assertThat(DataBaseType.ORACLE).isNotNull();
      assertThat(DataBaseType.ORACLE.getType()).isEqualTo("Oracle");
    }

    @Test
    @DisplayName("PostgreSQL 枚举常量验证")
    void shouldHavePostgreSqlConstant() {
      // Assert
      assertThat(DataBaseType.POSTGRE_SQL).isNotNull();
      assertThat(DataBaseType.POSTGRE_SQL.getType()).isEqualTo("PostgreSQL");
    }

    @Test
    @DisplayName("SQL Server 枚举常量验证")
    void shouldHaveSqlServerConstant() {
      // Assert
      assertThat(DataBaseType.SQL_SERVER).isNotNull();
      assertThat(DataBaseType.SQL_SERVER.getType()).isEqualTo("Microsoft SQL Server");
    }
  }

  @Nested
  @DisplayName("2. find 方法测试 - 有效数据库名称")
  class FindMethodValidNamesTests {

    @Test
    @DisplayName("应该找到 MySQL")
    void shouldFindMySQL() {
      // Act
      DataBaseType result = DataBaseType.find("MySQL");

      // Assert
      assertThat(result).isEqualTo(DataBaseType.MY_SQL);
    }

    @Test
    @DisplayName("应该找到 Oracle")
    void shouldFindOracle() {
      // Act
      DataBaseType result = DataBaseType.find("Oracle");

      // Assert
      assertThat(result).isEqualTo(DataBaseType.ORACLE);
    }

    @Test
    @DisplayName("应该找到 PostgreSQL")
    void shouldFindPostgreSQL() {
      // Act
      DataBaseType result = DataBaseType.find("PostgreSQL");

      // Assert
      assertThat(result).isEqualTo(DataBaseType.POSTGRE_SQL);
    }

    @Test
    @DisplayName("应该找到 Microsoft SQL Server")
    void shouldFindSqlServer() {
      // Act
      DataBaseType result = DataBaseType.find("Microsoft SQL Server");

      // Assert
      assertThat(result).isEqualTo(DataBaseType.SQL_SERVER);
    }
  }

  @Nested
  @DisplayName("3. find 方法测试 - 无效或边界值")
  class FindMethodInvalidNamesTests {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("应该返回 MySQL 作为默认值 - 当输入为 null 或空字符串")
    void shouldReturnMySqlAsDefault_WhenNullOrEmpty(String input) {
      // Act
      DataBaseType result = DataBaseType.find(input);

      // Assert
      assertThat(result).isEqualTo(DataBaseType.MY_SQL);
    }

    @ParameterizedTest
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("应该返回 MySQL 作为默认值 - 当输入为空白字符")
    void shouldReturnMySqlAsDefault_WhenBlank(String input) {
      // Act
      DataBaseType result = DataBaseType.find(input);

      // Assert
      assertThat(result).isEqualTo(DataBaseType.MY_SQL);
    }

    @ParameterizedTest
    @ValueSource(strings = {"UnknownDB", "DB2", "MongoDB", "Redis", "Cassandra"})
    @DisplayName("应该返回 MySQL 作为默认值 - 当数据库名称未知")
    void shouldReturnMySqlAsDefault_WhenUnknownDatabase(String databaseName) {
      // Act
      DataBaseType result = DataBaseType.find(databaseName);

      // Assert
      assertThat(result).isEqualTo(DataBaseType.MY_SQL);
    }

    @ParameterizedTest
    @ValueSource(strings = {"mysql", "MYSQL", "oracle", "ORACLE", "postgresql"})
    @DisplayName("应该区分大小写 - 不同大小写应返回默认值")
    void shouldBeCaseSensitive_WhenDifferentCase(String databaseName) {
      // Act
      DataBaseType result = DataBaseType.find(databaseName);

      // Assert
      assertThat(result).isEqualTo(DataBaseType.MY_SQL);
    }
  }

  @Nested
  @DisplayName("4. isMySql 方法测试")
  class IsMySqlMethodTests {

    @Test
    @DisplayName("MY_SQL 应该返回 true")
    void mySqlShouldReturnTrue() {
      assertThat(DataBaseType.MY_SQL.isMySql()).isTrue();
    }

    @Test
    @DisplayName("其他类型应该返回 false")
    void otherTypesShouldReturnFalse() {
      assertThat(DataBaseType.ORACLE.isMySql()).isFalse();
      assertThat(DataBaseType.POSTGRE_SQL.isMySql()).isFalse();
      assertThat(DataBaseType.SQL_SERVER.isMySql()).isFalse();
    }
  }

  @Nested
  @DisplayName("5. isOracle 方法测试")
  class IsOracleMethodTests {

    @Test
    @DisplayName("ORACLE 应该返回 true")
    void oracleShouldReturnTrue() {
      assertThat(DataBaseType.ORACLE.isOracle()).isTrue();
    }

    @Test
    @DisplayName("其他类型应该返回 false")
    void otherTypesShouldReturnFalse() {
      assertThat(DataBaseType.MY_SQL.isOracle()).isFalse();
      assertThat(DataBaseType.POSTGRE_SQL.isOracle()).isFalse();
      assertThat(DataBaseType.SQL_SERVER.isOracle()).isFalse();
    }
  }

  @Nested
  @DisplayName("6. isPostgreSql 方法测试")
  class IsPostgreSqlMethodTests {

    @Test
    @DisplayName("POSTGRE_SQL 应该返回 true")
    void postgreSqlShouldReturnTrue() {
      assertThat(DataBaseType.POSTGRE_SQL.isPostgreSql()).isTrue();
    }

    @Test
    @DisplayName("其他类型应该返回 false")
    void otherTypesShouldReturnFalse() {
      assertThat(DataBaseType.MY_SQL.isPostgreSql()).isFalse();
      assertThat(DataBaseType.ORACLE.isPostgreSql()).isFalse();
      assertThat(DataBaseType.SQL_SERVER.isPostgreSql()).isFalse();
    }
  }

  @Nested
  @DisplayName("7. isSqlServer 方法测试")
  class IsSqlServerMethodTests {

    @Test
    @DisplayName("SQL_SERVER 应该返回 true")
    void sqlServerShouldReturnTrue() {
      assertThat(DataBaseType.SQL_SERVER.isSqlServer()).isTrue();
    }

    @Test
    @DisplayName("其他类型应该返回 false")
    void otherTypesShouldReturnFalse() {
      assertThat(DataBaseType.MY_SQL.isSqlServer()).isFalse();
      assertThat(DataBaseType.ORACLE.isSqlServer()).isFalse();
      assertThat(DataBaseType.POSTGRE_SQL.isSqlServer()).isFalse();
    }
  }

  @Nested
  @DisplayName("8. 综合场景测试")
  class ComprehensiveScenarioTests {

    @ParameterizedTest
    @CsvSource({
      "MySQL, MY_SQL, true, false, false, false",
      "Oracle, ORACLE, false, true, false, false",
      "PostgreSQL, POSTGRE_SQL, false, false, true, false",
      "Microsoft SQL Server, SQL_SERVER, false, false, false, true"
    })
    @DisplayName("综合验证 - 数据库类型识别和判断方法")
    void shouldCorrectlyIdentifyDatabaseType(
        String databaseName,
        DataBaseType expectedType,
        boolean expectedIsMySql,
        boolean expectedIsOracle,
        boolean expectedIsPostgreSql,
        boolean expectedIsSqlServer) {
      // Act
      DataBaseType result = DataBaseType.find(databaseName);

      // Assert
      assertThat(result).isEqualTo(expectedType);
      assertThat(result.isMySql()).isEqualTo(expectedIsMySql);
      assertThat(result.isOracle()).isEqualTo(expectedIsOracle);
      assertThat(result.isPostgreSql()).isEqualTo(expectedIsPostgreSql);
      assertThat(result.isSqlServer()).isEqualTo(expectedIsSqlServer);
    }

    @Test
    @DisplayName("验证所有枚举常量互斥性")
    void shouldHaveMutualExclusiveTypes() {
      // 验证每个类型只对应一个 is 方法返回 true
      for (DataBaseType type : DataBaseType.values()) {
        int trueCount = 0;
        if (type.isMySql()) trueCount++;
        if (type.isOracle()) trueCount++;
        if (type.isPostgreSql()) trueCount++;
        if (type.isSqlServer()) trueCount++;

        assertThat(trueCount).as("DataBaseType %s 应该只有一个 is 方法返回 true", type).isEqualTo(1);
      }
    }
  }

  @Nested
  @DisplayName("9. Lombok @Getter 测试")
  class GetterMethodTests {

    @Test
    @DisplayName("getType 方法应该返回正确的数据库类型字符串")
    void getTypeShouldReturnCorrectDatabaseType() {
      assertThat(DataBaseType.MY_SQL.getType()).isEqualTo("MySQL");
      assertThat(DataBaseType.ORACLE.getType()).isEqualTo("Oracle");
      assertThat(DataBaseType.POSTGRE_SQL.getType()).isEqualTo("PostgreSQL");
      assertThat(DataBaseType.SQL_SERVER.getType()).isEqualTo("Microsoft SQL Server");
    }

    @Test
    @DisplayName("getType 方法返回值不应该为 null")
    void getTypeShouldNeverReturnNull() {
      for (DataBaseType type : DataBaseType.values()) {
        assertThat(type.getType()).as("DataBaseType %s 的 type 属性不应该为 null", type).isNotNull();
      }
    }
  }

  @Nested
  @DisplayName("10. 枚举基本特性测试")
  class EnumBasicFeaturesTests {

    @Test
    @DisplayName("valueOf 方法应该正确工作")
    void valueOfShouldWork() {
      assertThat(DataBaseType.valueOf("MY_SQL")).isEqualTo(DataBaseType.MY_SQL);
      assertThat(DataBaseType.valueOf("ORACLE")).isEqualTo(DataBaseType.ORACLE);
      assertThat(DataBaseType.valueOf("POSTGRE_SQL")).isEqualTo(DataBaseType.POSTGRE_SQL);
      assertThat(DataBaseType.valueOf("SQL_SERVER")).isEqualTo(DataBaseType.SQL_SERVER);
    }

    @Test
    @DisplayName("枚举常量应该是单例")
    void enumConstantsShouldBeSingletons() {
      assertThat(DataBaseType.valueOf("MY_SQL")).isSameAs(DataBaseType.MY_SQL);
      assertThat(DataBaseType.valueOf("ORACLE")).isSameAs(DataBaseType.ORACLE);
    }

    @Test
    @DisplayName("name 方法应该返回枚举常量名称")
    void nameShouldReturnConstantName() {
      assertThat(DataBaseType.MY_SQL.name()).isEqualTo("MY_SQL");
      assertThat(DataBaseType.ORACLE.name()).isEqualTo("ORACLE");
      assertThat(DataBaseType.POSTGRE_SQL.name()).isEqualTo("POSTGRE_SQL");
      assertThat(DataBaseType.SQL_SERVER.name()).isEqualTo("SQL_SERVER");
    }

    @Test
    @DisplayName("ordinal 方法应该返回枚举常量顺序")
    void ordinalShouldReturnCorrectOrder() {
      assertThat(DataBaseType.MY_SQL.ordinal()).isEqualTo(0);
      assertThat(DataBaseType.ORACLE.ordinal()).isEqualTo(1);
      assertThat(DataBaseType.POSTGRE_SQL.ordinal()).isEqualTo(2);
      assertThat(DataBaseType.SQL_SERVER.ordinal()).isEqualTo(3);
    }
  }
}
