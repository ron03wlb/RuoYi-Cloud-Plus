# Phase 2: ruoyi-auth 模块测试计划

> **模块:** ruoyi-auth
> **优先级:** P0 (最高)
> **覆盖率目标:** 95% (安全关键模块)
> **预计工作量:** 3天
> **创建日期:** 2025-11-03

---

## 📋 模块概览

### 模块职责

ruoyi-auth 是系统的认证中心，负责：

- 用户登录认证（多种方式）
- Token 生成和管理
- 验证码生成和校验
- 用户注册
- 租户管理

### 技术架构

- **设计模式:** 策略模式 (IAuthStrategy)
- **认证框架:** Sa-Token + JWT
- **验证码:** AJ-Captcha (滑动验证码、点选验证码)
- **多租户:** 支持租户隔离

---

## 🎯 测试目标

### 覆盖率目标

| 指标        | 目标   | 优先级 |
|-----------|------|-----|
| **指令覆盖率** | 95%  | P0  |
| **分支覆盖率** | 90%  | P0  |
| **方法覆盖率** | 95%  | P0  |
| **类覆盖率**  | 100% | P0  |

### 安全测试重点

- ✅ 密码安全（加密、强度校验）
- ✅ Token 安全（签名、过期、刷新）
- ✅ 验证码防护（防机器人）
- ✅ 防暴力破解（失败次数限制）
- ✅ 会话管理（单设备/多设备登录）
- ✅ 租户隔离（多租户数据安全）

---

## 📦 测试任务清单

### 1. Controller 层测试

#### 1.1 TokenController (高优先级)

**测试类:** `TokenControllerTest` (集成测试)

**测试场景:**

##### 登录功能

- [ ] **密码登录成功**
    - [ ] 正确的用户名和密码
    - [ ] 验证 Token 生成
    - [ ] 验证用户信息返回
    - [ ] 验证登录日志记录

- [ ] **密码登录失败**
    - [ ] 错误的密码
    - [ ] 不存在的用户
    - [ ] 账号被锁定
    - [ ] 账号被禁用
    - [ ] 验证码错误

- [ ] **邮箱登录**
    - [ ] 邮箱验证码登录成功
    - [ ] 验证码过期
    - [ ] 验证码错误

- [ ] **短信登录**
    - [ ] 手机验证码登录成功
    - [ ] 验证码过期
    - [ ] 验证码错误

- [ ] **社交登录**
    - [ ] 微信登录
    - [ ] QQ登录
    - [ ] 第三方授权失败

- [ ] **小程序登录**
    - [ ] 微信小程序登录
    - [ ] code 无效

##### 租户相关

- [ ] **租户列表查询**
    - [ ] 获取可用租户列表
    - [ ] 租户过滤（启用/禁用）

- [ ] **租户切换**
    - [ ] 切换到不同租户
    - [ ] 验证租户上下文更新

##### Token 管理

- [ ] **Token 刷新**
    - [ ] 使用 refresh_token 刷新
    - [ ] refresh_token 过期
    - [ ] refresh_token 无效

- [ ] **登出**
    - [ ] 登出成功
    - [ ] Token 失效
    - [ ] 清除缓存

##### 注册功能

- [ ] **用户注册**
    - [ ] 注册成功
    - [ ] 用户名已存在
    - [ ] 邮箱已存在
    - [ ] 手机号已存在
    - [ ] 密码强度不足
    - [ ] 验证码错误

##### 安全测试

- [ ] **防暴力破解**
    - [ ] 连续失败5次后锁定
    - [ ] 锁定时间验证（30分钟）
    - [ ] 锁定后的登录尝试

- [ ] **并发登录控制**
    - [ ] 单设备登录（踢出旧会话）
    - [ ] 多设备登录（允许多会话）

- [ ] **参数校验**
    - [ ] 空用户名
    - [ ] 空密码
    - [ ] SQL注入尝试
    - [ ] XSS攻击尝试

#### 1.2 CaptchaController

**测试类:** `CaptchaControllerTest` (集成测试)

**测试场景:**

