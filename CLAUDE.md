# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Quick Start for Local Development

### For Developers (Recommended)

Run infrastructure in Docker, develop services in IDE:

```bash
# 1. Start infrastructure services
cd script/docker
./dev-start.sh                  # Use official Nacos (recommended, fastest)
# or
./dev-start.sh --custom-nacos   # Use custom-built Nacos

# 2. Follow the prompts to:
#    - Import Nacos configurations at http://localhost:8848/nacos
#      (or use ./import-nacos-config.sh for automatic import)
#    - Initialize database using scripts in script/sql/
#    - Run business services in IntelliJ IDEA using .run/ configs
```

### For Full Docker Deployment

Build and run everything in Docker:

```bash
cd script/docker
./build-and-deploy.sh all
```

See `script/docker/README-LOCAL-BUILD.md` for detailed deployment instructions.

## Project Overview

RuoYi-Cloud-Plus is a Spring Cloud microservice system based on RuoYi-Cloud, featuring a completely rewritten architecture with modern tech stack. This is a production-ready enterprise microservice platform with multi-tenancy, RBAC permission system, workflow engine, and comprehensive business modules.

**Tech Stack:**
- Java 17/21, Spring Boot 3.5.6, Spring Cloud 2025.0.0
- Microservices: Nacos (registry/config), Dubbo 3.X (RPC), Gateway, Seata (distributed transactions)
- Authentication: Sa-Token + JWT
- Database: PostgreSQL 17, MyBatis-Plus 3.5.14, HikariCP, P6Spy (SQL monitoring), Dynamic-Datasource (multi-DB)
- Cache: Redis 5-7 + Redisson
- Message Queue: RocketMQ, RabbitMQ, Kafka
- Job Scheduling: SnailJob
- Workflow: Warm-Flow
- Search: ElasticSearch + Easy-Es
- Monitoring: Prometheus, Grafana, Skywalking, Spring Boot Admin
- Storage: MinIO (S3 compatible)
- Documentation: SpringDoc (OpenAPI)

## Project Structure

```
RuoYi-Cloud-Plus/
├── ruoyi-auth/              # Authentication & Authorization service (port 9210)
├── ruoyi-gateway/           # API Gateway (port 8080)
├── ruoyi-modules/           # Business modules
│   ├── ruoyi-system/        # System management service (port 9201)
│   ├── ruoyi-gen/           # Code generator service (port 9202)
│   ├── ruoyi-job/           # Job scheduling service (port 9203)
│   ├── ruoyi-resource/      # Resource management service (port 9204)
│   └── ruoyi-workflow/      # Workflow service (port 9205)
├── ruoyi-api/               # Dubbo API interfaces
│   ├── ruoyi-api-system/    # System service API
│   ├── ruoyi-api-resource/  # Resource service API
│   └── ruoyi-api-workflow/  # Workflow service API
├── ruoyi-common/            # Common libraries (plugin-based architecture)
│   ├── ruoyi-common-core/   # Core utilities
│   ├── ruoyi-common-web/    # Web layer support
│   ├── ruoyi-common-satoken/     # Sa-Token authentication
│   ├── ruoyi-common-mybatis/     # MyBatis-Plus integration
│   ├── ruoyi-common-redis/       # Redis/Redisson integration
│   ├── ruoyi-common-dubbo/       # Dubbo integration
│   ├── ruoyi-common-tenant/      # Multi-tenancy support
│   ├── ruoyi-common-security/    # Security components
│   ├── ruoyi-common-translation/ # Data translation
│   ├── ruoyi-common-sensitive/   # Data desensitization
│   ├── ruoyi-common-encrypt/     # Data encryption/decryption
│   ├── ruoyi-common-excel/       # Excel import/export
│   ├── ruoyi-common-oss/         # Object storage (S3/MinIO)
│   ├── ruoyi-common-idempotent/  # Distributed idempotence
│   ├── ruoyi-common-ratelimiter/ # Rate limiting
│   └── ruoyi-common-log/         # Logging support
├── ruoyi-visual/            # Visual monitoring services
│   ├── ruoyi-monitor/       # Spring Boot Admin monitoring (port 9100)
│   ├── ruoyi-nacos/         # Nacos server (port 8848)
│   ├── ruoyi-seata-server/  # Seata server (port 8091)
│   └── ruoyi-snailjob-server/ # SnailJob server (port 8800)
├── ruoyi-example/           # Example modules
│   ├── ruoyi-demo/          # Feature demonstration
│   └── ruoyi-test-mq/       # Message queue testing
└── script/                  # Deployment scripts
    ├── docker/              # Docker Compose configurations
    ├── sql/                 # Database initialization scripts
    └── config/              # Configuration templates (Nacos, Grafana)
```

