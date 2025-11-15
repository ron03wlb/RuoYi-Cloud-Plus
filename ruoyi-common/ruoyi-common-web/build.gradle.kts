/*
 * ===========================================
 * ruoyi-common-web
 * web服务
 * ===========================================
 */

plugins {
    id("jacoco")
}

description = "ruoyi-common-web web服务"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-json"))

    // ===========================================
    // Spring Boot Web
    // ===========================================
    api("org.springframework.boot:spring-boot-starter-web") {
        // 排除 Tomcat，使用 Undertow
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }

    // Undertow（性能更强的 Web 容器）
    api("org.springframework.boot:spring-boot-starter-undertow")

    // ===========================================
    // Spring Boot Actuator
    // ===========================================
    api("org.springframework.boot:spring-boot-starter-actuator")

    // ===========================================
    // Mica Metrics
    // ===========================================
    api(libs.mica.metrics) {
        exclude(group = "net.dreamlu", module = "mica-core")
    }

    // ===========================================
    // 可选依赖（compileOnly）
    // ===========================================
    // Nacos Discovery（可选）
    compileOnly("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")

    // Mica Core（provided）
    compileOnly(libs.mica.core)

    // ===========================================
    // 测试依赖
    // ===========================================
    // JUnit
    testImplementation("org.junit.jupiter:junit-jupiter")

    // AssertJ
    testImplementation("org.assertj:assertj-core")

    // Mockito
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
}

// ===========================================
// JaCoCo 配置
// ===========================================
jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    // 排除不需要覆盖的类
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/filter/**",                  // Servlet过滤器（依赖Spring）
                    "**/handler/**",                 // 全局异常处理器（依赖Spring）
                    "**/config/**",                  // Spring配置类
                    "**/XssHttpServletRequestWrapper.class", // Servlet包装器
                    "**/*Application.class"          // 主程序
                )
            }
        })
    )
}
