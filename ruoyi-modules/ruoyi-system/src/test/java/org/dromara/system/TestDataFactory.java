package org.dromara.system;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.dromara.system.domain.*;
import org.dromara.system.domain.bo.*;
import org.dromara.system.domain.vo.*;

/**
 * 测试数据工厂
 *
 * <p>提供统一的测试数据创建方法，遵循工厂模式
 *
 * <p>设计原则:
 *
 * <ul>
 *   <li>提供默认值，减少测试代码冗余
 *   <li>提供参数化构造方法，支持自定义
 *   <li>命名清晰，一看就懂
 *   <li>数据符合业务规则
 * </ul>
 *
 * @author Test Team
 */
public class TestDataFactory {

  // ====================
  // 用户相关测试数据
  // ====================

  /** 创建测试用户（Entity） */
  public static SysUser createUser(Long userId, String userName) {
    SysUser user = new SysUser();
    user.setUserId(userId);
    user.setUserName(userName);
    user.setNickName("测试用户" + (userId != null ? userId : ""));
    user.setEmail(userName + "@test.com");
    user.setPhonenumber("13800138" + String.format("%03d", userId != null ? userId % 1000 : 0));
    user.setStatus("0"); // 正常
    user.setDelFlag("0"); // 未删除
    user.setDeptId(100L);
    user.setCreateTime(new Date());
    return user;
  }

  /** 创建测试用户 BO */
  public static SysUserBo createUserBo(Long userId, String userName) {
    SysUserBo bo = new SysUserBo();
    bo.setUserId(userId);
    bo.setUserName(userName);
    bo.setNickName("测试用户" + (userId != null ? userId : ""));
    bo.setEmail(userName + "@test.com");
    bo.setPhonenumber("13800138" + String.format("%03d", userId != null ? userId % 1000 : 0));
    bo.setStatus("0");
    bo.setDeptId(100L);
    return bo;
  }

  /** 创建测试用户 VO */
  public static SysUserVo createUserVo(Long userId, String userName) {
    SysUserVo vo = new SysUserVo();
    vo.setUserId(userId);
    vo.setUserName(userName);
    vo.setNickName("测试用户" + (userId != null ? userId : ""));
    vo.setEmail(userName + "@test.com");
    vo.setPhonenumber("13800138" + String.format("%03d", userId != null ? userId % 1000 : 0));
    vo.setStatus("0");
    vo.setDeptId(100L);
    vo.setCreateTime(new Date());
    return vo;
  }

  // ====================
  // 角色相关测试数据
  // ====================

  /** 创建测试角色（Entity） */
  public static SysRole createRole(Long roleId, String roleKey) {
    SysRole role = new SysRole();
    role.setRoleId(roleId);
    role.setRoleKey(roleKey);
    role.setRoleName("测试角色" + roleId);
    role.setRoleSort(1);
    role.setStatus("0"); // 正常
    role.setDelFlag("0"); // 未删除
    role.setDataScope("1"); // 全部数据权限
    role.setCreateTime(new Date());
    return role;
  }

  /** 创建测试角色 BO */
  public static SysRoleBo createRoleBo(Long roleId, String roleKey) {
    SysRoleBo bo = new SysRoleBo();
    bo.setRoleId(roleId);
    bo.setRoleKey(roleKey);
    bo.setRoleName("测试角色" + roleId);
    bo.setRoleSort(1);
    bo.setStatus("0");
    bo.setDataScope("1");
    return bo;
  }

  /** 创建测试角色 VO */
  public static SysRoleVo createRoleVo(Long roleId, String roleKey) {
    SysRoleVo vo = new SysRoleVo();
    vo.setRoleId(roleId);
    vo.setRoleKey(roleKey);
    vo.setRoleName("测试角色" + roleId);
    vo.setRoleSort(1);
    vo.setStatus("0");
    vo.setDataScope("1");
    vo.setCreateTime(new Date());
    return vo;
  }

  // ====================
  // 部门相关测试数据
  // ====================

  /** 创建测试部门（Entity） */
  public static SysDept createDept(Long deptId, String deptName) {
    SysDept dept = new SysDept();
    dept.setDeptId(deptId);
    dept.setDeptName(deptName);
    dept.setParentId(0L);
    dept.setAncestors("0");
    dept.setOrderNum(1);
    dept.setStatus("0"); // 正常
    dept.setDelFlag("0"); // 未删除
    dept.setCreateTime(new Date());
    return dept;
  }