## Architecture Principles

### Plugin-Based Architecture
The project uses a plugin-based design where common features are packaged as independent modules in `ruoyi-common/`. Services only import the plugins they need, reducing coupling and improving modularity.

### Service Communication
- **Dubbo RPC**: Inter-service synchronous calls via Dubbo 3.X interfaces defined in `ruoyi-api/`
- **Message Queue**: Asynchronous communication via RocketMQ/Kafka/RabbitMQ
- **Gateway**: All external requests go through `ruoyi-gateway` for routing, authentication, and rate limiting

### Data Architecture
- **Multi-Tenancy**: Automatic tenant isolation via MyBatis-Plus plugin (`ruoyi-common-tenant`)
- **Multi-Datasource**: Dynamic datasource switching via `@DS` annotation
- **Data Permissions**: Row-level data filtering via MyBatis-Plus data permissions plugin
- **Data Translation**: Automatic field translation (e.g., user_id → username) via `@Translation` annotation
- **Data Desensitization**: Sensitive data masking via `@Sensitive` annotation during JSON serialization
- **Data Encryption**: Automatic encryption/decryption via `@EncryptField` annotation

### Typical Service Structure
```
ruoyi-modules/ruoyi-{module}/
├── src/main/java/org/dromara/{module}/
│   ├── {Module}Application.java    # Spring Boot entry point
│   ├── controller/                 # REST controllers
│   ├── service/                    # Service interfaces
│   │   └── impl/                   # Service implementations
│   ├── mapper/                     # MyBatis-Plus mappers
│   ├── domain/                     # Entity/DTO/VO classes
│   │   ├── bo/                     # Business Objects
│   │   └── vo/                     # View Objects
│   ├── dubbo/                      # Dubbo service implementations
│   └── listener/                   # Event listeners
└── src/main/resources/
    ├── application.yml             # Local config (mostly placeholders)
    ├── bootstrap.yml               # Bootstrap config (Nacos connection)
    └── mapper/                     # MyBatis XML mappers
```

## Build & Development Commands

### Prerequisites
- JDK 17 or 21
- Gradle 8.12+ (included via Gradle Wrapper)
- Running Nacos server (config and registry)
- Running Redis
- Running PostgreSQL 17

### Gradle Profiles
The project uses Gradle for building. Configuration properties can be set via:
- `gradle.properties`: Project-level configuration
- Command-line properties: `-PprofilesActive=prod`
- Environment variables

Profile settings control:
- `profilesActive`: Environment identifier (dev/prod), default: dev
- `nacosServer`: Nacos server address (default: 127.0.0.1:8848)
- `nacosUsername/password`: Nacos credentials (default: nacos/nacos)
- `nacosNamespace`: Nacos namespace (default: empty)

### Build Commands

```bash
# Build all modules (skips tests by default)
./gradlew build -x test

# Build with specific profile
./gradlew build -x test -PprofilesActive=prod

# Build specific module
./gradlew :ruoyi-modules:ruoyi-system:build -x test

# Build and run tests
./gradlew build

# Clean build
./gradlew clean build -x test

# View all available tasks
./gradlew tasks

# View project structure
./gradlew projects
```

### Docker Deployment (Local Build)

The project includes Dockerfiles for all services. Use `docker-compose-build.yml` to build images from local source code instead of pulling from remote registry.

#### Quick Start Script

Use the provided script for easy deployment:

```bash
cd script/docker

# Build and start all services (infrastructure + business services)
./build-and-deploy.sh all

# Only start infrastructure services (PostgreSQL, Redis, Nacos, MinIO)
./build-and-deploy.sh infra

# Build and start only business services (assumes infra is running)
./build-and-deploy.sh services

# Build and start a specific service
./build-and-deploy.sh ruoyi-gateway
./build-and-deploy.sh ruoyi-auth
./build-and-deploy.sh ruoyi-system

# View service status
./build-and-deploy.sh status

# View logs (all services or specific service)
./build-and-deploy.sh logs
./build-and-deploy.sh logs ruoyi-gateway

# Stop all services
./build-and-deploy.sh stop
```

#### Manual Docker Compose Commands

