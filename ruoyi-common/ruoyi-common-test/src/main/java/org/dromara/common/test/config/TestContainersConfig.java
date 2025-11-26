package org.dromara.common.test.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

/**
 * Testcontainers 配置类
 *
 * <p>提供各种测试容器的配置和管理
 *
 * <p>注意：项目已全面迁移至 PostgreSQL，不再使用 MySQL
 *
 * @author Lion Li
 * @since 2025-11-09
 */
public class TestContainersConfig {

    private static final Logger log = LoggerFactory.getLogger(TestContainersConfig.class);

    /** PostgreSQL 容器配置 */
    public static class PostgreSQL {
        private static final String POSTGRES_IMAGE = "postgres:17-alpine";
        private static final String DATABASE_NAME = "ry_cloud_test";
        private static final String USERNAME = "postgres";
        private static final String PASSWORD = "postgres123";

        /** 创建 PostgreSQL 容器 */
        public static PostgreSQLContainer<?> createContainer() {
            PostgreSQLContainer<?> postgres =
                    new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE))
                            .withDatabaseName(DATABASE_NAME)
                            .withUsername(USERNAME)
                            .withPassword(PASSWORD)
                            .withReuse(true)
                            .withLogConsumer(new Slf4jLogConsumer(log));

            return postgres;
        }

        public static String getDatabaseName() {
            return DATABASE_NAME;
        }

        public static String getUsername() {
            return USERNAME;
        }

        public static String getPassword() {
            return PASSWORD;
        }
    }

    /** Redis 容器配置 */
    public static class Redis {
        private static final String REDIS_IMAGE = "redis:7-alpine";
        private static final int REDIS_PORT = 6379;

        /** 创建 Redis 容器 */
        @SuppressWarnings("resource")
        public static GenericContainer<?> createContainer() {
            GenericContainer<?> redis =
                    new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
                            .withExposedPorts(REDIS_PORT)
                            .withReuse(true)
                            .withLogConsumer(new Slf4jLogConsumer(log));

            return redis;
        }

        public static int getPort() {
            return REDIS_PORT;
        }
    }

    /** MinIO 容器配置（用于 OSS 测试） */
    public static class MinIO {
        private static final String MINIO_IMAGE = "minio/minio:latest";
        private static final int MINIO_API_PORT = 9000;
        private static final int MINIO_CONSOLE_PORT = 9001;
        private static final String ACCESS_KEY = "minioadmin";
        private static final String SECRET_KEY = "minioadmin";

        /** 创建 MinIO 容器 */
        @SuppressWarnings("resource")
        public static GenericContainer<?> createContainer() {
            GenericContainer<?> minio =
                    new GenericContainer<>(DockerImageName.parse(MINIO_IMAGE))
                            .withExposedPorts(MINIO_API_PORT, MINIO_CONSOLE_PORT)
                            .withEnv("MINIO_ROOT_USER", ACCESS_KEY)
                            .withEnv("MINIO_ROOT_PASSWORD", SECRET_KEY)
                            .withCommand(
                                    "server",
                                    "/data",
                                    "--console-address",
                                    ":" + MINIO_CONSOLE_PORT)
                            .withReuse(true)
                            .withLogConsumer(new Slf4jLogConsumer(log));

            return minio;
        }

        public static int getApiPort() {
            return MINIO_API_PORT;
        }

        public static int getConsolePort() {
            return MINIO_CONSOLE_PORT;
        }

        public static String getAccessKey() {
            return ACCESS_KEY;
        }

        public static String getSecretKey() {
            return SECRET_KEY;
        }
    }

    /** RocketMQ 容器配置（可选） */
    public static class RocketMQ {
        private static final String ROCKETMQ_IMAGE = "apache/rocketmq:5.1.0";
        private static final int NAMESRV_PORT = 9876;
        private static final int BROKER_PORT = 10911;

        /** 创建 RocketMQ NameServer 容器 */
        @SuppressWarnings("resource")
        public static GenericContainer<?> createNameServerContainer() {
            GenericContainer<?> nameServer =
                    new GenericContainer<>(DockerImageName.parse(ROCKETMQ_IMAGE))
                            .withExposedPorts(NAMESRV_PORT)
                            .withCommand("sh", "mqnamesrv")
                            .withReuse(true)
                            .withLogConsumer(new Slf4jLogConsumer(log));

            return nameServer;
        }

        public static int getNameServerPort() {
            return NAMESRV_PORT;
        }

        public static int getBrokerPort() {
            return BROKER_PORT;
        }
    }

    /**
     * Nacos 容器配置（可选）
     *
     * <p>注意：Nacos 容器启动较慢，建议使用 Mock 配置代替
     */
    public static class Nacos {
        private static final String NACOS_IMAGE = "nacos/nacos-server:v2.2.3";
        private static final int NACOS_PORT = 8848;
        private static final String USERNAME = "nacos";
        private static final String PASSWORD = "nacos";

        /** 创建 Nacos 容器 */
        @SuppressWarnings("resource")
        public static GenericContainer<?> createContainer() {
            GenericContainer<?> nacos =
                    new GenericContainer<>(DockerImageName.parse(NACOS_IMAGE))
                            .withExposedPorts(NACOS_PORT)
                            .withEnv("MODE", "standalone")
                            .withEnv("NACOS_AUTH_ENABLE", "false")
                            .withReuse(true)
                            .withLogConsumer(new Slf4jLogConsumer(log));

            return nacos;
        }

        public static int getPort() {
            return NACOS_PORT;
        }

        public static String getUsername() {
            return USERNAME;
        }

        public static String getPassword() {
            return PASSWORD;
        }
    }
}