- [ ] **生成验证码**
    - [ ] 生成滑动验证码
    - [ ] 生成点选验证码
    - [ ] 生成数学验证码
    - [ ] 验证Redis缓存

- [ ] **校验验证码**
    - [ ] 正确的验证码
    - [ ] 错误的验证码
    - [ ] 验证码过期
    - [ ] 验证码已使用

- [ ] **验证码类型切换**
    - [ ] MATH (数学验证码)
    - [ ] CHAR (字符验证码)
    - [ ] BLOCK_PUZZLE (滑动拼图)
    - [ ] CLICK_WORD (点选文字)

---

### 2. Service 层测试

#### 2.1 SysLoginService (核心服务)

**测试类:** `SysLoginServiceTest` (单元测试 + 集成测试)

**测试场景:**

- [ ] **登录流程**
    - [ ] 策略模式路由测试
    - [ ] 密码登录策略调用
    - [ ] 邮箱登录策略调用
    - [ ] 短信登录策略调用
    - [ ] 社交登录策略调用
    - [ ] 小程序登录策略调用

- [ ] **Token 生成**
    - [ ] 生成 access_token
    - [ ] 生成 refresh_token
    - [ ] Token 包含用户信息
    - [ ] Token 包含权限信息
    - [ ] Token 有效期验证

- [ ] **登录日志**
    - [ ] 记录成功登录
    - [ ] 记录失败登录
    - [ ] 记录登录IP
    - [ ] 记录登录时间
    - [ ] 记录登录方式

- [ ] **租户处理**
    - [ ] 租户信息加载
    - [ ] 租户状态校验
    - [ ] 租户过期检查
    - [ ] 租户包套餐校验

#### 2.2 PasswordAuthStrategy (密码登录策略)

**测试类:** `PasswordAuthStrategyTest` (单元测试)

**测试场景:**

- [ ] **密码校验**
    - [ ] 正确密码验证
    - [ ] 错误密码验证
    - [ ] 密码加密验证（BCrypt）
    - [ ] 空密码处理

- [ ] **失败次数统计**
    - [ ] 失败次数递增
    - [ ] Redis 缓存失败次数
    - [ ] 达到上限后锁定
    - [ ] 锁定时间验证

- [ ] **用户状态校验**
    - [ ] 正常用户
    - [ ] 禁用用户
    - [ ] 锁定用户
    - [ ] 过期用户

- [ ] **密码强度**
    - [ ] 弱密码检测
    - [ ] 强制修改密码
    - [ ] 密码过期检查

#### 2.3 EmailAuthStrategy (邮箱登录策略)

**测试类:** `EmailAuthStrategyTest` (单元测试)

**测试场景:**

- [ ] **邮箱验证码校验**
    - [ ] 正确验证码
    - [ ] 错误验证码
    - [ ] 验证码过期
    - [ ] 验证码使用次数限制

- [ ] **邮箱格式验证**
    - [ ] 有效邮箱格式
    - [ ] 无效邮箱格式
    - [ ] 邮箱不存在

#### 2.4 SmsAuthStrategy (短信登录策略)

**测试类:** `SmsAuthStrategyTest` (单元测试)

**测试场景:**

- [ ] **短信验证码校验**
    - [ ] 正确验证码
    - [ ] 错误验证码
    - [ ] 验证码过期
    - [ ] 验证码使用次数限制

- [ ] **手机号格式验证**
    - [ ] 有效手机号
    - [ ] 无效手机号
    - [ ] 手机号不存在

- [ ] **频率限制**
    - [ ] 1分钟内不能重复发送
    - [ ] 1小时内最多发送5次

#### 2.5 SocialAuthStrategy (社交登录策略)

**测试类:** `SocialAuthStrategyTest` (单元测试)

**测试场景:**

- [ ] **第三方授权**
    - [ ] 微信授权回调
    - [ ] QQ授权回调
    - [ ] 钉钉授权回调
    - [ ] 授权失败处理

- [ ] **用户绑定**
    - [ ] 首次登录自动注册
    - [ ] 已绑定用户登录
    - [ ] 绑定多个社交账号
    - [ ] 解绑社交账号