```bash
cd script/docker

# Build and start infrastructure only
docker-compose -f docker-compose-build.yml up -d postgres redis nacos minio

# Build and start all services
docker-compose -f docker-compose-build.yml up -d --build

# Start specific service
docker-compose -f docker-compose-build.yml up -d ruoyi-gateway

# View logs
docker-compose -f docker-compose-build.yml logs -f ruoyi-gateway

# Stop and remove all containers
docker-compose -f docker-compose-build.yml down

# Rebuild a specific service
docker-compose -f docker-compose-build.yml build ruoyi-auth
docker-compose -f docker-compose-build.yml up -d ruoyi-auth
```

#### Important: Nacos Configuration Import

After starting Nacos for the first time:
1. Access Nacos console at http://localhost:8848/nacos (username: `nacos`, password: `nacos`)
2. Manually import all configuration files from `script/config/nacos/` directory
3. Configuration files to import:
   - `application-common.yml` - Shared configuration for all services
   - `ruoyi-gateway.yml` - Gateway routing configuration
   - `ruoyi-auth.yml` - Auth service configuration
   - `ruoyi-system.yml` - System service configuration
   - And other service-specific configurations as needed

### Running Services Locally (Without Docker)

#### Service Startup Order

Services should be started in this order:

1. **Infrastructure**: PostgreSQL, Redis, Nacos (can use Docker for these)
2. `ruoyi-nacos` - If running locally (port 8848)
3. `ruoyi-seata-server` - If using distributed transactions (port 8091)
4. `ruoyi-snailjob-server` - If using scheduled jobs (port 8800)
5. `ruoyi-gateway` - API Gateway (port 8080)
6. `ruoyi-auth` - Authentication service (port 9210)
7. `ruoyi-system` - System management (port 9201)
8. `ruoyi-gen` - Code generator (port 9202)
9. `ruoyi-job` - Job management (port 9203)
10. `ruoyi-resource` - Resource management (port 9204)
11. `ruoyi-workflow` - Workflow engine (port 9205)
12. `ruoyi-monitor` - Spring Boot Admin (port 9100) - Optional

#### Running Locally

```bash
# Option 1: Using Gradle
cd ruoyi-{module}
../../gradlew bootRun

# Option 2: Using compiled JAR
cd ruoyi-{module}
java -jar build/libs/ruoyi-{module}.jar

# Option 3: Using IntelliJ IDEA
# Use the pre-configured run configurations in .run/ directory

# Option 4: Mixed mode - Infrastructure in Docker, services locally
cd script/docker
docker-compose -f docker-compose-build.yml up -d postgres redis nacos minio
# Then run services locally using Gradle or IDEA
```

### Testing

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :ruoyi-modules:ruoyi-system:test

# Run tests with specific tags (controlled by @Tag annotation)
./gradlew test --tests "*DevTest"  # Only runs tests with "DevTest" in name
./gradlew test --tests "*ProdTest" # Only runs tests with "ProdTest" in name

# Skip specific tests
./gradlew test --exclude-task :ruoyi-example:test

# Run tests with detailed output
./gradlew test --info
```

### Code Generation

The project includes a code generator service that generates CRUD code from database tables:

1. Access code generation service at http://localhost:9202 (via gateway: http://localhost:8080/code/...)
2. Or use the frontend interface to design tables and generate code
3. Generated code follows project conventions:
   - Controllers with SpringDoc annotations
   - Services with interface + implementation
   - MyBatis-Plus mappers and entities
   - Excel import/export support
   - Data permissions, multi-tenancy support

## Configuration Management

### Nacos Configuration
All service configurations are managed in Nacos. Configuration files are in `script/config/nacos/`:
- `application-common.yml`: Shared configuration for all services
- `ruoyi-{module}.yml`: Module-specific configuration
- `ruoyi-gateway.yml`: Gateway routing and filters

After starting Nacos, manually import these configurations to the Nacos console.

### Local Configuration Override
Each service has `bootstrap.yml` which connects to Nacos and specifies which config to load:
```yaml
spring:
  cloud:
    nacos:
      server-addr: ${nacos.server}
      config:
        namespace: ${nacos.namespace:}
        group: ${nacos.config.group}
        file-extension: yml
