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
    // Spotless 代码格式化插件
    id("com.diffplug.spotless") version "6.25.0"
    // Checkstyle 代码质量检查插件
    id("checkstyle")
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
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21

            // 启用 Java 编译参数
            withSourcesJar()
            withJavadocJar()
        }

        // 编译任务配置
        tasks.withType<JavaCompile> {
            options.encoding = "UTF-8"

            // 编译参数
            // 保留参数名（Spring 需要）
            options.compilerArgs.addAll(
                listOf(
                    "-parameters",
                    "-Xlint:unchecked",
                    "-Xlint:deprecation",
                ),
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
                "-XX:MaxMetaspaceSize=256m",
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

                // 解压 Netty 原生库（Spring Boot会在启动时解压到临时目录）
                // 这样 Netty 就能正确找到并加载原生库
                requiresUnpack("**/netty-transport-native-kqueue-*.jar")
                requiresUnpack("**/netty-resolver-dns-native-macos-*.jar")
            }

            // ===========================================
            // Netty 原生传输层和 DNS 解析器（通用 JAR 方案）
            // ===========================================
            // 为所有 Spring Boot 应用添加 Netty 原生传输层和 DNS 解析器
            // 解决 macOS 上的 DNS 解析警告，提升网络性能
            //
            // 说明：
            // - macOS 需要 kqueue 传输层 + DNS resolver 原生库
            // - Linux 不需要特定的 DNS resolver 原生库，但 epoll 传输层已自动包含
            // - Windows 同样不需要特定的原生库
            //
            // 通用 JAR 方案：打包所有平台的原生库，运行时自动选择
            // 优点：一次构建，到处运行（macOS 开发，Linux 生产无缝切换）
            // 缺点：JAR 体积增加约 1-2MB
            dependencies {
                val nettyVersion = rootProject.libs.versions.netty.get()

                // macOS: 添加 kqueue 传输层（DNS resolver 依赖它）
                // macOS ARM64 (M1/M2/M3/M4)
                add("runtimeOnly", "io.netty:netty-transport-native-kqueue:$nettyVersion:osx-aarch_64@jar")
                // macOS x86_64 (Intel Mac)
                add("runtimeOnly", "io.netty:netty-transport-native-kqueue:$nettyVersion:osx-x86_64@jar")

                // macOS: 添加 DNS 解析器原生库
                // macOS ARM64 (M1/M2/M3/M4)
                add("runtimeOnly", "io.netty:netty-resolver-dns-native-macos:$nettyVersion:osx-aarch_64@jar")
                // macOS x86_64 (Intel Mac)
                add("runtimeOnly", "io.netty:netty-resolver-dns-native-macos:$nettyVersion:osx-x86_64@jar")

                // Linux: 不需要添加，Netty 会自动使用标准 DNS 解析实现
                // epoll 传输层已通过传递依赖自动包含
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
                    "project.artifactId" to project.name,
                    "profiles.active" to (findProperty("profilesActive")?.toString() ?: "dev"),
                    "nacos.server" to (findProperty("nacosServer")?.toString() ?: "127.0.0.1:8848"),
                    "nacos.username" to (findProperty("nacosUsername")?.toString() ?: "nacos"),
                    "nacos.password" to (findProperty("nacosPassword")?.toString() ?: "nacos"),
                    "nacos.namespace" to (findProperty("nacosNamespace")?.toString() ?: ""),
                    "nacos.discovery.group" to (findProperty("nacosDiscoveryGroup")?.toString() ?: "DEFAULT_GROUP"),
                    "nacos.config.group" to (findProperty("nacosConfigGroup")?.toString() ?: "DEFAULT_GROUP"),
                )

                // 使用 filter 而不是 expand 来实现部分替换
                filter { line ->
                    var result = line
                    props.forEach { (key, value) ->
                        result = result.replace("\${$key}", value)
                        result = result.replace("@$key@", value) // 支持 Maven 风格的 @key@ 占位符
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

            // 获取版本号
            val springBootVersion = rootProject.libs.versions.springBoot.get()
            val springCloudVersion = rootProject.libs.versions.springCloud.get()
            val hutoolVersion = rootProject.libs.versions.hutool.get()

            // 核心 BOM：Spring Boot（优先级最高）
            // 使用 DependencyHandler 的 add() 方法添加平台依赖到所有相关配置
            add("api", platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
            add("implementation", platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
            add("compileOnly", platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
            add("annotationProcessor", platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
            add("testImplementation", platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))

            // Spring Cloud BOM
            add("api", platform("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion"))
            add("implementation", platform("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion"))
            add(
                "testImplementation",
                platform("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion"),
            )

            // Hutool BOM
            add("api", platform("cn.hutool:hutool-bom:$hutoolVersion"))
            add("implementation", platform("cn.hutool:hutool-bom:$hutoolVersion"))
            add("testImplementation", platform("cn.hutool:hutool-bom:$hutoolVersion"))

            // Alibaba BOM（延迟解析项目依赖）
            add("api", platform(project(":ruoyi-common:ruoyi-common-alibaba-bom")))
            add("implementation", platform(project(":ruoyi-common:ruoyi-common-alibaba-bom")))
            add("testImplementation", platform(project(":ruoyi-common:ruoyi-common-alibaba-bom")))

            // 注意：ruoyi-common、ruoyi-api 的 BOM
            // 由各个子模块根据需要自行导入

            // ===========================================
            // 注解处理器（必须按此顺序！）
            // ===========================================
            annotationProcessor(rootProject.libs.therapi.javadoc.scribe)
            annotationProcessor(rootProject.libs.lombok)
            annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
            annotationProcessor("io.github.linpeilie:mapstruct-plus-processor:${rootProject.libs.versions.mapstructPlus.get()}")
            annotationProcessor(rootProject.libs.lombok.mapstruct.binding)

            // ===========================================
            // compileOnly 依赖
            // ===========================================
            compileOnly(rootProject.libs.lombok)

            // ===========================================
            // 测试依赖
            // ===========================================
            testImplementation("org.springframework.boot:spring-boot-starter-test")
            testImplementation("org.junit.jupiter:junit-jupiter")
            testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
// Spotless 代码格式化配置
// ===========================================

// 应用 Spotless 到所有子项目
allprojects {
    apply(plugin = "com.diffplug.spotless")

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        // Java 格式化 (Google Java Format)
        java {
            target("src/**/*.java")
            targetExclude("**/build/**", "**/target/**", "**/.gradle/**")

            // 使用 Google Java Format (标准 2 空格缩进)
            googleJavaFormat("1.19.2").reflowLongStrings()

            // 导入顺序
            importOrder()
            removeUnusedImports()

            // 行尾空格
            trimTrailingWhitespace()
            endWithNewline()
        }

        // Gradle Kotlin DSL 格式化
        kotlinGradle {
            target("*.gradle.kts", "**/*.gradle.kts")
            targetExclude("**/build/**")
            ktlint("1.0.1").editorConfigOverride(
                mapOf(
                    // 禁用行内注释位置检查（对于参数列表中的注释过于严格）
                    "ktlint_standard_discouraged-comment-location" to "disabled",
                ),
            )
        }

        // XML 格式化（简化版，仅处理空白字符）
        format("xml") {
            target("src/**/*.xml")
            targetExclude("**/build/**", "**/target/**")
            // 只做基本的空白字符处理，不进行复杂的格式化
            trimTrailingWhitespace()
            endWithNewline()
            // 统一缩进为 2 空格
            replaceRegex("XML indentation", "\t", "  ")
        }

        // YAML 格式化（简化版，仅处理空白字符）
        format("yaml") {
            target("src/**/*.yml", "src/**/*.yaml", "*.yml", "*.yaml")
            targetExclude("**/build/**", "**/target/**")
            // 只做基本的空白字符处理，不进行复杂的格式化
            trimTrailingWhitespace()
            endWithNewline()
            // 统一缩进为 2 空格
            replaceRegex("YAML indentation", "\t", "  ")
        }
    }
}

// 自动在构建时运行 spotlessApply
subprojects {
    // 只对应用了 Java 插件的项目添加 spotlessApply 依赖
    plugins.withId("java") {
        tasks.named("compileJava") {
            dependsOn("spotlessApply")
        }
    }
}

// ===========================================
// Checkstyle 代码质量检查配置
// ===========================================

// 应用 Checkstyle 到所有子项目（排除 BOM 和 example 模块）
subprojects {
    // 只对应用了 Java 插件的项目应用 Checkstyle
    plugins.withId("java") {
        // 排除示例/演示模块
        if (!project.name.contains("demo") && !project.name.contains("example")) {
            apply(plugin = "checkstyle")

            configure<CheckstyleExtension> {
                // 使用 Google Java Style
                toolVersion = "12.1.2"
                configFile = rootProject.file("config/checkstyle/google_checks.xml")

                // 只检查 main 源代码，不检查 test
                sourceSets = listOf(project.extensions.getByType<SourceSetContainer>()["main"])

                // 忽略失败（如果需要严格模式，设为 false）
                // 设置为 true 允许编译成功，但保留警告信息
                isIgnoreFailures = true

                // 最大警告数（0 表示不允许任何警告）
                maxWarnings = 0

                // 最大错误数（0 表示不允许任何错误）
                maxErrors = 0
            }

            // 配置 Checkstyle 任务
            tasks.withType<Checkstyle> {
                reports {
                    // 生成 HTML 报告
                    html.required.set(true)
                    html.outputLocation.set(file("build/reports/checkstyle/main.html"))

                    // 生成 XML 报告（用于 CI 集成）
                    xml.required.set(true)
                    xml.outputLocation.set(file("build/reports/checkstyle/main.xml"))
                }
            }

            // 让 check 任务依赖 checkstyleMain（自动在 build 时运行）
            tasks.named("check") {
                dependsOn("checkstyleMain")
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
    ║   Version: $version                                   ║
    ║   Java: ${JavaVersion.current()}                                         ║
    ║   Gradle: ${gradle.gradleVersion}                                    ║
    ║                                                           ║
    ╚═══════════════════════════════════════════════════════════╝
    """.trimIndent(),
)