  /** 创建测试部门 BO */
  public static SysDeptBo createDeptBo(Long deptId, String deptName) {
    SysDeptBo bo = new SysDeptBo();
    bo.setDeptId(deptId);
    bo.setDeptName(deptName);
    bo.setParentId(0L);
    bo.setOrderNum(1);
    bo.setStatus("0");
    return bo;
  }

  /** 创建测试部门 VO */
  public static SysDeptVo createDeptVo(Long deptId, String deptName) {
    SysDeptVo vo = new SysDeptVo();
    vo.setDeptId(deptId);
    vo.setDeptName(deptName);
    vo.setParentId(0L);
    vo.setAncestors("0");
    vo.setOrderNum(1);
    vo.setStatus("0");
    vo.setCreateTime(new Date());
    return vo;
  }

  // ====================
  // 菜单相关测试数据
  // ====================

  /** 创建测试菜单（Entity） */
  public static SysMenu createMenu(Long menuId, String menuName) {
    SysMenu menu = new SysMenu();
    menu.setMenuId(menuId);
    menu.setMenuName(menuName);
    menu.setParentId(0L);
    menu.setMenuType("M"); // 目录
    menu.setPath("/" + menuName.toLowerCase());
    menu.setVisible("0"); // 显示
    menu.setStatus("0"); // 正常
    menu.setOrderNum(1);
    menu.setCreateTime(new Date());
    return menu;
  }

  /** 创建测试菜单 BO */
  public static SysMenuBo createMenuBo(Long menuId, String menuName) {
    SysMenuBo bo = new SysMenuBo();
    bo.setMenuId(menuId);
    bo.setMenuName(menuName);
    bo.setParentId(0L);
    bo.setMenuType("M");
    bo.setPath("/" + menuName.toLowerCase());
    bo.setVisible("0");
    bo.setStatus("0");
    bo.setOrderNum(1);
    return bo;
  }

  /** 创建测试菜单 VO */
  public static SysMenuVo createMenuVo(Long menuId, String menuName) {
    SysMenuVo vo = new SysMenuVo();
    vo.setMenuId(menuId);
    vo.setMenuName(menuName);
    vo.setParentId(0L);
    vo.setMenuType("M");
    vo.setPath("/" + menuName.toLowerCase());
    vo.setVisible("0");
    vo.setStatus("0");
    vo.setOrderNum(1);
    vo.setCreateTime(new Date());
    return vo;
  }

  // ====================
  // 岗位相关测试数据
  // ====================

  /** 创建测试岗位（Entity） */
  public static SysPost createPost(Long postId, String postCode) {
    SysPost post = new SysPost();
    post.setPostId(postId);
    post.setPostCode(postCode);
    post.setPostName("测试岗位" + postId);
    post.setPostSort(1);
    post.setStatus("0"); // 正常
    post.setCreateTime(new Date());
    return post;
  }

  /** 创建测试岗位 VO */
  public static SysPostVo createPostVo(Long postId, String postCode) {
    SysPostVo vo = new SysPostVo();
    vo.setPostId(postId);
    vo.setPostCode(postCode);
    vo.setPostName("测试岗位" + postId);
    vo.setPostSort(1);
    vo.setStatus("0");
    vo.setCreateTime(new Date());
    return vo;
  }

  // ====================
  // 字典相关测试数据
  // ====================

  /** 创建测试字典类型（Entity） */
  public static SysDictType createDictType(Long dictId, String dictType) {
    SysDictType type = new SysDictType();
    type.setDictId(dictId);
    type.setDictType(dictType);
    type.setDictName("测试字典" + dictId);
    type.setCreateTime(new Date());
    return type;
  }

  /** 创建测试字典数据（Entity） */
  public static SysDictData createDictData(Long dictCode, String dictType, String dictValue) {
    SysDictData data = new SysDictData();
    data.setDictCode(dictCode);
    data.setDictType(dictType);
    data.setDictLabel("测试字典值" + dictCode);
    data.setDictValue(dictValue);
    data.setDictSort(1);
    data.setCreateTime(new Date());
    return data;
  }

  // ====================
  // 客户端相关测试数据
  // ====================

  /** 创建测试客户端（Entity） */
  public static SysClient createClient(Long id, String clientKey) {
    SysClient client = new SysClient();
    client.setId(id);
    client.setClientKey(clientKey);
    client.setClientSecret("secret" + id);
    client.setClientId("test_client_id_" + id);
    client.setGrantType("password,client_credentials");
    client.setDeviceType("pc");
    client.setActiveTimeout(1800L);
    client.setTimeout(604800L);
    client.setStatus("0"); // 正常
    client.setDelFlag("0"); // 未删除
    client.setCreateTime(new Date());
    return client;
  }