- [ ] **用户信息同步**
    - [ ] 同步头像
    - [ ] 同步昵称
    - [ ] 同步性别

#### 2.6 XcxAuthStrategy (小程序登录策略)

**测试类:** `XcxAuthStrategyTest` (单元测试)

**测试场景:**

- [ ] **微信小程序登录**
    - [ ] code 换取 session_key
    - [ ] 解密用户信息
    - [ ] code 无效
    - [ ] code 过期

- [ ] **手机号授权**
    - [ ] 解密手机号
    - [ ] 手机号绑定

---

### 3. Domain 层测试

#### 3.1 Form 对象校验

**测试场景:**

- [ ] **RegisterBody**
    - [ ] @NotBlank 校验
    - [ ] @Size 校验
    - [ ] @Email 校验
    - [ ] @Pattern 校验（用户名、密码）

- [ ] **PasswordLoginBody**
    - [ ] username 必填
    - [ ] password 必填
    - [ ] tenantId 可选
    - [ ] code 可选（验证码）

- [ ] **EmailLoginBody**
    - [ ] email 必填且格式正确
    - [ ] emailCode 必填

- [ ] **SmsLoginBody**
    - [ ] phonenumber 必填且格式正确
    - [ ] smsCode 必填

- [ ] **SocialLoginBody**
    - [ ] source 必填
    - [ ] socialCode 必填
    - [ ] socialState 必填

- [ ] **XcxLoginBody**
    - [ ] xcxCode 必填
    - [ ] encryptedData 可选
    - [ ] iv 可选

#### 3.2 VO 对象

**测试场景:**

- [ ] **LoginVo**
    - [ ] access_token 字段
    - [ ] refresh_token 字段
    - [ ] expire_in 字段
    - [ ] user_info 字段

- [ ] **CaptchaVo**
    - [ ] captchaOnOff 字段
    - [ ] uuid 字段
    - [ ] img 字段（Base64）

---

### 4. Config 层测试

#### 4.1 CaptchaConfig

**测试类:** `CaptchaConfigTest` (集成测试)

**测试场景:**

- [ ] **验证码配置加载**
    - [ ] 验证码类型配置
    - [ ] 验证码有效期配置
    - [ ] 验证码开关配置

- [ ] **验证码Bean初始化**
    - [ ] CaptchaService Bean 创建
    - [ ] CaptchaProperties Bean 创建

---

### 5. Properties 层测试

#### 5.1 UserPasswordProperties

**测试类:** `UserPasswordPropertiesTest` (单元测试)

**测试场景:**

- [ ] **属性加载**
    - [ ] maxRetryCount 加载
    - [ ] lockTime 加载
    - [ ] 默认值验证

#### 5.2 CaptchaProperties

**测试类:** `CaptchaPropertiesTest` (单元测试)

**测试场景:**

- [ ] **属性加载**
    - [ ] type 加载
    - [ ] category 加载
    - [ ] numberLength 加载
    - [ ] expiration 加载

---

### 6. Enums 层测试

#### 6.1 CaptchaCategory

**测试类:** `CaptchaCategoryTest` (单元测试)

**测试场景:**

- [ ] **枚举值验证**
    - [ ] MATH
    - [ ] CHAR
    - [ ] BLOCK_PUZZLE
    - [ ] CLICK_WORD
    - [ ] DEFAULT

#### 6.2 CaptchaType

**测试类:** `CaptchaTypeTest` (单元测试)

**测试场景:**

- [ ] **枚举值验证**
    - [ ] RANDOM
    - [ ] DEFAULT
    - [ ] SLIDER
    - [ ] CLICK

---

### 7. Listener 层测试

#### 7.1 UserActionListener

**测试类:** `UserActionListenerTest` (集成测试)

**测试场景:**

- [ ] **事件监听**
    - [ ] 登录事件监听
    - [ ] 登出事件监听
    - [ ] 注册事件监听

- [ ] **日志记录**
    - [ ] 记录用户操作日志
    - [ ] 异步日志记录

---

## 🔧 集成测试场景

### 完整登录流程测试

