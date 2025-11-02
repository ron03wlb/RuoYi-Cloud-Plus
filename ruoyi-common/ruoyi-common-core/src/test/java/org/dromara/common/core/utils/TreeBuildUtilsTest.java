package org.dromara.common.core.utils;

import cn.hutool.core.lang.tree.Tree;
import org.dromara.common.core.BaseUnitTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TreeBuildUtils 测试类
 * <p>
 * 测试树形结构构建工具类的各种功能，包括：
 * - 基本树构建
 * - 多根节点树构建
 * - 叶子节点提取
 * - 边界条件处理
 * </p>
 *
 * @author Test Team
 */
class TreeBuildUtilsTest extends BaseUnitTest {

    /**
     * 测试用树节点类
     */
    static class TreeNode {
        private Long id;
        private Long parentId;
        private String name;
        private Integer sort;

        public TreeNode(Long id, Long parentId, String name) {
            this.id = id;
            this.parentId = parentId;
            this.name = name;
            this.sort = 0;
        }

        public TreeNode(Long id, Long parentId, String name, Integer sort) {
            this.id = id;
            this.parentId = parentId;
            this.name = name;
            this.sort = sort;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getParentId() {
            return parentId;
        }

        public void setParentId(Long parentId) {
            this.parentId = parentId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getSort() {
            return sort;
        }

        public void setSort(Integer sort) {
            this.sort = sort;
        }
    }

    // ==================== build(List, NodeParser) 方法测试 ====================

    @Test
    void shouldReturnEmptyListWhenBuildWithEmptyList() {
        List<TreeNode> emptyList = new ArrayList<>();

        List<Tree<Long>> result = TreeBuildUtils.build(emptyList, (node, tree) -> {
            tree.setId(node.getId());
            tree.setParentId(node.getParentId());
            tree.setName(node.getName());
        });

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenBuildWithNullList() {
        List<TreeNode> nullList = null;
        List<Tree<Long>> result = TreeBuildUtils.build(nullList, (node, tree) -> {
            tree.setId(node.getId());
            tree.setParentId(node.getParentId());
            tree.setName(node.getName());
        });

        assertThat(result).isEmpty();
    }

    @Test
    void shouldBuildSingleNodeTree() {
        List<TreeNode> nodes = Arrays.asList(
            new TreeNode(1L, 0L, "根节点")
        );

        List<Tree<Long>> result = TreeBuildUtils.build(nodes, (node, tree) -> {
            tree.setId(node.getId());
            tree.setParentId(node.getParentId());
            tree.setName(node.getName());
        });

        assertThat(result)
            .hasSize(1)
            .first()
            .satisfies(tree -> {
                assertThat(tree.getId()).isEqualTo(1L);
                assertThat(tree.getName()).isEqualTo("根节点");
                assertThat(tree.hasChild()).isFalse();
            });
    }

    @Test
    void shouldBuildTwoLevelTree() {
        List<TreeNode> nodes = Arrays.asList(
            new TreeNode(1L, 0L, "根节点"),
            new TreeNode(2L, 1L, "子节点1"),
            new TreeNode(3L, 1L, "子节点2")
        );

        List<Tree<Long>> result = TreeBuildUtils.build(nodes, (node, tree) -> {
            tree.setId(node.getId());
            tree.setParentId(node.getParentId());
            tree.setName(node.getName());
        });

        assertThat(result)
            .hasSize(1)
            .first()
            .satisfies(root -> {
                assertThat(root.getId()).isEqualTo(1L);
                assertThat(root.getName()).isEqualTo("根节点");
                assertThat(root.getChildren()).hasSize(2);

                Tree<Long> child1 = root.getChildren().get(0);
                assertThat(child1.getId()).isEqualTo(2L);
                assertThat(child1.getName()).isEqualTo("子节点1");

                Tree<Long> child2 = root.getChildren().get(1);
                assertThat(child2.getId()).isEqualTo(3L);
                assertThat(child2.getName()).isEqualTo("子节点2");
            });
    }

    @Test
    void shouldBuildMultiLevelTree() {
        List<TreeNode> nodes = Arrays.asList(
            new TreeNode(1L, 0L, "根节点"),
            new TreeNode(2L, 1L, "子节点1"),
            new TreeNode(3L, 1L, "子节点2"),
            new TreeNode(4L, 2L, "孙节点1"),
            new TreeNode(5L, 2L, "孙节点2"),
            new TreeNode(6L, 3L, "孙节点3")
        );

        List<Tree<Long>> result = TreeBuildUtils.build(nodes, (node, tree) -> {
            tree.setId(node.getId());
            tree.setParentId(node.getParentId());
            tree.setName(node.getName());
        });

        assertThat(result).hasSize(1);

        Tree<Long> root = result.get(0);
        assertThat(root.getId()).isEqualTo(1L);
        assertThat(root.getChildren()).hasSize(2);

        // 验证第一个子节点及其子节点
        Tree<Long> child1 = root.getChildren().get(0);
        assertThat(child1.getId()).isEqualTo(2L);
        assertThat(child1.getChildren()).hasSize(2);
        assertThat(child1.getChildren().get(0).getId()).isEqualTo(4L);
        assertThat(child1.getChildren().get(1).getId()).isEqualTo(5L);

        // 验证第二个子节点及其子节点
        Tree<Long> child2 = root.getChildren().get(1);
        assertThat(child2.getId()).isEqualTo(3L);
        assertThat(child2.getChildren()).hasSize(1);
        assertThat(child2.getChildren().get(0).getId()).isEqualTo(6L);
    }

    // ==================== build(List, parentId, NodeParser) 方法测试 ====================

    @Test
    void shouldReturnEmptyListWhenBuildWithParentIdAndEmptyList() {
        List<TreeNode> emptyList = new ArrayList<>();

        List<Tree<Long>> result = TreeBuildUtils.build(emptyList, 0L, (node, tree) -> {
            tree.setId(node.getId());
            tree.setParentId(node.getParentId());
            tree.setName(node.getName());
        });

        assertThat(result).isEmpty();
    }

    @Test
    void shouldBuildTreeWithSpecificParentId() {
        List<TreeNode> nodes = Arrays.asList(
            new TreeNode(1L, 0L, "根节点1"),
            new TreeNode(2L, 0L, "根节点2"),
            new TreeNode(3L, 1L, "子节点1"),
            new TreeNode(4L, 2L, "子节点2")
        );

        // 只构建 parentId=0 的节点作为根
        List<Tree<Long>> result = TreeBuildUtils.build(nodes, 0L, (node, tree) -> {
            tree.setId(node.getId());
            tree.setParentId(node.getParentId());
            tree.setName(node.getName());
        });

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getChildren()).hasSize(1);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getChildren()).hasSize(1);
    }

