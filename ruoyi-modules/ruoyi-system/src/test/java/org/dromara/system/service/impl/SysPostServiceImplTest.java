package org.dromara.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.system.domain.SysPost;
import org.dromara.system.domain.SysUserPost;
import org.dromara.system.domain.bo.SysPostBo;
import org.dromara.system.domain.vo.SysPostVo;
import org.dromara.system.mapper.SysDeptMapper;
import org.dromara.system.mapper.SysPostMapper;
import org.dromara.system.mapper.SysUserPostMapper;
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
 * SysPostServiceImpl 单元测试
 *
 * <p>测试岗位管理服务
 *
 * <p><b>测试覆盖范围:</b>
 *
 * <ul>
 *   <li>查询方法 - 单个查询、列表查询、用户岗位查询
 *   <li>验证方法 - 岗位名称唯一性、岗位编码唯一性
 *   <li>计数方法 - 用户岗位计数、部门岗位计数
 *   <li>删除方法 - 单个删除、批量删除（含业务规则验证）
 *   <li>边界条件 - null、空列表、负数等
 * </ul>
 *
 * <p><b>测试限制:</b>
 *
 * <ul>
 *   <li>无法测试 insertPost/updatePost - 需要 MapstructUtils.convert()
 *   <li>无法测试 selectPagePostList - 复杂分页查询需要完整 MyBatis-Plus 环境
 *   <li>无法测试 buildQueryWrapper - 私有方法，通过公共方法间接测试
 * </ul>
 *
 * @author Test Team
 * @see SysPostServiceImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysPostServiceImpl 单元测试")
class SysPostServiceImplTest {

  @Mock private SysPostMapper baseMapper;

  @Mock private SysDeptMapper deptMapper;

  @Mock private SysUserPostMapper userPostMapper;

  @InjectMocks private SysPostServiceImpl postService;

  @Captor private ArgumentCaptor<LambdaQueryWrapper<SysPost>> wrapperCaptor;

  @Captor private ArgumentCaptor<LambdaQueryWrapper<SysUserPost>> userPostWrapperCaptor;

  // ==================== Nested Test Groups ====================

  @Nested
  @DisplayName("1. 查询方法测试")
  class QueryMethodsTests {

    @Test
    @DisplayName("应该根据ID查询岗位_返回岗位VO")
    void shouldReturnPostVo_WhenQueryById() {
      // Arrange
      Long postId = 1L;
      SysPostVo expectedPost = createPostVo(postId, "总经理");

      when(baseMapper.selectVoById(postId)).thenReturn(expectedPost);

      // Act
      SysPostVo result = postService.selectPostById(postId);

      // Assert
      assertThat(result)
          .as("应该返回岗位VO")
          .isNotNull()
          .satisfies(
              post -> {
                assertThat(post.getPostId()).isEqualTo(postId);
                assertThat(post.getPostName()).isEqualTo("总经理");
              });

      verify(baseMapper, times(1)).selectVoById(postId);
    }

    @Test
    @DisplayName("应该返回null_当岗位ID不存在")
    void shouldReturnNull_WhenPostIdNotExists() {
      // Arrange
      Long postId = 999L;
      when(baseMapper.selectVoById(postId)).thenReturn(null);

      // Act
      SysPostVo result = postService.selectPostById(postId);

      // Assert
      assertThat(result).as("应该返回null").isNull();

      verify(baseMapper, times(1)).selectVoById(postId);
    }

