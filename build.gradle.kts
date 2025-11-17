/*
 * ===========================================
 * RuoYi-Cloud-Plus 根 build.gradle.kts
 * 定义所有子项目的通用配置
 * ===========================================
 */

plugins {
    // 应用 Java 库插件到所有子项目（在 subprojects 块中应用）
    id("java")
    // Spring Boot 插件（仅声明，不应用到根项目）
    alias(libs.plugins.spring.boot) apply false
}

// ===========================================
// 项目信息
// ===========================================
group = "org.dromara"
version = project.findProperty("version")?.toString() ?: "2.5.0"

// ===========================================
// 所有项目通用配置（包括根项目）
// ===========================================
allprojects {
    // 设置仓库（已在 settings.gradle.kts 中统一配置）
    // 这里不再重复设置
}

// ===========================================
// 所有子项目通用配置
// ===========================================
subprojects {
    // 项目组和版本
    group = rootProject.group
    version = rootProject.version

    // 只对非 BOM 模块应用 Java 插件（BOM 模块使用 java-platform 插件）
    if (!project.name.endsWith("-bom")) {
        apply(plugin = "java")
        apply(plugin = "java-library")

        // Java 编译配置
        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17

            // 启用 Java 编译参数
            withSourcesJar()
            withJavadocJar()
        }

        // 编译任务配置
        tasks.withType<JavaCompile> {
            options.encoding = "UTF-8"

            // 编译参数
            options.compilerArgs.addAll(
                listOf(
                    "-parameters",  // 保留参数名（Spring 需要）
                    "-Xlint:unchecked",
                    "-Xlint:deprecation"
                )
            )

            // 注解处理器配置（重要：顺序很关键！）
            // 1. therapi-javadoc（必须第一个）
            // 2. Lombok
            // 3. Spring Boot Configuration Processor
            // 4. MapStruct Plus
            // 5. Lombok MapStruct Binding（必须最后）
            options.annotationProcessorPath = configurations.getByName("annotationProcessor")
        }

        // 测试配置
        tasks.withType<Test> {
            useJUnitPlatform()

            // 测试 JVM 参数
            jvmArgs = listOf(
                "-Xmx1024m",
                "-XX:MaxMetaspaceSize=256m"
            )

            // 测试日志
            testLogging {
                events("passed", "skipped", "failed")
                showStandardStreams = false
            }
        }

        // Spring Boot JAR 打包配置（仅对应用了 Spring Boot 插件的模块生效）
        plugins.withId("org.springframework.boot") {
            tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
                // 处理重复的 JAR 文件
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
        }

        // 资源文件处理（替代 Maven 的 resource filtering）
        tasks.withType<ProcessResources> {
            // 设置字符编码
            filteringCharset = "UTF-8"

            // 对 application*.yml, bootstrap*.yml, logback*.xml 进行变量替换
            filesMatching(listOf("application*.yml", "bootstrap*.yml", "logback*.xml")) {
                // 定义项目属性映射
                val props = mapOf(
                    "project.version" to version.toString(),
                    "project.artifactId" to name,
                    "profiles.active" to (findProperty("profilesActive")?.toString() ?: "dev"),
                    "nacos.server" to (findProperty("nacosServer")?.toString() ?: "127.0.0.1:8848"),
                    "nacos.username" to (findProperty("nacosUsername")?.toString() ?: "nacos"),
                    "nacos.password" to (findProperty("nacosPassword")?.toString() ?: "nacos"),
                    "nacos.namespace" to (findProperty("nacosNamespace")?.toString() ?: ""),
                    "nacos.discovery.group" to (findProperty("nacosDiscoveryGroup")?.toString() ?: "DEFAULT_GROUP"),
                    "nacos.config.group" to (findProperty("nacosConfigGroup")?.toString() ?: "DEFAULT_GROUP")
                )

                // 使用 filter 而不是 expand 来实现部分替换
                filter { line ->
                    var result = line
                    props.forEach { (key, value) ->
                        result = result.replace("\${$key}", value)
                        result = result.replace("@$key@", value)  // 支持 Maven 风格的 @key@ 占位符
                    }
                    result
                }
            }
        }

        // 依赖配置
        dependencies {
            // ===========================================
            // BOM 依赖管理（按优先级顺序导入）
            // ===========================================
            // 使用 platform() 导入 BOM，让所有配置都能继承版本约束

            // 核心 BOM：Spring Boot（优先级最高）
            // 使用 DependencyHandler 的 add() 方法添加平台依赖到所有相关配置
            add("api", platform("org.springframework.boot:spring-boot-dependencies:3.5.6"))
            add("implementation", platform("org.springframework.boot:spring-boot-dependencies:3.5.6"))
            add("compileOnly", platform("org.springframework.boot:spring-boot-dependencies:3.5.6"))
            add("annotationProcessor", platform("org.springframework.boot:spring-boot-dependencies:3.5.6"))
            add("testImplementation", platform("org.springframework.boot:spring-boot-dependencies:3.5.6"))

            // Spring Cloud BOM
            add("api", platform("org.springframework.cloud:spring-cloud-dependencies:2025.0.0"))
            add("implementation", platform("org.springframework.cloud:spring-cloud-dependencies:2025.0.0"))
            add("testImplementation", platform("org.springframework.cloud:spring-cloud-dependencies:2025.0.0"))

            // Hutool BOM
            add("api", platform("cn.hutool:hutool-bom:5.8.40"))
            add("implementation", platform("cn.hutool:hutool-bom:5.8.40"))
            add("testImplementation", platform("cn.hutool:hutool-bom:5.8.40"))

            // Alibaba BOM（延迟解析项目依赖）
            add("api", platform(project(":ruoyi-common:ruoyi-common-alibaba-bom")))
            add("implementation", platform(project(":ruoyi-common:ruoyi-common-alibaba-bom")))
            add("testImplementation", platform(project(":ruoyi-common:ruoyi-common-alibaba-bom")))

            // 注意：ruoyi-common、ruoyi-api 的 BOM
            // 由各个子模块根据需要自行导入

            // ===========================================
            // 注解处理器（必须按此顺序！）
            // ===========================================
            annotationProcessor("com.github.therapi:therapi-runtime-javadoc-scribe:0.15.0")
            annotationProcessor("org.projectlombok:lombok:1.18.40")
            annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
            annotationProcessor("io.github.linpeilie:mapstruct-plus-processor:1.5.0")
            annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

            // compileOnly 依赖
            compileOnly("org.projectlombok:lombok:1.18.40")

            // 测试依赖
            testImplementation("org.springframework.boot:spring-boot-starter-test")
            testImplementation("org.junit.jupiter:junit-jupiter")
            testRuntimeOnly("org.junit.platform:junit-platform-launcher")

            // ===========================================
            // Netty 原生 DNS 解析器（平台特定依赖）
            // ===========================================
            // 解决 macOS 上的 DNS 解析警告和性能问题
            // 根据运行平台自动添加对应的原生库
            val osName = System.getProperty("os.name").lowercase()
            val osArch = System.getProperty("os.arch").lowercase()

            when {
                // macOS ARM64 (M1/M2/M3)
                osName.contains("mac") && (osArch.contains("aarch64") || osArch.contains("arm")) -> {
                    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.127.Final:osx-aarch_64")
                }
                // macOS x86_64
                osName.contains("mac") && osArch.contains("x86_64") -> {
                    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.127.Final:osx-x86_64")
                }
                // Linux x86_64 (生产环境)
                osName.contains("linux") && osArch.contains("amd64") -> {
                    runtimeOnly("io.netty:netty-resolver-dns-native-epoll:4.1.127.Final:linux-x86_64")
                }
            }
        }

        // Javadoc 配置
        tasks.withType<Javadoc> {
            options {
                encoding = "UTF-8"
                charset("UTF-8")
                (this as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
            }
        }
    }
}