    @Test
    void shouldBuildSubTreeFromSpecificNode() {
        List<TreeNode> nodes = Arrays.asList(
            new TreeNode(1L, 0L, "根节点"),
            new TreeNode(2L, 1L, "子节点1"),
            new TreeNode(3L, 1L, "子节点2"),
            new TreeNode(4L, 2L, "孙节点1")
        );

        // 从子节点2开始构建树
        List<Tree<Long>> result = TreeBuildUtils.build(nodes, 1L, (node, tree) -> {
            tree.setId(node.getId());
            tree.setParentId(node.getParentId());
            tree.setName(node.getName());
        });

        assertThat(result).hasSize(2);  // 子节点1 和 子节点2
        assertThat(result.get(0).getId()).isEqualTo(2L);
        assertThat(result.get(0).getChildren()).hasSize(1);  // 孙节点1
        assertThat(result.get(1).getId()).isEqualTo(3L);
        assertThat(result.get(1).hasChild()).isFalse();
    }

    // ==================== buildMultiRoot 方法测试 ====================

    @Test
    void shouldReturnEmptyListWhenBuildMultiRootWithEmptyList() {
        List<TreeNode> emptyList = new ArrayList<>();

        List<Tree<Long>> result = TreeBuildUtils.buildMultiRoot(
            emptyList,
            TreeNode::getId,
            TreeNode::getParentId,
            (node, tree) -> {
                tree.setId(node.getId());
                tree.setParentId(node.getParentId());
                tree.setName(node.getName());
            }
        );

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenBuildMultiRootWithNullList() {
        List<Tree<Long>> result = TreeBuildUtils.buildMultiRoot(
            null,
            TreeNode::getId,
            TreeNode::getParentId,
            (node, tree) -> {
                tree.setId(node.getId());
                tree.setParentId(node.getParentId());
                tree.setName(node.getName());
            }
        );

        assertThat(result).isEmpty();
    }

    @Test
    void shouldBuildMultipleRootTrees() {
        // 模拟多个独立的树（不同的根节点）
        List<TreeNode> nodes = Arrays.asList(
            // 第一棵树：根节点 parentId=100（不在节点列表中）
            new TreeNode(1L, 100L, "树1根节点"),
            new TreeNode(2L, 1L, "树1子节点1"),
            new TreeNode(3L, 1L, "树1子节点2"),
            // 第二棵树：根节点 parentId=200（不在节点列表中）
            new TreeNode(4L, 200L, "树2根节点"),
            new TreeNode(5L, 4L, "树2子节点1")
        );

        List<Tree<Long>> result = TreeBuildUtils.buildMultiRoot(
            nodes,
            TreeNode::getId,
            TreeNode::getParentId,
            (node, tree) -> {
                tree.setId(node.getId());
                tree.setParentId(node.getParentId());
                tree.setName(node.getName());
            }
        );

        // 应该生成2棵树
        assertThat(result).hasSize(2);

        // 验证树的结构
        Tree<Long> tree1 = result.stream()
            .filter(t -> t.getId().equals(1L))
            .findFirst()
            .orElse(null);
        assertThat((Object) tree1).isNotNull();
        assertThat(tree1.getName()).isEqualTo("树1根节点");
        assertThat(tree1.getChildren()).hasSize(2);

        Tree<Long> tree2 = result.stream()
            .filter(t -> t.getId().equals(4L))
            .findFirst()
            .orElse(null);
        assertThat((Object) tree2).isNotNull();
        assertThat(tree2.getName()).isEqualTo("树2根节点");
        assertThat(tree2.getChildren()).hasSize(1);
    }

    @Test
    void shouldBuildSingleRootTreeWhenMultiRootCalled() {
        List<TreeNode> nodes = Arrays.asList(
            new TreeNode(1L, 0L, "根节点"),
            new TreeNode(2L, 1L, "子节点1"),
            new TreeNode(3L, 1L, "子节点2")
        );

        List<Tree<Long>> result = TreeBuildUtils.buildMultiRoot(
            nodes,
            TreeNode::getId,
            TreeNode::getParentId,
            (node, tree) -> {
                tree.setId(node.getId());
                tree.setParentId(node.getParentId());
                tree.setName(node.getName());
            }
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getChildren()).hasSize(2);
    }

    @Test
    void shouldHandleNullParentIdsInMultiRoot() {
        List<TreeNode> nodes = Arrays.asList(
            new TreeNode(1L, null, "根节点1"),
            new TreeNode(2L, 1L, "子节点1"),
            new TreeNode(3L, null, "根节点2")
        );

        List<Tree<Long>> result = TreeBuildUtils.buildMultiRoot(
            nodes,
            TreeNode::getId,
            TreeNode::getParentId,
            (node, tree) -> {
                tree.setId(node.getId());
                tree.setParentId(node.getParentId());
                tree.setName(node.getName());
            }
        );

        // null parentId 会被过滤掉，所以结果为空
        assertThat(result).isEmpty();
    }

    // ==================== getLeafNodes 方法测试 ====================

    @Test
    void shouldReturnEmptyListWhenGetLeafNodesWithEmptyList() {
        List<Tree<Long>> emptyList = new ArrayList<>();

        List<Tree<Long>> result = TreeBuildUtils.getLeafNodes(emptyList);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenGetLeafNodesWithNullList() {
        List<Tree<Long>> result = TreeBuildUtils.getLeafNodes(null);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnSingleNodeWhenTreeHasOnlyRoot() {
        List<TreeNode> nodes = Arrays.asList(
            new TreeNode(1L, 0L, "根节点")
        );

        List<Tree<Long>> tree = TreeBuildUtils.build(nodes, (node, t) -> {
            t.setId(node.getId());
            t.setParentId(node.getParentId());
            t.setName(node.getName());
        });

        List<Tree<Long>> leafNodes = TreeBuildUtils.getLeafNodes(tree);

        assertThat(leafNodes)
            .hasSize(1)
            .first()
            .satisfies(leaf -> {
                assertThat(leaf.getId()).isEqualTo(1L);
                assertThat(leaf.getName()).isEqualTo("根节点");
            });
    }

    @Test
    void shouldReturnOnlyLeafNodesFromTwoLevelTree() {
        List<TreeNode> nodes = Arrays.asList(
            new TreeNode(1L, 0L, "根节点"),
            new TreeNode(2L, 1L, "子节点1"),
            new TreeNode(3L, 1L, "子节点2"),
            new TreeNode(4L, 1L, "子节点3")
        );

        List<Tree<Long>> tree = TreeBuildUtils.build(nodes, (node, t) -> {
            t.setId(node.getId());
            t.setParentId(node.getParentId());
            t.setName(node.getName());
        });

        List<Tree<Long>> leafNodes = TreeBuildUtils.getLeafNodes(tree);

        // 应该只返回3个叶子节点
        assertThat(leafNodes).hasSize(3);
        assertThat(leafNodes)
            .extracting(Tree::getId)
            .containsExactlyInAnyOrder(2L, 3L, 4L);
    }

    @Test
    void shouldReturnOnlyLeafNodesFromMultiLevelTree() {
        List<TreeNode> nodes = Arrays.asList(
            new TreeNode(1L, 0L, "根节点"),
            new TreeNode(2L, 1L, "子节点1"),
            new TreeNode(3L, 1L, "子节点2"),
            new TreeNode(4L, 2L, "孙节点1"),
            new TreeNode(5L, 2L, "孙节点2"),
            new TreeNode(6L, 3L, "孙节点3"),
            new TreeNode(7L, 6L, "曾孙节点1")
        );

        List<Tree<Long>> tree = TreeBuildUtils.build(nodes, (node, t) -> {
            t.setId(node.getId());
            t.setParentId(node.getParentId());
            t.setName(node.getName());
        });

        List<Tree<Long>> leafNodes = TreeBuildUtils.getLeafNodes(tree);

        // 叶子节点应该是：孙节点1(4), 孙节点2(5), 曾孙节点1(7)
        assertThat(leafNodes).hasSize(3);
        assertThat(leafNodes)
            .extracting(Tree::getId)
            .containsExactlyInAnyOrder(4L, 5L, 7L);
    }

    @Test
    void shouldReturnAllNodesWhenAllAreLeaves() {
        // 构建一个扁平的树（所有节点都是根节点的直接子节点）
        List<TreeNode> nodes = Arrays.asList(
            new TreeNode(1L, 0L, "根节点"),
            new TreeNode(2L, 1L, "叶子1"),
            new TreeNode(3L, 1L, "叶子2"),
            new TreeNode(4L, 1L, "叶子3")
        );

        List<Tree<Long>> tree = TreeBuildUtils.build(nodes, (node, t) -> {
            t.setId(node.getId());
            t.setParentId(node.getParentId());
            t.setName(node.getName());
        });

        List<Tree<Long>> leafNodes = TreeBuildUtils.getLeafNodes(tree);

        assertThat(leafNodes).hasSize(3);
        assertThat(leafNodes)
            .extracting(Tree::getId)
            .containsExactlyInAnyOrder(2L, 3L, 4L);
    }

    @Test
    void shouldExtractLeafNodesFromMultipleRootTrees() {
        List<TreeNode> nodes = Arrays.asList(
            // 第一棵树
            new TreeNode(1L, 100L, "树1根节点"),
            new TreeNode(2L, 1L, "树1叶子1"),
            new TreeNode(3L, 1L, "树1叶子2"),
            // 第二棵树
            new TreeNode(4L, 200L, "树2根节点"),
            new TreeNode(5L, 4L, "树2子节点"),
            new TreeNode(6L, 5L, "树2叶子1")
        );

        List<Tree<Long>> trees = TreeBuildUtils.buildMultiRoot(
            nodes,
            TreeNode::getId,
            TreeNode::getParentId,
            (node, tree) -> {
                tree.setId(node.getId());
                tree.setParentId(node.getParentId());
                tree.setName(node.getName());
            }
        );

        List<Tree<Long>> leafNodes = TreeBuildUtils.getLeafNodes(trees);

        // 叶子节点：树1叶子1(2), 树1叶子2(3), 树2叶子1(6)
        assertThat(leafNodes).hasSize(3);
        assertThat(leafNodes)
            .extracting(Tree::getId)
            .containsExactlyInAnyOrder(2L, 3L, 6L);
    }

    // ==================== 边界测试 ====================

    @Test
    void shouldHandleDeepNestedTree() {
        // 创建一个深度嵌套的树（链式结构）
        List<TreeNode> nodes = Arrays.asList(
            new TreeNode(1L, 0L, "第1层"),
            new TreeNode(2L, 1L, "第2层"),
            new TreeNode(3L, 2L, "第3层"),
            new TreeNode(4L, 3L, "第4层"),
            new TreeNode(5L, 4L, "第5层")
        );

        List<Tree<Long>> tree = TreeBuildUtils.build(nodes, (node, t) -> {
            t.setId(node.getId());
            t.setParentId(node.getParentId());
            t.setName(node.getName());
        });

        assertThat(tree).hasSize(1);

        // 验证深度
        Tree<Long> current = tree.get(0);
        int depth = 1;
        while (current.hasChild()) {
            current = current.getChildren().get(0);
            depth++;
        }
        assertThat(depth).isEqualTo(5);

        // 验证叶子节点
        List<Tree<Long>> leafNodes = TreeBuildUtils.getLeafNodes(tree);
        assertThat(leafNodes).hasSize(1);
        assertThat(leafNodes.get(0).getId()).isEqualTo(5L);
    }

    @Test
    void shouldHandleTreeWithCustomAttributes() {
        List<TreeNode> nodes = Arrays.asList(
            new TreeNode(1L, 0L, "根节点", 1),
            new TreeNode(2L, 1L, "子节点1", 2),
            new TreeNode(3L, 1L, "子节点2", 1)
        );

        List<Tree<Long>> tree = TreeBuildUtils.build(nodes, (node, t) -> {
            t.setId(node.getId());
            t.setParentId(node.getParentId());
            t.setName(node.getName());
            t.putExtra("sort", node.getSort());  // 自定义属性
        });

        assertThat(tree).hasSize(1);
        Tree<Long> root = tree.get(0);
        assertThat(root.get("sort")).isEqualTo(1);
        assertThat(root.getChildren().get(0).get("sort")).isEqualTo(2);
        assertThat(root.getChildren().get(1).get("sort")).isEqualTo(1);
    }
}
