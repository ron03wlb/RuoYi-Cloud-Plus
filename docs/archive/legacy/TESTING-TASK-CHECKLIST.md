# RuoYi-Cloud-Plus 单元测试任务清单

> **生成日期:** 2025-11-03
> **总模块数:** 33个业务模块
> **任务粒度:** 类级别
> **测试类型:** 单元测试 + 集成测试 + 特殊场景测试

---

## 📊 总体进度概览

### 统计数据

| 模块类型            | 模块数    | Service类 | Controller类 | Mapper类 | Utils类 | 其他     |
|-----------------|--------|----------|-------------|---------|--------|--------|
| ruoyi-common-*  | 27     | 8        | 3           | 0       | 33     | 29     |
| ruoyi-modules/* | 4      | 60       | 29          | 29      | 0      | 2      |
| ruoyi-api/*     | 3      | 21       | 0           | 0       | 0      | 0      |
| ruoyi-auth      | 1      | 1        | 2           | 0       | 0      | 0      |
| ruoyi-gateway   | 1      | 0        | 0           | 0       | 1      | 1      |
| ruoyi-visual/*  | 1      | 0        | 1           | 0       | 0      | 0      |
| **总计**          | **37** | **90**   | **35**      | **29**  | **34** | **32** |

### 整体进度

- [ ] **Phase 1: Common库模块** (27个模块) - 预计10天
- [ ] **Phase 2: 认证与网关** (2个模块) - 预计3天
- [ ] **Phase 3: 核心业务模块** (4个模块) - 预计7天
- [ ] **Phase 4: API接口模块** (3个模块) - 预计2天
- [ ] **Phase 5: 监控模块** (1个模块) - 预计1天

---

## Phase 1: Common库模块测试任务

### ✅ ruoyi-common-core (已完成85%)

**模块路径:** `ruoyi-common/ruoyi-common-core`
**覆盖率目标:** 95%
**当前进度:** 85% (228个测试用例已完成)

#### 单元测试 - Utils类

- [x] StringUtils (98%覆盖率, 74个测试用例)
- [x] DateUtils (100%覆盖率)
- [x] StreamUtils (99%覆盖率)
- [x] TreeBuildUtils (100%覆盖率)
- [x] FileUtils (100%覆盖率)
- [x] MimeTypeUtils (100%覆盖率)
- [x] ReflectUtils (100%覆盖率)
- [x] SqlUtil (100%覆盖率)
- [x] RegexValidator (90%覆盖率)
- [ ] RegexUtils
- [ ] NetUtils
- [ ] Threads
- [ ] ObjectUtils
- [ ] RegionUtils

#### 集成测试 - Spring依赖Utils类

- [x] ValidatorUtils (100%覆盖率, 集成测试)
- [x] ServletUtils (95%覆盖率, 集成测试)
- [x] MapstructUtils (92%覆盖率, 集成测试)
- [x] MessageUtils (100%覆盖率, 集成测试)
- [x] SpringUtils (100%覆盖率, 集成测试)
- [ ] AddressUtils (40%覆盖率, 需补充集成测试)

#### 单元测试 - Exception类

- [x] ServiceException (100%覆盖率)
- [x] SseException (100%覆盖率)
- [x] BaseException (100%覆盖率)
- [x] UserException (100%覆盖率)
- [x] FileException (100%覆盖率)

#### 单元测试 - Validator类

- [ ] XssValidator
- [ ] IdCardValidator
- [ ] InEnumValidator
- [ ] PhoneValidator
- [ ] RepeatSubmitValidator
- [ ] MobileValidator

---

### ⏳ ruoyi-common-mybatis

**模块路径:** `ruoyi-common/ruoyi-common-mybatis`
**覆盖率目标:** 90%
**测试重点:** MyBatis-Plus插件、数据权限、多租户

#### 单元测试 - Service类

- [ ] PlusMetaObjectHandler (测试自动填充功能)

#### 集成测试 - Handler类

- [ ] PlusPostInitTableInfoHandler
    - [ ] 测试表信息初始化
    - [ ] 测试自定义主键生成器
- [ ] InjectionMetaObjectHandler
    - [ ] 测试createBy/updateBy自动填充
    - [ ] 测试createTime/updateTime自动填充
- [ ] MybatisExceptionHandler
    - [ ] 测试SQL异常处理
    - [ ] 测试异常消息转换
- [ ] PlusDataPermissionHandler
    - [ ] 测试数据权限SQL拦截
    - [ ] 测试多租户数据隔离

#### 特殊场景测试

- [ ] 测试MyBatis-Plus分页插件
- [ ] 测试乐观锁插件
- [ ] 测试SQL性能分析

---

### ⏳ ruoyi-common-redis

**模块路径:** `ruoyi-common/ruoyi-common-redis`
**覆盖率目标:** 90%
**测试重点:** Redis操作、分布式锁、缓存

#### 单元测试 - Utils类

- [ ] CacheUtils
    - [ ] 测试put/get/evict操作
    - [ ] 测试缓存名称管理
    - [ ] 测试缓存清空
- [ ] RedisUtils
    - [ ] 测试字符串操作(set/get/delete)
    - [ ] 测试Hash操作(hset/hget/hdel)
    - [ ] 测试List操作(lpush/rpush/lpop)
    - [ ] 测试Set操作(sadd/srem/smembers)
    - [ ] 测试ZSet操作(zadd/zrem/zrange)
    - [ ] 测试过期时间设置
    - [ ] 测试批量操作
- [ ] SequenceUtils
    - [ ] 测试ID生成
    - [ ] 测试序列号生成

#### 集成测试 - Redis操作

- [ ] RedisUtils集成测试
    - [ ] 使用TestContainers Redis
    - [ ] 测试真实Redis连接
    - [ ] 测试分布式场景

#### 单元测试 - Handler类

- [ ] KeyPrefixHandler
    - [ ] 测试Key前缀添加
    - [ ] 测试Key格式化
- [ ] RedisExceptionHandler
    - [ ] 测试Redis连接异常
    - [ ] 测试序列化异常

#### 特殊场景测试

- [ ] 测试@Lock4j分布式锁
- [ ] 测试Redisson分布式锁
- [ ] 测试Redis缓存注解(@Cacheable, @CacheEvict)

---

### ⏳ ruoyi-common-satoken

**模块路径:** `ruoyi-common/ruoyi-common-satoken`
**覆盖率目标:** 95% (安全关键)
**测试重点:** 认证、权限、Token管理

#### 单元测试 - Utils类

- [ ] LoginHelper
    - [ ] 测试用户登录(login)
    - [ ] 测试用户登出(logout)
    - [ ] 测试获取登录用户信息
    - [ ] 测试获取用户ID
    - [ ] 测试获取租户ID
    - [ ] 测试Token刷新
    - [ ] 测试超级管理员判断

#### 集成测试 - Handler类

- [ ] SaTokenExceptionHandler
    - [ ] 测试Token过期异常
    - [ ] 测试Token无效异常
    - [ ] 测试权限不足异常
    - [ ] 测试未登录异常

#### 特殊场景测试

- [ ] 测试多设备登录
- [ ] 测试单设备互踢
- [ ] 测试Remember Me功能
- [ ] 测试Token二级认证
- [ ] 测试临时Token认证

---

### ⏳ ruoyi-common-encrypt

**模块路径:** `ruoyi-common/ruoyi-common-encrypt`
**覆盖率目标:** 95%
**测试重点:** 数据加密解密、字段级加密

#### 单元测试 - Utils类

- [ ] EncryptUtils
    - [ ] 测试AES加密/解密
    - [ ] 测试RSA加密/解密
    - [ ] 测试SM2加密/解密
    - [ ] 测试SM4加密/解密
    - [ ] 测试Base64编码/解码
    - [ ] 测试MD5哈希
    - [ ] 测试SHA256哈希

#### 特殊场景测试

- [ ] 测试@EncryptField注解
    - [ ] 测试实体字段自动加密
    - [ ] 测试实体字段自动解密
- [ ] 测试加密配置切换
- [ ] 测试加密算法性能

---

### ⏳ ruoyi-common-excel

**模块路径:** `ruoyi-common/ruoyi-common-excel`
**覆盖率目标:** 90%
**测试重点:** Excel导入导出、数据转换

#### 单元测试 - Utils类

- [ ] ExcelUtil
    - [ ] 测试Excel导出(单sheet)
    - [ ] 测试Excel导出(多sheet)
    - [ ] 测试Excel导入
    - [ ] 测试Excel模板导出
    - [ ] 测试数据验证
- [ ] ExcelWriterWrapper
    - [ ] 测试Wrapper构建
    - [ ] 测试写入操作

#### 单元测试 - Handler类

- [ ] DataWriteHandler
    - [ ] 测试下拉框数据写入
    - [ ] 测试数据字典翻译
- [ ] ExcelDownHandler
    - [ ] 测试Excel下载响应
    - [ ] 测试文件名编码
- [ ] CellMergeHandler
    - [ ] 测试单元格合并
    - [ ] 测试合并策略

#### 集成测试

- [ ] 测试大数据量导出(100万行)
- [ ] 测试Excel模板功能
- [ ] 测试自定义转换器

---

### ⏳ ruoyi-common-json

**模块路径:** `ruoyi-common/ruoyi-common-json`
**覆盖率目标:** 95%

#### 单元测试 - Utils类

- [ ] JsonUtils
    - [ ] 测试对象转JSON字符串
    - [ ] 测试JSON字符串转对象
    - [ ] 测试JSON字符串转List
    - [ ] 测试JSON字符串转Map
    - [ ] 测试美化输出
    - [ ] 测试空值处理
    - [ ] 测试日期格式化
    - [ ] 测试异常场景

---

### ⏳ ruoyi-common-sensitive

**模块路径:** `ruoyi-common/ruoyi-common-sensitive`
**覆盖率目标:** 95%
**测试重点:** 敏感数据脱敏

#### 单元测试 - Service类

- [ ] SensitiveService
    - [ ] 测试手机号脱敏
    - [ ] 测试身份证脱敏
    - [ ] 测试邮箱脱敏
    - [ ] 测试银行卡号脱敏
    - [ ] 测试地址脱敏

#### 单元测试 - Handler类

- [ ] SensitiveHandler
    - [ ] 测试@Sensitive注解处理
    - [ ] 测试JSON序列化脱敏

#### 特殊场景测试

- [ ] 测试嵌套对象脱敏
- [ ] 测试集合对象脱敏
- [ ] 测试自定义脱敏策略

---

### ⏳ ruoyi-common-tenant

**模块路径:** `ruoyi-common/ruoyi-common-tenant`
**覆盖率目标:** 95% (多租户关键)

#### 集成测试 - Handler类

- [ ] TenantLineHandler
    - [ ] 测试租户ID自动填充
    - [ ] 测试SELECT时租户过滤
    - [ ] 测试UPDATE时租户校验
    - [ ] 测试DELETE时租户校验
- [ ] TenantKeyPrefixHandler
    - [ ] 测试Redis Key租户前缀

#### 单元测试 - Exception类

- [ ] TenantException
    - [ ] 测试租户不存在异常
    - [ ] 测试租户已过期异常
    - [ ] 测试租户无权限异常

#### 特殊场景测试

- [ ] 测试@IgnoreTenant注解
- [ ] 测试租户上下文切换
- [ ] 测试跨租户数据查询（管理员）
- [ ] 测试租户数据隔离

---

### ⏳ ruoyi-common-translation

**模块路径:** `ruoyi-common/ruoyi-common-translation`
**覆盖率目标:** 90%

#### 单元测试 - Handler类

- [ ] TranslationHandler
    - [ ] 测试@Translation注解处理
    - [ ] 测试字典翻译
    - [ ] 测试OSS翻译
    - [ ] 测试用户翻译
    - [ ] 测试部门翻译

#### 特殊场景测试

- [ ] 测试嵌套对象翻译
- [ ] 测试集合对象翻译
- [ ] 测试自定义翻译接口

---

### ⏳ ruoyi-common-web

**模块路径:** `ruoyi-common/ruoyi-common-web`
**覆盖率目标:** 85%

#### 单元测试 - Controller类

- [ ] IndexController
    - [ ] 测试首页访问
    - [ ] 测试健康检查

#### 单元测试 - Handler类

- [ ] GlobalExceptionHandler
    - [ ] 测试业务异常处理
    - [ ] 测试验证异常处理
    - [ ] 测试权限异常处理
    - [ ] 测试404异常处理
    - [ ] 测试500异常处理

---

### ⏳ ruoyi-common-oss

**模块路径:** `ruoyi-common/ruoyi-common-oss`
**覆盖率目标:** 90%

#### 单元测试 - Exception类

- [ ] OssException
    - [ ] 测试OSS异常创建
    - [ ] 测试异常消息

#### 集成测试 - OSS操作

- [ ] 测试MinIO文件上传
- [ ] 测试MinIO文件下载
- [ ] 测试MinIO文件删除
- [ ] 测试MinIO文件列表
- [ ] 测试预签名URL生成

---

### ⏳ ruoyi-common-mail

**模块路径:** `ruoyi-common/ruoyi-common-mail`
**覆盖率目标:** 85%

#### 单元测试 - Utils类

- [ ] MailUtils
    - [ ] 测试发送简单邮件
    - [ ] 测试发送HTML邮件
    - [ ] 测试发送带附件邮件
    - [ ] 测试批量发送
    - [ ] 测试异常处理

---

### ⏳ ruoyi-common-social

**模块路径:** `ruoyi-common/ruoyi-common-social`
**覆盖率目标:** 85%

#### 单元测试 - Utils类

- [ ] SocialUtils
    - [ ] 测试微信登录
    - [ ] 测试QQ登录
    - [ ] 测试钉钉登录
    - [ ] 测试GitHub登录
- [ ] SocialLoginUtils
    - [ ] 测试OAuth回调处理
    - [ ] 测试用户信息获取

---

### ⏳ ruoyi-common-websocket

**模块路径:** `ruoyi-common/ruoyi-common-websocket`
**覆盖率目标:** 85%

#### 单元测试 - Utils类

- [ ] WebSocketUtils
    - [ ] 测试消息发送
    - [ ] 测试广播消息
    - [ ] 测试会话管理

#### 单元测试 - Handler类

- [ ] WebSocketHandler
    - [ ] 测试连接建立
    - [ ] 测试连接关闭
    - [ ] 测试消息接收

---

### ⏳ ruoyi-common-sse

**模块路径:** `ruoyi-common/ruoyi-common-sse`
**覆盖率目标:** 85%

#### 单元测试 - Controller类

- [ ] SseController
    - [ ] 测试SSE连接建立
    - [ ] 测试SSE消息推送

#### 单元测试 - Utils类

- [ ] SseUtils
    - [ ] 测试Emitter创建
    - [ ] 测试消息发送
    - [ ] 测试连接管理

---

### ⏳ ruoyi-common-sms

**模块路径:** `ruoyi-common/ruoyi-common-sms`
**覆盖率目标:** 85%

#### 单元测试 - Handler类

- [ ] SmsHandler
    - [ ] 测试短信发送(阿里云)
    - [ ] 测试短信发送(腾讯云)
    - [ ] 测试验证码发送
    - [ ] 测试短信模板

---

### ⏳ ruoyi-common-idempotent

**模块路径:** `ruoyi-common/ruoyi-common-idempotent`
**覆盖率目标:** 95%

#### 特殊场景测试

- [ ] 测试@RepeatSubmit注解
    - [ ] 测试防重复提交
    - [ ] 测试基于Token的幂等
    - [ ] 测试基于参数的幂等
- [ ] 测试幂等性键生成
- [ ] 测试Redis幂等存储

---

### ⏳ ruoyi-common-ratelimiter

**模块路径:** `ruoyi-common/ruoyi-common-ratelimiter`
**覆盖率目标:** 95%

#### 特殊场景测试

- [ ] 测试@RateLimiter注解
    - [ ] 测试滑动窗口限流
    - [ ] 测试令牌桶限流
    - [ ] 测试漏桶限流
- [ ] 测试Sentinel限流规则
- [ ] 测试分布式限流

---

### ⏳ ruoyi-common-service-impl

**模块路径:** `ruoyi-common/ruoyi-common-service-impl`
**覆盖率目标:** 90%

#### 单元测试 - Service类

- [ ] BaseServiceImpl
    - [ ] 测试分页查询
    - [ ] 测试列表查询
- [ ] DictServiceImpl
    - [ ] 测试字典数据获取
    - [ ] 测试字典缓存

#### 单元测试 - Utils类

- [ ] DictUtils
    - [ ] 测试字典标签获取
    - [ ] 测试字典值获取

---

### ⏳ ruoyi-common-doc

**模块路径:** `ruoyi-common/ruoyi-common-doc`
**覆盖率目标:** 70%

#### 单元测试 - Handler类

- [ ] OpenApiHandler
    - [ ] 测试API文档配置
    - [ ] 测试Swagger UI配置

---

### ⏳ ruoyi-common-dubbo

**模块路径:** `ruoyi-common/ruoyi-common-dubbo`
**覆盖率目标:** 85%

#### 单元测试 - Handler类

- [ ] DubboExceptionHandler
    - [ ] 测试Dubbo异常处理
    - [ ] 测试RPC超时异常
    - [ ] 测试服务不可用异常

---

### ⏳ ruoyi-common-security

**模块路径:** `ruoyi-common/ruoyi-common-security`
**覆盖率目标:** 95% (安全关键)

#### 特殊场景测试

- [ ] 测试XSS过滤器
- [ ] 测试SQL注入防护
- [ ] 测试CSRF防护
- [ ] 测试安全响应头

---

### ⏳ ruoyi-common-log

**模块路径:** `ruoyi-common/ruoyi-common-log`
**覆盖率目标:** 80%

#### 特殊场景测试

- [ ] 测试@Log注解
    - [ ] 测试操作日志记录
    - [ ] 测试异步日志记录
- [ ] 测试日志切面
- [ ] 测试日志存储

---

### ⏳ ruoyi-common-logstash

**模块路径:** `ruoyi-common/ruoyi-common-logstash`
**覆盖率目标:** 70%

#### 集成测试

- [ ] 测试Logstash日志输出
- [ ] 测试ELK集成

---

### ⏳ ruoyi-common-prometheus

**模块路径:** `ruoyi-common/ruoyi-common-prometheus`
**覆盖率目标:** 70%

#### 集成测试

- [ ] 测试Prometheus指标暴露
- [ ] 测试自定义指标

---

### ⏳ ruoyi-common-skylog

**模块路径:** `ruoyi-common/ruoyi-common-skylog`
**覆盖率目标:** 70%

#### 集成测试

- [ ] 测试Skywalking链路追踪
- [ ] 测试TraceId传递

---

### ⏳ ruoyi-common-bus

**模块路径:** `ruoyi-common/ruoyi-common-bus`
**覆盖率目标:** 75%

#### 集成测试

- [ ] 测试配置动态刷新
- [ ] 测试事件总线

---

### ⏳ ruoyi-common-job

**模块路径:** `ruoyi-common/ruoyi-common-job`
**覆盖率目标:** 80%

#### 集成测试

- [ ] 测试SnailJob任务注册
- [ ] 测试任务执行

---

### ⏳ ruoyi-common-seata

**模块路径:** `ruoyi-common/ruoyi-common-seata`
**覆盖率目标:** 85%

#### 特殊场景测试

- [ ] 测试@GlobalTransactional注解
- [ ] 测试分布式事务回滚
- [ ] 测试AT模式
- [ ] 测试TCC模式

---

### ⏳ ruoyi-common-elasticsearch

**模块路径:** `ruoyi-common/ruoyi-common-elasticsearch`
**覆盖率目标:** 80%

#### 集成测试

- [ ] 测试ElasticSearch连接
- [ ] 测试Easy-Es CRUD操作
- [ ] 测试全文检索

---

### ⏳ ruoyi-common-loadbalancer

**模块路径:** `ruoyi-common/ruoyi-common-loadbalancer`
**覆盖率目标:** 75%

#### 集成测试

- [ ] 测试灰度发布
- [ ] 测试版本路由
- [ ] 测试自定义负载均衡

---

### ⏳ ruoyi-common-nacos

**模块路径:** `ruoyi-common/ruoyi-common-nacos`
**覆盖率目标:** 70%

#### 集成测试

- [ ] 测试Nacos配置读取
- [ ] 测试配置动态刷新
- [ ] 测试服务注册发现

---

## Phase 2: 认证与网关模块测试任务

### ⏳ ruoyi-auth (优先级最高)

**模块路径:** `ruoyi-auth`
**覆盖率目标:** 95% (安全关键)
**测试重点:** 登录、认证、Token管理

#### 单元测试 - Controller类

- [ ] TokenController
    - [ ] 测试用户登录(成功)
    - [ ] 测试用户登录(失败-密码错误)
    - [ ] 测试用户登录(失败-账号锁定)
    - [ ] 测试用户登录(失败-账号过期)
    - [ ] 测试Token刷新
    - [ ] 测试登出
    - [ ] 测试获取Token信息
- [ ] CaptchaController
    - [ ] 测试图形验证码生成
    - [ ] 测试验证码校验
    - [ ] 测试验证码刷新
    - [ ] 测试验证码过期

#### 集成测试

- [ ] 完整登录流程测试
    - [ ] 测试从登录到访问受保护资源
    - [ ] 测试Token过期后自动刷新
    - [ ] 测试多设备同时登录

#### 特殊场景测试

- [ ] 测试短信验证码登录
- [ ] 测试社交登录(微信/QQ)
- [ ] 测试扫码登录
- [ ] 测试Remember Me功能
- [ ] 测试防暴力破解(失败次数限制)
- [ ] 测试验证码防机器人

---

### ⏳ ruoyi-gateway

**模块路径:** `ruoyi-gateway`
**覆盖率目标:** 85%
**测试重点:** 路由、过滤器、限流

#### 单元测试 - Utils类

- [ ] WebFluxUtils
    - [ ] 测试响应包装
    - [ ] 测试请求体读取
    - [ ] 测试响应体写入

#### 单元测试 - Handler类

- [ ] GatewayExceptionHandler
    - [ ] 测试404路由未找到
    - [ ] 测试503服务不可用
    - [ ] 测试超时异常
    - [ ] 测试限流异常

#### 集成测试 - 路由与过滤器

- [ ] 测试路由转发
    - [ ] 测试转发到system服务
    - [ ] 测试转发到auth服务
    - [ ] 测试路由断言
- [ ] 测试全局过滤器
    - [ ] 测试认证过滤器
    - [ ] 测试日志过滤器
    - [ ] 测试跨域过滤器
    - [ ] 测试限流过滤器

#### 特殊场景测试

- [ ] 测试灰度发布路由
- [ ] 测试熔断降级
- [ ] 测试重试机制
- [ ] 测试黑白名单

---

## Phase 3: 核心业务模块测试任务

### ⏳ ruoyi-system (核心模块)

**模块路径:** `ruoyi-modules/ruoyi-system`
**覆盖率目标:** 90%
**测试重点:** 用户、角色、权限、多租户

#### 单元测试 - Service Implementation类

##### 用户管理

- [ ] SysUserServiceImpl
    - [ ] 测试查询用户(selectUserById)
    - [ ] 测试查询用户列表(selectUserList)
    - [ ] 测试分页查询用户
    - [ ] 测试新增用户(insertUser)
    - [ ] 测试修改用户(updateUser)
    - [ ] 测试删除用户(deleteUserById)
    - [ ] 测试批量删除用户
    - [ ] 测试重置密码(resetPwd)
    - [ ] 测试修改密码(updateUserProfile)
    - [ ] 测试校验用户名唯一性
    - [ ] 测试校验手机号唯一性
    - [ ] 测试校验邮箱唯一性

##### 角色管理

- [ ] SysRoleServiceImpl
    - [ ] 测试查询角色(selectRoleById)
    - [ ] 测试查询角色列表(selectRoleList)
    - [ ] 测试新增角色(insertRole)
    - [ ] 测试修改角色(updateRole)
    - [ ] 测试删除角色(deleteRoleById)
    - [ ] 测试批量删除角色
    - [ ] 测试角色授权菜单(authDataScope)
    - [ ] 测试角色授权用户
    - [ ] 测试校验角色名唯一性
    - [ ] 测试校验角色键唯一性

##### 菜单管理

- [ ] SysMenuServiceImpl
    - [ ] 测试查询菜单(selectMenuById)
    - [ ] 测试查询菜单树(selectMenuTreeByUserId)
    - [ ] 测试查询菜单列表(selectMenuList)
    - [ ] 测试新增菜单(insertMenu)
    - [ ] 测试修改菜单(updateMenu)
    - [ ] 测试删除菜单(deleteMenuById)
    - [ ] 测试校验菜单名唯一性
    - [ ] 测试构建前端路由树

##### 部门管理

- [ ] SysDeptServiceImpl
    - [ ] 测试查询部门(selectDeptById)
    - [ ] 测试查询部门树(selectDeptTreeList)
    - [ ] 测试查询部门列表(selectDeptList)
    - [ ] 测试新增部门(insertDept)
    - [ ] 测试修改部门(updateDept)
    - [ ] 测试删除部门(deleteDeptById)
    - [ ] 测试校验部门名唯一性

##### 岗位管理

- [ ] SysPostServiceImpl
    - [ ] 测试查询岗位(selectPostById)
    - [ ] 测试查询岗位列表(selectPostList)
    - [ ] 测试新增岗位(insertPost)
    - [ ] 测试修改岗位(updatePost)
    - [ ] 测试删除岗位(deletePostById)
    - [ ] 测试批量删除岗位

##### 字典管理

- [ ] SysDictTypeServiceImpl
    - [ ] 测试查询字典类型
    - [ ] 测试新增字典类型
    - [ ] 测试修改字典类型
    - [ ] 测试删除字典类型
    - [ ] 测试刷新字典缓存
- [ ] SysDictDataServiceImpl
    - [ ] 测试查询字典数据
    - [ ] 测试新增字典数据
    - [ ] 测试修改字典数据
    - [ ] 测试删除字典数据

##### 配置管理

- [ ] SysConfigServiceImpl
    - [ ] 测试查询配置(selectConfigByKey)
    - [ ] 测试查询配置列表
    - [ ] 测试新增配置(insertConfig)
    - [ ] 测试修改配置(updateConfig)
    - [ ] 测试删除配置(deleteConfigById)
    - [ ] 测试刷新配置缓存

##### 通知公告

- [ ] SysNoticeServiceImpl
    - [ ] 测试查询通知公告
    - [ ] 测试新增通知公告
    - [ ] 测试修改通知公告
    - [ ] 测试删除通知公告

##### 日志管理

- [ ] SysOperLogServiceImpl
    - [ ] 测试查询操作日志
    - [ ] 测试新增操作日志
    - [ ] 测试批量删除操作日志
    - [ ] 测试清空操作日志
- [ ] SysLogininforServiceImpl
    - [ ] 测试查询登录日志
    - [ ] 测试新增登录日志
    - [ ] 测试批量删除登录日志
    - [ ] 测试清空登录日志

##### 多租户管理

- [ ] SysTenantServiceImpl
    - [ ] 测试查询租户
    - [ ] 测试新增租户
    - [ ] 测试修改租户
    - [ ] 测试删除租户
    - [ ] 测试校验租户状态
    - [ ] 测试租户过期检查
- [ ] SysTenantPackageServiceImpl
    - [ ] 测试查询租户套餐
    - [ ] 测试新增租户套餐
    - [ ] 测试修改租户套餐
    - [ ] 测试删除租户套餐

##### 客户端管理

- [ ] SysClientServiceImpl
    - [ ] 测试查询客户端
    - [ ] 测试新增客户端
    - [ ] 测试修改客户端
    - [ ] 测试删除客户端

##### 权限管理

- [ ] SysPermissionServiceImpl
    - [ ] 测试获取用户权限
    - [ ] 测试获取角色权限
    - [ ] 测试获取菜单权限

##### 社交绑定

- [ ] SysSocialServiceImpl
    - [ ] 测试查询社交绑定
    - [ ] 测试新增社交绑定
    - [ ] 测试删除社交绑定

##### 敏感词管理

- [ ] SysSensitiveServiceImpl
    - [ ] 测试查询敏感词
    - [ ] 测试新增敏感词
    - [ ] 测试修改敏感词
    - [ ] 测试删除敏感词
    - [ ] 测试敏感词过滤

#### 单元测试 - Controller类

- [ ] SysUserController (18个Controller,参照Service测试)
- [ ] SysRoleController
- [ ] SysMenuController
- [ ] SysDeptController
- [ ] SysPostController
- [ ] SysDictTypeController
- [ ] SysDictDataController
- [ ] SysConfigController
- [ ] SysNoticeController
- [ ] SysOperlogController
- [ ] SysLogininforController
- [ ] SysProfileController
- [ ] SysTenantController
- [ ] SysTenantPackageController
- [ ] SysClientController
- [ ] SysSocialController
- [ ] CacheController
- [ ] SysUserOnlineController

#### 集成测试 - Mapper类

- [ ] SysUserMapper (19个Mapper)
- [ ] SysRoleMapper
- [ ] SysMenuMapper
- [ ] SysDeptMapper
- [ ] SysPostMapper
- [ ] SysDictTypeMapper
- [ ] SysDictDataMapper
- [ ] SysConfigMapper
- [ ] SysNoticeMapper
- [ ] SysOperLogMapper
- [ ] SysLogininforMapper
- [ ] SysTenantMapper
- [ ] SysTenantPackageMapper
- [ ] SysClientMapper
- [ ] SysSocialMapper
- [ ] SysUserRoleMapper
- [ ] SysUserPostMapper
- [ ] SysRoleMenuMapper
- [ ] SysRoleDeptMapper

#### 单元测试 - Dubbo Service类 (Mock mapper)

- [ ] RemoteUserServiceImpl (13个Dubbo服务)
- [ ] RemoteRoleServiceImpl
- [ ] RemotePermissionServiceImpl
- [ ] RemoteConfigServiceImpl
- [ ] RemoteDeptServiceImpl
- [ ] RemoteDictServiceImpl
- [ ] RemotePostServiceImpl
- [ ] RemoteTenantServiceImpl
- [ ] RemoteClientServiceImpl
- [ ] RemoteLogServiceImpl
- [ ] RemoteSocialServiceImpl
- [ ] RemoteTaskAssigneeServiceImpl
- [ ] RemoteDataScopeServiceImpl

#### 特殊场景测试

- [ ] 多租户数据隔离测试
    - [ ] 测试租户A查询不到租户B的数据
    - [ ] 测试租户自动切换
    - [ ] 测试@IgnoreTenant注解
- [ ] 数据权限测试
    - [ ] 测试部门数据权限
    - [ ] 测试自定义数据权限
- [ ] 权限验证测试
    - [ ] 测试@SaCheckPermission注解
    - [ ] 测试@SaCheckRole注解
- [ ] 缓存测试
    - [ ] 测试字典缓存
    - [ ] 测试配置缓存
    - [ ] 测试用户缓存

---

### ⏳ ruoyi-gen (代码生成)

**模块路径:** `ruoyi-modules/ruoyi-gen`
**覆盖率目标:** 85%

#### 单元测试 - Service类

- [ ] GenTableServiceImpl
    - [ ] 测试查询表信息
    - [ ] 测试导入表结构
    - [ ] 测试预览生成代码
    - [ ] 测试生成代码(ZIP下载)
    - [ ] 测试修改表配置
    - [ ] 测试同步表结构

#### 单元测试 - Controller类

- [ ] GenController
    - [ ] 测试查询表列表
    - [ ] 测试导入表
    - [ ] 测试编辑表
    - [ ] 测试删除表
    - [ ] 测试预览代码
    - [ ] 测试生成代码

#### 集成测试 - Mapper类

- [ ] GenTableMapper
    - [ ] 测试查询数据库表
    - [ ] 测试查询表详情
- [ ] GenTableColumnMapper
    - [ ] 测试查询表字段

#### 特殊场景测试

- [ ] 测试不同数据库类型(MySQL/Oracle/PostgreSQL)
- [ ] 测试模板引擎渲染
- [ ] 测试自定义模板

---

### ⏳ ruoyi-resource (资源管理)

**模块路径:** `ruoyi-modules/ruoyi-resource`
**覆盖率目标:** 85%

#### 单元测试 - Service类

- [ ] SysOssServiceImpl
    - [ ] 测试查询OSS配置
    - [ ] 测试文件上传
    - [ ] 测试文件下载
    - [ ] 测试文件删除
    - [ ] 测试查询文件列表
- [ ] SysOssConfigServiceImpl
    - [ ] 测试查询OSS配置
    - [ ] 测试新增OSS配置
    - [ ] 测试修改OSS配置
    - [ ] 测试删除OSS配置

#### 单元测试 - Controller类

- [ ] SysOssController
- [ ] SysOssConfigController
- [ ] SysEmailController
- [ ] SysSmsController

#### 集成测试 - Mapper类

- [ ] SysOssMapper
- [ ] SysOssConfigMapper

#### 单元测试 - Dubbo Service类

- [ ] RemoteFileServiceImpl
    - [ ] 测试远程文件上传
    - [ ] 测试远程文件下载
- [ ] RemoteMailServiceImpl
    - [ ] 测试远程发送邮件
- [ ] RemoteSmsServiceImpl
    - [ ] 测试远程发送短信
- [ ] RemoteMessageServiceImpl
    - [ ] 测试远程发送消息

#### 特殊场景测试

- [ ] 测试MinIO文件上传(集成测试)
- [ ] 测试大文件上传
- [ ] 测试文件分片上传
- [ ] 测试邮件发送(Mock SMTP)
- [ ] 测试短信发送(Mock SMS)

---

### ⏳ ruoyi-workflow (工作流)

**模块路径:** `ruoyi-modules/ruoyi-workflow`
**覆盖率目标:** 85%

#### 单元测试 - Service类

- [ ] FlwDefinitionServiceImpl
    - [ ] 测试查询流程定义
    - [ ] 测试部署流程
    - [ ] 测试删除流程
    - [ ] 测试挂起/激活流程
- [ ] FlwInstanceServiceImpl
    - [ ] 测试启动流程实例
    - [ ] 测试查询流程实例
    - [ ] 测试终止流程实例
- [ ] FlwTaskServiceImpl
    - [ ] 测试查询待办任务
    - [ ] 测试查询已办任务
    - [ ] 测试完成任务
    - [ ] 测试转办任务
    - [ ] 测试委派任务
    - [ ] 测试退回任务
- [ ] FlwCategoryServiceImpl
    - [ ] 测试查询流程分类
    - [ ] 测试新增流程分类
- [ ] FlwSpelServiceImpl
    - [ ] 测试SpEL表达式解析
    - [ ] 测试SpEL表达式计算
- [ ] WorkflowServiceImpl
    - [ ] 测试工作流通用操作

#### 单元测试 - Controller类

- [ ] FlwDefinitionController (6个Controller)
- [ ] FlwInstanceController
- [ ] FlwTaskController
- [ ] FlwCategoryController
- [ ] FlwSpelController
- [ ] TestLeaveController (示例)

#### 集成测试 - Mapper类

- [ ] FlwInstanceMapper (6个Mapper)
- [ ] FlwInstanceBizExtMapper
- [ ] FlwTaskMapper
- [ ] FlwCategoryMapper
- [ ] FlwSpelMapper
- [ ] TestLeaveMapper

#### 单元测试 - Dubbo Service类

- [ ] RemoteWorkflowServiceImpl
    - [ ] 测试远程启动流程
    - [ ] 测试远程查询任务

#### 单元测试 - Handler类

- [ ] WorkflowPermissionHandler
    - [ ] 测试工作流权限校验
- [ ] FlowProcessEventHandler
    - [ ] 测试流程事件监听

#### 特殊场景测试

- [ ] 测试完整请假流程
    - [ ] 测试流程启动
    - [ ] 测试部门经理审批
    - [ ] 测试HR审批
    - [ ] 测试流程结束
- [ ] 测试流程退回
- [ ] 测试流程转办
- [ ] 测试流程会签
- [ ] 测试流程并行网关

---

## Phase 4: API接口模块测试任务

### ⏳ ruoyi-api-system

**模块路径:** `ruoyi-api/ruoyi-api-system`
**覆盖率目标:** 80% (接口定义)

#### 单元测试 - Service Interface

测试方式：通过测试实现类(ruoyi-system模块的Dubbo服务)来覆盖接口

- [ ] RemoteUserService (13个接口)
- [ ] RemoteRoleService
- [ ] RemotePermissionService
- [ ] RemoteConfigService
- [ ] RemoteDeptService
- [ ] RemoteDictService
- [ ] RemotePostService
- [ ] RemoteTenantService
- [ ] RemoteClientService
- [ ] RemoteLogService
- [ ] RemoteSocialService
- [ ] RemoteTaskAssigneeService
- [ ] RemoteDataScopeService

---

### ⏳ ruoyi-api-resource

**模块路径:** `ruoyi-api/ruoyi-api-resource`
**覆盖率目标:** 80%

#### 单元测试 - Service Interface

- [ ] RemoteFileService (6个接口)
- [ ] RemoteMailService
- [ ] RemoteSmsService
- [ ] RemoteMessageService

---

### ⏳ ruoyi-api-workflow

**模块路径:** `ruoyi-api/ruoyi-api-workflow`
**覆盖率目标:** 80%

#### 单元测试 - Service Interface

- [ ] RemoteWorkflowService (2个接口)

---

## Phase 5: 监控模块测试任务

### ⏳ ruoyi-monitor

**模块路径:** `ruoyi-visual/ruoyi-monitor`
**覆盖率目标:** 70%

#### 单元测试 - Controller类

- [ ] MonitorController
    - [ ] 测试查询应用列表
    - [ ] 测试查询应用详情
    - [ ] 测试健康检查

---

## 附录A: 测试实施规范

### A.1 测试文件命名规范

```
{ClassName}Test.java           # 单元测试
{ClassName}IntegrationTest.java # 集成测试
```

### A.2 测试方法命名规范

```java
shouldReturnExpectedResult_WhenCondition() // 推荐格式
```

### A.3 测试覆盖率目标

| 模块类型                               | 覆盖率目标 |
|------------------------------------|-------|
| 安全相关 (auth, satoken, security)     | 95%   |
| 核心业务 (system, workflow)            | 90%   |
| 通用工具 (common-core, common-redis)   | 90%   |
| 业务模块 (gen, resource)               | 85%   |
| 配置模块 (common-nacos, common-config) | 70%   |

### A.4 测试执行命令

```bash
# 运行所有测试
./gradlew test

# 运行单个模块测试
./gradlew :ruoyi-modules:ruoyi-system:test

# 生成覆盖率报告
./gradlew test jacocoTestReport

# 查看覆盖率报告
open build/reports/jacoco/test/html/index.html
```

---

## 附录B: 预估工作量

| Phase   | 模块数    | 测试类数     | 预计人天    | 优先级     |
|---------|--------|----------|---------|---------|
| Phase 1 | 27     | ~150     | 10天     | P1      |
| Phase 2 | 2      | ~10      | 3天      | P0 (最高) |
| Phase 3 | 4      | ~120     | 7天      | P1      |
| Phase 4 | 3      | ~21      | 2天      | P2      |
| Phase 5 | 1      | ~2       | 1天      | P3      |
| **总计**  | **37** | **~303** | **23天** | -       |

---

## 附录C: 进度追踪

**实时进度报告:** 请查看 [docs/TESTING-PROGRESS-REPORT.md](./TESTING-PROGRESS-REPORT.md)

**更新规则:**

- 每完成一个模块的所有测试，更新进度报告
- 每周五更新整体进度统计
- 达成里程碑时(如Phase完成)更新进度报告

---

## 附录D: 快速开始示例

### 1. 创建测试类

```java
package org.dromara.system.service;

import org.dromara.common.test.BaseUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SysUserServiceTest extends BaseUnitTest {

    @Mock
    private SysUserMapper userMapper;

    @InjectMocks
    private SysUserServiceImpl userService;

    @Test
    void shouldReturnUser_WhenValidIdProvided() {
        // Arrange
        Long userId = 1L;
        SysUser mockUser = new SysUser();
        mockUser.setUserId(userId);
        when(userMapper.selectById(userId)).thenReturn(mockUser);

        // Act
        SysUser result = userService.selectUserById(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        verify(userMapper, times(1)).selectById(userId);
    }
}
```

### 2. 运行测试

```bash
./gradlew :ruoyi-modules:ruoyi-system:test --tests "*SysUserServiceTest"
```

### 3. 查看覆盖率

```bash
./gradlew :ruoyi-modules:ruoyi-system:jacocoTestReport
open ruoyi-modules/ruoyi-system/build/reports/jacoco/test/html/index.html
```

---

**文档版本:** 1.0
**生成日期:** 2025-11-03
**维护者:** Test Team
**状态:** ✅ 已确认

祝测试顺利！🎯