```

## Database Initialization

Database scripts are in `script/sql/`:

- Execute PostgreSQL SQL scripts from `script/sql/` directory:
    - `ry_cloud.sql` - Main application database
    - `ry_job.sql` - Job scheduling database
    - `ry_seata.sql` - Seata distributed transaction database
    - `ry_workflow.sql` - Workflow engine database
- For demo module, also execute `ruoyi-example/ruoyi-demo/test.sql`
- Archived MySQL and Oracle scripts are in `script/sql/archive/` for reference

## Key Development Patterns

### Controller Layer
- Use SpringDoc (`@Operation`, `@Tag`) for API documentation instead of Swagger annotations
- Use `@SaCheckPermission` for permission control
- Use `@Log` annotation for operation logging
- Use `@RateLimiter` for rate limiting
- Use `@RepeatSubmit` for idempotent operations
- Return `R<T>` (unified response wrapper)

### Service Layer
- Define interface in `service/` package
- Implement in `service/impl/` with `@Service` annotation
- Use `@Transactional` for transaction management
- Use `@DS("datasource")` to switch datasource
- For distributed transactions, use `@GlobalTransactional` (Seata)

### Mapper Layer
- Extend `BaseMapperPlus<T, V>` instead of `BaseMapper<T>`
- XML mappers go in `src/main/resources/mapper/`
- Use MyBatis-Plus QueryWrapper or LambdaQueryWrapper
- Data permissions are automatically applied via `@DataPermission` annotation

### Entity/DTO/VO Design
- Entities in `domain/`: Correspond to database tables, use `@TableName`
- Business Objects (BO) in `domain/bo/`: Input DTOs for business logic
- View Objects (VO) in `domain/vo/`: Output DTOs for presentation
- Use MapStruct Plus (`@AutoMapper`) for object conversions

### Dubbo API Design
- Define Dubbo interfaces in `ruoyi-api/{module}/` modules
- Implement in corresponding service module under `dubbo/` package
- Use `@DubboService` for provider, `@DubboReference` for consumer

### Multi-Tenancy
- Enable by including `ruoyi-common-tenant` dependency
- Automatic tenant_id column filtering for all queries/updates
- Use `@IgnoreTenant` to bypass tenant isolation for specific methods

### Distributed Features
- **Distributed Lock**: Use `@Lock4j(keys = "#key")` annotation
- **Distributed Cache**: Use Spring Cache annotations with Redis backing
- **Distributed ID**: Snowflake IDs via `@TableId(type = IdType.INPUT)`
- **Distributed Rate Limiting**: Use Sentinel via `@SentinelResource`

## Common Tasks

### Adding a New Module
1. Create module directory under `ruoyi-modules/`
2. Copy `pom.xml` from existing module and adjust
3. Create main application class with `@SpringBootApplication` and `@EnableDubbo`
4. Create `application.yml` and `bootstrap.yml`
5. Add module to parent `pom.xml` `<modules>` section
6. Add corresponding config to Nacos
7. Register service startup configuration in `.run/`

### Adding a New Dubbo API
1. Define interface in `ruoyi-api/{module}/` with `@DubboService` metadata
2. Implement in service module's `dubbo/` package with `@DubboService`
3. Consumer injects with `@DubboReference`

### Adding New Common Plugin
1. Create module under `ruoyi-common/ruoyi-common-{feature}/`
2. Add dependency in `ruoyi-common-bom/pom.xml`
3. Create auto-configuration class with `@Configuration`
4. Add `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
5. Services import the plugin as needed

## Monitoring & Observability

- **Spring Boot Admin**: http://localhost:9100
- **Nacos Console**: http://localhost:8848/nacos
- **Skywalking UI**: http://localhost:18080 (if enabled)
- **Prometheus**: http://localhost:9090 (if enabled)
- **Grafana**: http://localhost:3000 (if enabled, default pwd: 123456)
- **Seata Console**: http://localhost:7091 (if enabled)
- **SnailJob Console**: http://localhost:8800 (if enabled)

## Documentation & Resources

- Official Documentation: https://plus-doc.dromara.org
- Initialization Guide: https://plus-doc.dromara.org/#/ruoyi-cloud-plus/quickstart/init
- Deployment Guide: https://plus-doc.dromara.org/#/ruoyi-cloud-plus/quickstart/deploy
- Frontend Repository: https://gitee.com/JavaLionLi/plus-ui

## Important Notes

- Follow Alibaba Java Coding Guidelines
- Use `Hutool` and `Lombok` to reduce boilerplate code
- Use `P6Spy` SQL logging to debug database issues (check console for complete SQL)
- Redis keys use colon-separated namespacing: `project:module:function:key`
- All dates use `LocalDateTime`, not `Date`
- Use `Optional` return types instead of null where appropriate
- Always check tenant context when dealing with multi-tenant data