// ===========================================
// 根项目任务
// ===========================================

tasks.register("cleanAll") {
    group = "build"
    description = "清理所有子项目的构建输出"

    dependsOn(subprojects.map { it.tasks.named("clean") })
}

tasks.register("buildAll") {
    group = "build"
    description = "构建所有子项目"

    dependsOn(subprojects.map { it.tasks.named("build") })
}

tasks.register("testAll") {
    group = "verification"
    description = "运行所有子项目的测试"

    dependsOn(subprojects.map { it.tasks.named("test") })
}

// ===========================================
// 项目特定配置（BOM 模块）
// ===========================================

// 为 BOM 模块应用 java-platform 插件
configure(subprojects.filter { it.name.endsWith("-bom") }) {
    apply(plugin = "java-platform")

    // BOM 模块只管理依赖版本，不包含代码
}

// ===========================================
// 欢迎信息
// ===========================================
println(
    """
    ╔═══════════════════════════════════════════════════════════╗
    ║                                                           ║
    ║       RuoYi-Cloud-Plus Gradle Build Configuration        ║
    ║                                                           ║
    ║   Version: ${version}                                   ║
    ║   Java: ${JavaVersion.current()}                                         ║
    ║   Gradle: ${gradle.gradleVersion}                                    ║
    ║                                                           ║
    ╚═══════════════════════════════════════════════════════════╝
""".trimIndent()
)
