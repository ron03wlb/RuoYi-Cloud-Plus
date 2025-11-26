package org.dromara.common.test.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Sa-Token 测试配置
 *
 * <p>为测试环境提供内存存储的 SaTokenDao 实现
 *
 * @author Lion Li
 * @since 2025-11-11
 */
@TestConfiguration
public class TestSaTokenConfig {

    /**
     * 提供内存存储的 SaTokenDao
     *
     * <p>使用默认实现（基于 ConcurrentHashMap）以在测试中持久化 Session 数据
     */
    @Bean
    @Primary
    public SaTokenDao saTokenDao() {
        return new SaTokenDaoDefaultImpl();
    }
}