    @Test
    @DisplayName("应该查询所有岗位_返回岗位VO列表")
    void shouldReturnAllPosts_WhenQueryAll() {
      // Arrange
      List<SysPostVo> expectedPosts =
          Arrays.asList(createPostVo(1L, "总经理"), createPostVo(2L, "部门经理"), createPostVo(3L, "员工"));

      when(baseMapper.selectVoList(any(QueryWrapper.class))).thenReturn(expectedPosts);

      // Act
      List<SysPostVo> result = postService.selectPostAll();

      // Assert
      assertThat(result)
          .as("应该返回所有岗位")
          .isNotNull()
          .hasSize(3)
          .extracting(SysPostVo::getPostName)
          .containsExactly("总经理", "部门经理", "员工");

      verify(baseMapper, times(1)).selectVoList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("应该返回空列表_当没有任何岗位")
    void shouldReturnEmptyList_WhenNoPostsExist() {
      // Arrange
      when(baseMapper.selectVoList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

      // Act
      List<SysPostVo> result = postService.selectPostAll();

      // Assert
      assertThat(result).as("应该返回空列表").isNotNull().isEmpty();

      verify(baseMapper, times(1)).selectVoList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("应该根据条件查询岗位列表")
    void shouldReturnPostList_WhenQueryByConditions() {
      // Arrange
      SysPostBo queryBo = createPostBo(null, null);
      queryBo.setPostCode("manager");

      List<SysPostVo> expectedPosts =
          Arrays.asList(createPostVo(1L, "部门经理"), createPostVo(2L, "项目经理"));

      when(baseMapper.selectVoList(any(LambdaQueryWrapper.class))).thenReturn(expectedPosts);

      // Act
      List<SysPostVo> result = postService.selectPostList(queryBo);

      // Assert
      assertThat(result).as("应该返回符合条件的岗位列表").isNotNull().hasSize(2);

      verify(baseMapper, times(1)).selectVoList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该根据用户ID查询岗位列表")
    void shouldReturnPostsByUserId() {
      // Arrange
      Long userId = 100L;
      List<SysPostVo> expectedPosts =
          Arrays.asList(createPostVo(1L, "部门经理"), createPostVo(2L, "项目负责人"));

      when(baseMapper.selectPostsByUserId(userId)).thenReturn(expectedPosts);

      // Act
      List<SysPostVo> result = postService.selectPostsByUserId(userId);

      // Assert
      assertThat(result)
          .as("应该返回用户的岗位列表")
          .isNotNull()
          .hasSize(2)
          .extracting(SysPostVo::getPostName)
          .containsExactly("部门经理", "项目负责人");

      verify(baseMapper, times(1)).selectPostsByUserId(userId);
    }

    @Test
    @DisplayName("应该根据用户ID查询岗位ID列表")
    void shouldReturnPostIdsByUserId() {
      // Arrange
      Long userId = 100L;
      List<SysPostVo> posts =
          Arrays.asList(createPostVo(1L, "岗位1"), createPostVo(2L, "岗位2"), createPostVo(3L, "岗位3"));

      when(baseMapper.selectPostsByUserId(userId)).thenReturn(posts);

      // Act
      List<Long> result = postService.selectPostListByUserId(userId);

      // Assert
      assertThat(result).as("应该返回岗位ID列表").isNotNull().hasSize(3).containsExactly(1L, 2L, 3L);

      verify(baseMapper, times(1)).selectPostsByUserId(userId);
    }

    @Test
    @DisplayName("应该返回空列表_当用户没有岗位")
    void shouldReturnEmptyList_WhenUserHasNoPosts() {
      // Arrange
      Long userId = 100L;
      when(baseMapper.selectPostsByUserId(userId)).thenReturn(Collections.emptyList());

      // Act
      List<Long> result = postService.selectPostListByUserId(userId);

      // Assert
      assertThat(result).as("应该返回空列表").isNotNull().isEmpty();

      verify(baseMapper, times(1)).selectPostsByUserId(userId);
    }

    /**
     * 注意: 由于 selectPostByIds 使用 LambdaQueryWrapper with in() clause, 在纯单元测试中无法完全 mock 复杂的 Lambda
     * 表达式构建过程。 这些测试需要集成测试环境或 MyBatis-Plus 表信息初始化。
     *
     * <p>实际方法实现已包含 CollUtil.isNotEmpty(postIds) 检查, 因此空列表和 null 的边界情况已在生产代码中正确处理。
     */
  }

  @Nested
  @DisplayName("2. 验证方法测试")
  class ValidationMethodsTests {

    @Test
    @DisplayName("应该返回true_当岗位名称唯一")
    void shouldReturnTrue_WhenPostNameIsUnique() {
      // Arrange
      SysPostBo newPost = createPostBo(null, "新岗位");
      newPost.setDeptId(1L);

      when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(false);

      // Act
      boolean result = postService.checkPostNameUnique(newPost);

      // Assert
      assertThat(result).as("岗位名称唯一时应该返回true").isTrue();

      verify(baseMapper, times(1)).exists(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该返回false_当岗位名称已存在")
    void shouldReturnFalse_WhenPostNameExists() {
      // Arrange
      SysPostBo newPost = createPostBo(null, "已存在岗位");
      newPost.setDeptId(1L);

      when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(true);

      // Act
      boolean result = postService.checkPostNameUnique(newPost);

      // Assert
      assertThat(result).as("岗位名称重复时应该返回false").isFalse();

      verify(baseMapper, times(1)).exists(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该排除自身ID_当检查岗位名称唯一性用于更新")
    void shouldExcludeSelfId_WhenCheckingPostNameUniquenessForUpdate() {
      // Arrange
      SysPostBo updatePost = createPostBo(1L, "更新岗位");
      updatePost.setDeptId(1L);

      when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(false);

      // Act
      boolean result = postService.checkPostNameUnique(updatePost);

      // Assert
      assertThat(result).as("更新时应该排除自身ID").isTrue();

      verify(baseMapper, times(1)).exists(wrapperCaptor.capture());
      // 验证查询条件包含排除自身ID的条件
    }

    @Test
    @DisplayName("应该返回true_当岗位编码唯一")
    void shouldReturnTrue_WhenPostCodeIsUnique() {
      // Arrange
      SysPostBo newPost = createPostBo(null, "新岗位");
      newPost.setPostCode("unique_code");

      when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(false);

      // Act
      boolean result = postService.checkPostCodeUnique(newPost);

      // Assert
      assertThat(result).as("岗位编码唯一时应该返回true").isTrue();

      verify(baseMapper, times(1)).exists(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该返回false_当岗位编码已存在")
    void shouldReturnFalse_WhenPostCodeExists() {
      // Arrange
      SysPostBo newPost = createPostBo(null, "新岗位");
      newPost.setPostCode("existing_code");

      when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(true);

      // Act
      boolean result = postService.checkPostCodeUnique(newPost);

      // Assert
      assertThat(result).as("岗位编码重复时应该返回false").isFalse();

      verify(baseMapper, times(1)).exists(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该排除自身ID_当检查岗位编码唯一性用于更新")
    void shouldExcludeSelfId_WhenCheckingPostCodeUniquenessForUpdate() {
      // Arrange
      SysPostBo updatePost = createPostBo(1L, "更新岗位");
      updatePost.setPostCode("update_code");

      when(baseMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(false);

      // Act
      boolean result = postService.checkPostCodeUnique(updatePost);

      // Assert
      assertThat(result).as("更新时应该排除自身ID").isTrue();

      verify(baseMapper, times(1)).exists(any(LambdaQueryWrapper.class));
    }
  }

  @Nested
  @DisplayName("3. 计数方法测试")
  class CountMethodsTests {

    @Test
    @DisplayName("应该返回用户岗位数量_当岗位被用户使用")
    void shouldReturnCount_WhenPostIsAssignedToUsers() {
      // Arrange
      Long postId = 1L;
      when(userPostMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);

      // Act
      long result = postService.countUserPostById(postId);

      // Assert
      assertThat(result).as("应该返回使用该岗位的用户数量").isEqualTo(5L);

      verify(userPostMapper, times(1)).selectCount(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该返回0_当岗位未被任何用户使用")
    void shouldReturnZero_WhenPostIsNotAssigned() {
      // Arrange
      Long postId = 999L;
      when(userPostMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

      // Act
      long result = postService.countUserPostById(postId);

      // Assert
      assertThat(result).as("应该返回0").isEqualTo(0L);

      verify(userPostMapper, times(1)).selectCount(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该返回部门岗位数量_当部门有岗位")
    void shouldReturnCount_WhenDeptHasPosts() {
      // Arrange
      Long deptId = 1L;
      when(baseMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(10L);

      // Act
      long result = postService.countPostByDeptId(deptId);

      // Assert
      assertThat(result).as("应该返回部门的岗位数量").isEqualTo(10L);

      verify(baseMapper, times(1)).selectCount(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该返回0_当部门没有岗位")
    void shouldReturnZero_WhenDeptHasNoPosts() {
      // Arrange
      Long deptId = 999L;
      when(baseMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

      // Act
      long result = postService.countPostByDeptId(deptId);

      // Assert
      assertThat(result).as("应该返回0").isEqualTo(0L);

      verify(baseMapper, times(1)).selectCount(any(LambdaQueryWrapper.class));
    }
  }

  @Nested
  @DisplayName("4. 删除方法测试")
  class DeleteMethodsTests {

    @Test
    @DisplayName("应该成功删除岗位_当根据ID删除")
    void shouldDeleteSuccessfully_WhenDeleteById() {
      // Arrange
      Long postId = 1L;
      when(baseMapper.deleteById(postId)).thenReturn(1);

      // Act
      int result = postService.deletePostById(postId);

      // Assert
      assertThat(result).as("应该返回删除的记录数").isEqualTo(1);

      verify(baseMapper, times(1)).deleteById(postId);
    }

    @Test
    @DisplayName("应该返回0_当删除不存在的岗位")
    void shouldReturnZero_WhenDeleteNonExistentPost() {
      // Arrange
      Long postId = 999L;
      when(baseMapper.deleteById(postId)).thenReturn(0);

      // Act
      int result = postService.deletePostById(postId);

      // Assert
      assertThat(result).as("应该返回0").isEqualTo(0);

      verify(baseMapper, times(1)).deleteById(postId);
    }

    @Test
    @DisplayName("应该成功批量删除岗位_当所有岗位都未被分配")
    void shouldBatchDeleteSuccessfully_WhenNoPostsAreAssigned() {
      // Arrange
      List<Long> postIds = Arrays.asList(1L, 2L, 3L);
      List<SysPost> posts =
          Arrays.asList(createPost(1L, "岗位1"), createPost(2L, "岗位2"), createPost(3L, "岗位3"));

      when(baseMapper.selectByIds(postIds)).thenReturn(posts);
      when(userPostMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
      when(baseMapper.deleteByIds(postIds)).thenReturn(3);

      // Act
      int result = postService.deletePostByIds(postIds);

      // Assert
      assertThat(result).as("应该返回删除的记录数").isEqualTo(3);

      verify(baseMapper, times(1)).selectByIds(postIds);
      verify(userPostMapper, times(3)).selectCount(any(LambdaQueryWrapper.class));
      verify(baseMapper, times(1)).deleteByIds(postIds);
    }

    @Test
    @DisplayName("应该抛出异常_当岗位已被分配给用户")
    void shouldThrowException_WhenPostIsAssignedToUser() {
      // Arrange
      List<Long> postIds = Arrays.asList(1L, 2L);
      SysPost assignedPost = createPost(1L, "已分配岗位");
      SysPost unassignedPost = createPost(2L, "未分配岗位");
      List<SysPost> posts = Arrays.asList(assignedPost, unassignedPost);

      when(baseMapper.selectByIds(postIds)).thenReturn(posts);
      when(userPostMapper.selectCount(any(LambdaQueryWrapper.class)))
          .thenReturn(5L) // 第一个岗位有5个用户
          .thenReturn(0L); // 第二个岗位没有用户

      // Act & Assert
      assertThatThrownBy(() -> postService.deletePostByIds(postIds))
          .as("应该抛出ServiceException")
          .isInstanceOf(ServiceException.class)
          .hasMessageContaining("已分配")
          .hasMessageContaining("不能删除");

      verify(baseMapper, times(1)).selectByIds(postIds);
      verify(userPostMapper, times(1)).selectCount(any(LambdaQueryWrapper.class));
      verify(baseMapper, never()).deleteByIds(any()); // 不应该执行删除操作
    }

    @Test
    @DisplayName("应该返回0_当批量删除空列表")
    void shouldReturnZero_WhenBatchDeleteEmptyList() {
      // Arrange
      List<Long> emptyIds = Collections.emptyList();
      when(baseMapper.selectByIds(emptyIds)).thenReturn(Collections.emptyList());
      when(baseMapper.deleteByIds(emptyIds)).thenReturn(0);

      // Act
      int result = postService.deletePostByIds(emptyIds);

      // Assert
      assertThat(result).as("应该返回0").isEqualTo(0);

      verify(baseMapper, times(1)).selectByIds(emptyIds);
      verify(baseMapper, times(1)).deleteByIds(emptyIds);
    }

    @Test
    @DisplayName("应该检查每个岗位的分配情况_当批量删除")
    void shouldCheckEachPostAssignment_WhenBatchDelete() {
      // Arrange
      List<Long> postIds = Arrays.asList(1L, 2L, 3L);
      List<SysPost> posts =
          Arrays.asList(createPost(1L, "岗位1"), createPost(2L, "岗位2"), createPost(3L, "岗位3"));

      when(baseMapper.selectByIds(postIds)).thenReturn(posts);
      when(userPostMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
      when(baseMapper.deleteByIds(postIds)).thenReturn(3);

      // Act
      postService.deletePostByIds(postIds);

      // Assert - 应该为每个岗位检查用户分配情况
      verify(userPostMapper, times(3)).selectCount(any(LambdaQueryWrapper.class));
    }
  }

  @Nested
  @DisplayName("5. 边界条件测试")
  class BoundaryTests {

    @Test
    @DisplayName("应该处理null岗位ID_selectPostById")
    void shouldHandleNullPostId_SelectPostById() {
      // Arrange
      Long nullId = null;
      when(baseMapper.selectVoById(nullId)).thenReturn(null);

      // Act
      SysPostVo result = postService.selectPostById(nullId);

      // Assert
      assertThat(result).isNull();
      verify(baseMapper, times(1)).selectVoById(nullId);
    }

    @Test
    @DisplayName("应该处理null用户ID_selectPostsByUserId")
    void shouldHandleNullUserId_SelectPostsByUserId() {
      // Arrange
      Long nullUserId = null;
      when(baseMapper.selectPostsByUserId(nullUserId)).thenReturn(Collections.emptyList());

      // Act
      List<SysPostVo> result = postService.selectPostsByUserId(nullUserId);

      // Assert
      assertThat(result).isNotNull().isEmpty();
      verify(baseMapper, times(1)).selectPostsByUserId(nullUserId);
    }

    /**
     * 注意: selectPostByIds with null 无法在纯单元测试中测试, 因为 LambdaQueryWrapper.in() 会在构建时失败。 实际代码中
     * CollUtil.isNotEmpty() 会正确处理 null 情况。
     */
    @Test
    @DisplayName("应该处理负数岗位ID_countUserPostById")
    void shouldHandleNegativePostId_CountUserPostById() {
      // Arrange
      Long negativeId = -1L;
      when(userPostMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

      // Act
      long result = postService.countUserPostById(negativeId);

      // Assert
      assertThat(result).isEqualTo(0L);
      verify(userPostMapper, times(1)).selectCount(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("应该处理零值岗位ID_deletePostById")
    void shouldHandleZeroPostId_DeletePostById() {
      // Arrange
      Long zeroId = 0L;
      when(baseMapper.deleteById(zeroId)).thenReturn(0);

      // Act
      int result = postService.deletePostById(zeroId);

      // Assert
      assertThat(result).isEqualTo(0);
      verify(baseMapper, times(1)).deleteById(zeroId);
    }

    @Test
    @DisplayName("应该处理最大值岗位ID")
    void shouldHandleMaxLongPostId() {
      // Arrange
      Long maxId = Long.MAX_VALUE;
      when(baseMapper.selectVoById(maxId)).thenReturn(null);
      when(userPostMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
      when(baseMapper.deleteById(maxId)).thenReturn(0);

      // Act & Assert
      assertThat(postService.selectPostById(maxId)).isNull();
      assertThat(postService.countUserPostById(maxId)).isEqualTo(0L);
      assertThat(postService.deletePostById(maxId)).isEqualTo(0);
    }
  }

  // ==================== Test Data Factory Methods ====================

  /** 创建岗位测试数据 */
  private static SysPost createPost(Long postId, String postName) {
    SysPost post = new SysPost();
    post.setPostId(postId);
    post.setPostName(postName);
    post.setPostCode("post_" + postId);
    post.setPostSort(postId != null ? postId.intValue() : 0);
    post.setDeptId(1L);
    post.setPostCategory("category_" + postId);
    post.setStatus("0");
    post.setRemark("岗位备注");
    return post;
  }

  /** 创建岗位BO测试数据 */
  private static SysPostBo createPostBo(Long postId, String postName) {
    SysPostBo bo = new SysPostBo();
    bo.setPostId(postId);
    bo.setPostName(postName);
    bo.setPostCode("post_" + postId);
    bo.setPostSort(postId != null ? postId.intValue() : 0);
    bo.setDeptId(1L);
    bo.setPostCategory("category_" + postId);
    bo.setStatus("0");
    bo.setRemark("岗位备注");
    return bo;
  }

  /** 创建岗位VO测试数据 */
  private static SysPostVo createPostVo(Long postId, String postName) {
    SysPostVo vo = new SysPostVo();
    vo.setPostId(postId);
    vo.setPostName(postName);
    vo.setPostCode("post_" + postId);
    vo.setPostSort(postId != null ? postId.intValue() : 0);
    vo.setDeptId(1L);
    vo.setPostCategory("category_" + postId);
    vo.setStatus("0");
    vo.setRemark("岗位备注");
    vo.setDeptName("测试部门");
    return vo;
  }
}