**测试类:** `LoginFlowIntegrationTest`

**测试场景:**

- [ ] **从登录到访问受保护资源**
    1. 获取验证码
    2. 用户登录（带验证码）
    3. 获取 Token
    4. 使用 Token 访问受保护 API
    5. Token 过期
    6. 刷新 Token
    7. 使用新 Token 访问 API
    8. 登出
    9. Token 失效验证

- [ ] **多租户登录流程**
    1. 查询租户列表
    2. 选择租户登录
    3. 验证租户上下文
    4. 切换租户
    5. 验证数据隔离

- [ ] **防暴力破解流程**
    1. 连续5次错误登录
    2. 账号被锁定
    3. 等待30分钟（Mock时间）
    4. 锁定解除
    5. 成功登录

---

## 📊 预计测试用例数量

| 模块          | 测试类数   | 预计用例数     | 优先级 |
|-------------|--------|-----------|-----|
| Controller  | 2      | 80个       | P0  |
| Service     | 6      | 120个      | P0  |
| Form        | 6      | 40个       | P1  |
| VO          | 2      | 10个       | P2  |
| Config      | 1      | 10个       | P1  |
| Properties  | 2      | 10个       | P2  |
| Enums       | 2      | 10个       | P3  |
| Listener    | 1      | 10个       | P1  |
| Integration | 1      | 30个       | P0  |
| **总计**      | **23** | **~320个** | -   |

---

## 🎯 实施顺序

### Day 1: 核心Controller和Service

1. ✅ 创建测试基础设施
2. ⏳ TokenController 集成测试 (核心)
3. ⏳ SysLoginService 单元测试
4. ⏳ PasswordAuthStrategy 单元测试 (最常用)

### Day 2: 其他Strategy和集成测试

5. ⏳ EmailAuthStrategy 单元测试
6. ⏳ SmsAuthStrategy 单元测试
7. ⏳ SocialAuthStrategy 单元测试
8. ⏳ XcxAuthStrategy 单元测试
9. ⏳ CaptchaController 集成测试
10. ⏳ LoginFlowIntegrationTest 完整流程

### Day 3: Domain/Config/Properties/Enums

11. ⏳ Form 对象校验测试
12. ⏳ VO 对象测试
13. ⏳ CaptchaConfig 测试
14. ⏳ Properties 测试
15. ⏳ Enums 测试
16. ⏳ Listener 测试
17. ⏳ 生成覆盖率报告
18. ⏳ 修复覆盖率不足的部分

---

## 📝 测试注意事项

### 安全测试要点

1. **密码安全**
    - 不得明文存储密码
    - 使用 BCrypt 加密
    - 验证密码强度

2. **Token安全**
    - Token 签名验证
    - Token 过期验证
    - Refresh Token 机制

3. **防护机制**
    - 验证码防机器人
    - 失败次数限制
    - IP黑名单（如有）

### Mock 策略

- **外部依赖Mock:**
    - 微信API
    - QQ API
    - 短信服务
    - 邮件服务

- **数据库Mock:**
    - 使用 @MockBean 模拟 Dubbo 远程服务
    - 使用 TestContainers Redis (或 Embedded Redis)

### 测试数据

- 使用测试专用账号
- 使用测试租户
- 清理测试数据（@AfterEach）

---

## ✅ 验收标准

- [ ] **覆盖率达标**
    - 指令覆盖率 >= 95%
    - 分支覆盖率 >= 90%
    - 方法覆盖率 >= 95%
    - 类覆盖率 = 100%

- [ ] **安全测试通过**
    - 所有安全测试场景覆盖
    - 无已知安全漏洞

- [ ] **集成测试通过**
    - 完整登录流程测试通过
    - 多租户隔离测试通过
    - 防暴力破解测试通过

- [ ] **代码质量**
    - 遵循测试最佳实践
    - 使用 @Nested 组织测试
    - 使用 @ParameterizedTest 减少重复
    - 使用 AssertJ 流式断言

---

**创建人:** Test Team
**状态:** ✅ 计划完成，待实施
**下一步:** 创建测试基础设施，开始 TokenController 测试
