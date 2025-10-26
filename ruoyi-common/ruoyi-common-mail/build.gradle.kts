/*
 * ===========================================
 * ruoyi-common-mail
 * 邮件模块
 * ===========================================
 */

description = "ruoyi-common-mail 邮件模块"

dependencies {
    // ===========================================
    // 项目内依赖
    // ===========================================
    api(project(":ruoyi-common:ruoyi-common-core"))

    // ===========================================
    // Jakarta Mail
    // ===========================================
    api("jakarta.mail:jakarta.mail-api")
    api("org.eclipse.angus:jakarta.mail")
}
