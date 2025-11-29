package org.dromara.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.dromara.system.domain.SysNotice;
import org.dromara.system.domain.SysUser;
import org.dromara.system.domain.bo.SysNoticeBo;
import org.dromara.system.domain.vo.SysNoticeVo;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.mapper.SysNoticeMapper;
import org.dromara.system.mapper.SysUserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SysNoticeServiceImpl 单元测试
 *
 * @author Claude Code
 * @date 2025-11-07
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysNoticeServiceImpl 单元测试")
class SysNoticeServiceImplTest {

  @Mock private SysNoticeMapper baseMapper;

  @Mock private SysUserMapper userMapper;

  @InjectMocks private SysNoticeServiceImpl noticeService;

  @Captor private ArgumentCaptor<LambdaQueryWrapper<SysNotice>> noticeWrapperCaptor;

  @Captor private ArgumentCaptor<LambdaQueryWrapper<SysUser>> userWrapperCaptor;

  @Nested
  @DisplayName("1. 查询方法测试")
  class QueryMethodsTests {

    @Test
    @DisplayName("应该根据公告标题查询公告列表")
    void shouldReturnNoticeList_WhenQueryByNoticeTitle() {
      // Arrange
      SysNoticeBo queryBo = createNoticeBo(null, "系统维护");
      queryBo.setNoticeTitle("系统维护");

      List<SysNoticeVo> expectedList =
          Arrays.asList(
              createNoticeVo(1L, "系统维护通知", "1", "0"), createNoticeVo(2L, "系统维护公告", "2", "0"));
      when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

      // Act
      List<SysNoticeVo> result = noticeService.selectNoticeList(queryBo);

      // Assert
      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .extracting(SysNoticeVo::getNoticeTitle)
          .allMatch(title -> title.contains("系统维护"));

      verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该根据公告类型查询公告列表")
    void shouldReturnNoticeList_WhenQueryByNoticeType() {
      // Arrange
      SysNoticeBo queryBo = createNoticeBo(null, "");
      queryBo.setNoticeType("1"); // 通知类型

      List<SysNoticeVo> expectedList =
          Arrays.asList(
              createNoticeVo(1L, "系统维护通知", "1", "0"), createNoticeVo(2L, "版本更新通知", "1", "0"));
      when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

      // Act
      List<SysNoticeVo> result = noticeService.selectNoticeList(queryBo);

      // Assert
      assertThat(result)
          .isNotNull()
          .hasSize(2)
          .extracting(SysNoticeVo::getNoticeType)
          .containsOnly("1");

      verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该根据创建人名称查询公告列表")
    void shouldReturnNoticeList_WhenQueryByCreateByName() {
      // Arrange
      SysNoticeBo queryBo = createNoticeBo(null, "");
      queryBo.setCreateByName("admin");

      SysUserVo foundUser = new SysUserVo();
      foundUser.setUserId(1L);
      foundUser.setUserName("admin");

      when(userMapper.selectVoOne(any(LambdaQueryWrapper.class))).thenReturn(foundUser);

      List<SysNoticeVo> expectedList =
          Collections.singletonList(createNoticeVo(1L, "管理员发布的通知", "1", "0"));
      when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

      // Act
      List<SysNoticeVo> result = noticeService.selectNoticeList(queryBo);

      // Assert
      assertThat(result).isNotNull().hasSize(1);

      verify(userMapper, times(1)).selectVoOne(any(LambdaQueryWrapper.class));
      verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该返回空列表_当创建人不存在")
    void shouldReturnEmptyList_WhenCreateByNameNotFound() {
      // Arrange
      SysNoticeBo queryBo = createNoticeBo(null, "");
      queryBo.setCreateByName("nonexistent");

      when(userMapper.selectVoOne(any(LambdaQueryWrapper.class))).thenReturn(null);
      when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
          .thenReturn(Collections.emptyList());

      // Act
      List<SysNoticeVo> result = noticeService.selectNoticeList(queryBo);

      // Assert
      assertThat(result).isNotNull().isEmpty();

      verify(userMapper, times(1)).selectVoOne(any(LambdaQueryWrapper.class));
      verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该返回空列表_当没有公告匹配")
    void shouldReturnEmptyList_WhenNoNoticesMatch() {
      // Arrange
      SysNoticeBo queryBo = createNoticeBo(null, "不存在的公告");
      when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
          .thenReturn(Collections.emptyList());

      // Act
      List<SysNoticeVo> result = noticeService.selectNoticeList(queryBo);

      // Assert
      assertThat(result).isNotNull().isEmpty();

      verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该返回公告VO_当根据ID查询存在的公告")
    void shouldReturnNoticeVo_WhenQueryById() {
      // Arrange
      Long noticeId = 1L;
      SysNoticeVo expectedNotice = createNoticeVo(noticeId, "系统维护通知", "1", "0");

      when(baseMapper.selectVoById(noticeId)).thenReturn(expectedNotice);

      // Act
      SysNoticeVo result = noticeService.selectNoticeById(noticeId);

      // Assert
      assertThat(result)
          .isNotNull()
          .extracting(
              SysNoticeVo::getNoticeId,
              SysNoticeVo::getNoticeTitle,
              SysNoticeVo::getNoticeType,
              SysNoticeVo::getStatus)
          .containsExactly(noticeId, "系统维护通知", "1", "0");

      verify(baseMapper, times(1)).selectVoById(noticeId);
    }

    @Test
    @DisplayName("应该返回null_当根据ID查询不存在的公告")
    void shouldReturnNull_WhenNoticeIdDoesNotExist() {
      // Arrange
      Long nonExistentId = 999L;
      when(baseMapper.selectVoById(nonExistentId)).thenReturn(null);

      // Act
      SysNoticeVo result = noticeService.selectNoticeById(nonExistentId);

      // Assert
      assertThat(result).isNull();
      verify(baseMapper, times(1)).selectVoById(nonExistentId);
    }

    @Test
    @DisplayName("应该根据多个条件查询公告列表")
    void shouldReturnNoticeList_WhenQueryByMultipleConditions() {
      // Arrange
      SysNoticeBo queryBo = createNoticeBo(null, "系统通知");
      queryBo.setNoticeTitle("系统通知");
      queryBo.setNoticeType("1");

      List<SysNoticeVo> expectedList =
          Collections.singletonList(createNoticeVo(1L, "系统通知", "1", "0"));
      when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedList);

      // Act
      List<SysNoticeVo> result = noticeService.selectNoticeList(queryBo);

      // Assert
      assertThat(result).isNotNull().hasSize(1);

      verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
    }
  }

  /**
   * 注意: 无法测试分页方法
   *
   * <p><b>selectPageNoticeList</b> - 需要 MyBatis-Plus Page 对象和完整分页设置<br>
   */
  @Nested
  @DisplayName("2. 删除方法测试")
  class DeleteMethodsTests {

    @Test
    @DisplayName("应该成功删除单个公告")
    void shouldDeleteNotice_WhenDeleteById() {
      // Arrange
      Long noticeId = 1L;
      when(baseMapper.deleteById(noticeId)).thenReturn(1);

      // Act
      int result = noticeService.deleteNoticeById(noticeId);

      // Assert
      assertThat(result).isEqualTo(1);
      verify(baseMapper, times(1)).deleteById(noticeId);
    }

    @Test
    @DisplayName("应该返回0_当删除不存在的公告")
    void shouldReturn0_WhenDeletingNonExistentNotice() {
      // Arrange
      Long nonExistentId = 999L;
      when(baseMapper.deleteById(nonExistentId)).thenReturn(0);

      // Act
      int result = noticeService.deleteNoticeById(nonExistentId);

      // Assert
      assertThat(result).isEqualTo(0);
      verify(baseMapper, times(1)).deleteById(nonExistentId);
    }

    @Test
    @DisplayName("应该成功批量删除公告_使用单个ID")
    void shouldDeleteNotices_WhenBatchDeleteWithSingleId() {
      // Arrange
      Long[] noticeIds = {1L};
      when(baseMapper.deleteByIds(any(List.class))).thenReturn(1);

      // Act
      int result = noticeService.deleteNoticeByIds(noticeIds);

      // Assert
      assertThat(result).isEqualTo(1);
      verify(baseMapper, times(1)).deleteByIds(Arrays.asList(noticeIds));
    }

    @Test
    @DisplayName("应该成功批量删除公告_使用多个ID")
    void shouldDeleteNotices_WhenBatchDeleteWithMultipleIds() {
      // Arrange
      Long[] noticeIds = {1L, 2L, 3L};
      when(baseMapper.deleteByIds(any(List.class))).thenReturn(3);

      // Act
      int result = noticeService.deleteNoticeByIds(noticeIds);

      // Assert
      assertThat(result).isEqualTo(3);
      verify(baseMapper, times(1)).deleteByIds(Arrays.asList(noticeIds));
    }

    @Test
    @DisplayName("应该返回0_当批量删除空ID数组")
    void shouldReturn0_WhenBatchDeleteWithEmptyArray() {
      // Arrange
      Long[] emptyIds = {};
      when(baseMapper.deleteByIds(any(List.class))).thenReturn(0);

      // Act
      int result = noticeService.deleteNoticeByIds(emptyIds);

      // Assert
      assertThat(result).isEqualTo(0);
      verify(baseMapper, times(1)).deleteByIds(Collections.emptyList());
    }
  }

  /**
   * 注意: 无法测试 CRUD 相关方法
   *
   * <p><b>insertNotice()</b> - 使用 MapstructUtils.convert()，需要静态方法 mock<br>
   * <b>updateNotice()</b> - 使用 MapstructUtils.convert()，需要静态方法 mock<br>
   * 需要 mockito-inline 或集成测试环境才能测试这些方法
   */
  @Nested
  @DisplayName("3. 边界条件测试")
  class BoundaryTests {

    @Test
    @DisplayName("应该处理null公告ID_selectNoticeById")
    void shouldHandleNullNoticeId_selectNoticeById() {
      // Arrange
      Long nullNoticeId = null;
      when(baseMapper.selectVoById(nullNoticeId)).thenReturn(null);

      // Act
      SysNoticeVo result = noticeService.selectNoticeById(nullNoticeId);

      // Assert
      assertThat(result).isNull();
      verify(baseMapper, times(1)).selectVoById(nullNoticeId);
    }

    @Test
    @DisplayName("应该处理null公告ID_deleteNoticeById")
    void shouldHandleNullNoticeId_deleteNoticeById() {
      // Arrange
      Long nullNoticeId = null;
      when(baseMapper.deleteById(nullNoticeId)).thenReturn(0);

      // Act
      int result = noticeService.deleteNoticeById(nullNoticeId);

      // Assert
      assertThat(result).isEqualTo(0);
      verify(baseMapper, times(1)).deleteById(nullNoticeId);
    }

    @Test
    @DisplayName("应该处理最大Long值_selectNoticeById")
    void shouldHandleMaxLongValue_selectNoticeById() {
      // Arrange
      Long maxNoticeId = Long.MAX_VALUE;
      when(baseMapper.selectVoById(maxNoticeId)).thenReturn(null);

      // Act
      SysNoticeVo result = noticeService.selectNoticeById(maxNoticeId);

      // Assert
      assertThat(result).isNull();
      verify(baseMapper, times(1)).selectVoById(maxNoticeId);
    }

    @Test
    @DisplayName("应该处理空字符串公告标题")
    void shouldHandleEmptyStringNoticeTitle() {
      // Arrange
      SysNoticeBo queryBo = createNoticeBo(null, "");
      queryBo.setNoticeTitle("");
      when(baseMapper.selectVoList(any(LambdaQueryWrapper.class)))
          .thenReturn(Collections.emptyList());

      // Act
      List<SysNoticeVo> result = noticeService.selectNoticeList(queryBo);

      // Assert
      assertThat(result).isNotNull().isEmpty();

      verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
    }
  }

  // ==================== Factory Methods ====================

  /** 创建测试用 SysNoticeBo 业务对象 */
  private static SysNoticeBo createNoticeBo(Long noticeId, String noticeTitle) {
    SysNoticeBo noticeBo = new SysNoticeBo();
    noticeBo.setNoticeId(noticeId);
    noticeBo.setNoticeTitle(noticeTitle);
    noticeBo.setNoticeType("1");
    noticeBo.setNoticeContent("测试公告内容");
    noticeBo.setStatus("0");
    noticeBo.setRemark("测试备注");
    return noticeBo;
  }

  /** 创建测试用 SysNoticeVo 视图对象 */
  private static SysNoticeVo createNoticeVo(
      Long noticeId, String noticeTitle, String noticeType, String status) {
    SysNoticeVo noticeVo = new SysNoticeVo();
    noticeVo.setNoticeId(noticeId);
    noticeVo.setNoticeTitle(noticeTitle);
    noticeVo.setNoticeType(noticeType);
    noticeVo.setNoticeContent("测试公告内容");
    noticeVo.setStatus(status);
    noticeVo.setRemark("测试备注");
    noticeVo.setCreateBy(1L);
    noticeVo.setCreateByName("admin");
    return noticeVo;
  }
}