  /** 创建测试客户端 BO */
  public static SysClientBo createClientBo(Long id, String clientKey) {
    SysClientBo bo = new SysClientBo();
    bo.setId(id);
    bo.setClientKey(clientKey);
    bo.setClientSecret("secret" + id);
    bo.setGrantTypeList(Arrays.asList("password", "client_credentials"));
    bo.setDeviceType("pc");
    bo.setActiveTimeout(1800L);
    bo.setTimeout(604800L);
    bo.setStatus("0");
    return bo;
  }

  /** 创建测试客户端 VO */
  public static SysClientVo createClientVo(Long id, String clientKey) {
    SysClientVo vo = new SysClientVo();
    vo.setId(id);
    vo.setClientKey(clientKey);
    vo.setClientSecret("secret" + id);
    vo.setClientId("test_client_id_" + id);
    vo.setGrantType("password,client_credentials");
    vo.setGrantTypeList(Arrays.asList("password", "client_credentials"));
    vo.setDeviceType("pc");
    vo.setActiveTimeout(1800L);
    vo.setTimeout(604800L);
    vo.setStatus("0");
    return vo;
  }

  // ====================
  // 社会化关系相关测试数据
  // ====================

  /** 创建测试社会化关系（Entity） */
  public static SysSocial createSocial(Long id, Long userId, String source) {
    SysSocial social = new SysSocial();
    social.setId(id);
    social.setUserId(userId);
    social.setAuthId("auth_" + source + "_" + userId);
    social.setSource(source); // wechat, github, qq等
    social.setAccessToken("access_token_" + id);
    social.setExpireIn(7200);
    social.setRefreshToken("refresh_token_" + id);
    social.setOpenId("open_id_" + id);
    social.setUserName("user_" + source);
    social.setNickName("昵称_" + source);
    social.setEmail(source + "@test.com");
    social.setAvatar("https://avatar.com/" + id + ".jpg");
    social.setCreateTime(new Date());
    return social;
  }

  /** 创建测试社会化关系 BO */
  public static SysSocialBo createSocialBo(Long id, Long userId, String source) {
    SysSocialBo bo = new SysSocialBo();
    bo.setId(id);
    bo.setUserId(userId);
    bo.setAuthId("auth_" + source + "_" + userId);
    bo.setSource(source);
    bo.setAccessToken("access_token_" + id);
    bo.setExpireIn(7200);
    bo.setRefreshToken("refresh_token_" + id);
    bo.setOpenId("open_id_" + id);
    bo.setUserName("user_" + source);
    bo.setNickName("昵称_" + source);
    bo.setEmail(source + "@test.com");
    bo.setAvatar("https://avatar.com/" + id + ".jpg");
    return bo;
  }

  /** 创建测试社会化关系 VO */
  public static SysSocialVo createSocialVo(Long id, Long userId, String source) {
    SysSocialVo vo = new SysSocialVo();
    vo.setId(id);
    vo.setUserId(userId);
    vo.setAuthId("auth_" + source + "_" + userId);
    vo.setSource(source);
    vo.setAccessToken("access_token_" + id);
    vo.setExpireIn(7200);
    vo.setRefreshToken("refresh_token_" + id);
    vo.setOpenId("open_id_" + id);
    vo.setUserName("user_" + source);
    vo.setNickName("昵称_" + source);
    vo.setEmail(source + "@test.com");
    vo.setAvatar("https://avatar.com/" + id + ".jpg");
    vo.setCreateTime(new Date());
    return vo;
  }

  // ====================
  // 通用列表创建方法
  // ====================

  /** 创建用户列表 */
  public static List<SysUserVo> createUserList(int count) {
    return Arrays.asList(
            createUserVo(1L, "testuser1"),
            createUserVo(2L, "testuser2"),
            createUserVo(3L, "testuser3"))
        .subList(0, Math.min(count, 3));
  }

  /** 创建角色列表 */
  public static List<SysRoleVo> createRoleList(int count) {
    return Arrays.asList(
            createRoleVo(1L, "admin"), createRoleVo(2L, "user"), createRoleVo(3L, "guest"))
        .subList(0, Math.min(count, 3));
  }
}
