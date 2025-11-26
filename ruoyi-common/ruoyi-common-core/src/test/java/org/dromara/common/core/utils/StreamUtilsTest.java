package org.dromara.common.core.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.*;
import org.dromara.common.core.BaseUnitTest;
import org.junit.jupiter.api.Test;

/**
 * StreamUtils 工具类测试
 *
 * <p>测试覆盖： - 集合过滤 - 查找元素 - 字符串拼接 - 集合排序 - 集合转 Map - 集合分组 - 类型转换 - Map 合并
 *
 * @author Test Team
 */
class StreamUtilsTest extends BaseUnitTest {

    // 测试数据类
    static class User {
        private Long id;
        private String name;
        private Integer age;
        private String grade;
        private String clazz;

        public User(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public User(Long id, String name, Integer age, String grade, String clazz) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.grade = grade;
            this.clazz = clazz;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

        public String getGrade() {
            return grade;
        }

        public String getClazz() {
            return clazz;
        }
    }

    // ========================================
    // filter 测试
    // ========================================

    @Test
    void shouldFilterCollectionByPredicate() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

        List<Integer> result = StreamUtils.filter(numbers, n -> n > 3);

        assertThat(result).hasSize(3).containsExactly(4, 5, 6);
    }

    @Test
    void shouldReturnEmptyListWhenFilterEmptyCollection() {
        List<Integer> empty = Collections.emptyList();

        List<Integer> result = StreamUtils.filter(empty, n -> n > 3);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenFilterNullCollection() {
        List<Integer> result = StreamUtils.filter(null, n -> n > 3);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenNoMatchInFilter() {
        List<Integer> numbers = Arrays.asList(1, 2, 3);

        List<Integer> result = StreamUtils.filter(numbers, n -> n > 10);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFilterComplexObjects() {
        List<User> users =
                Arrays.asList(
                        new User(1L, "Alice", 20),
                        new User(2L, "Bob", 25),
                        new User(3L, "Charlie", 30));

        List<User> result = StreamUtils.filter(users, u -> u.getAge() >= 25);

        assertThat(result).hasSize(2).extracting(User::getName).containsExactly("Bob", "Charlie");
    }

    // ========================================
    // findFirst 测试
    // ========================================

    @Test
    void shouldFindFirstMatchingElement() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        Optional<Integer> result = StreamUtils.findFirst(numbers, n -> n > 2);

        assertThat(result).isPresent().hasValue(3);
    }

    @Test
    void shouldReturnEmptyWhenFindFirstInEmptyCollection() {
        List<Integer> empty = Collections.emptyList();

        Optional<Integer> result = StreamUtils.findFirst(empty, n -> n > 2);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenFindFirstInNullCollection() {
        Optional<Integer> result = StreamUtils.findFirst(null, n -> n > 2);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenNoMatchInFindFirst() {
        List<Integer> numbers = Arrays.asList(1, 2, 3);

        Optional<Integer> result = StreamUtils.findFirst(numbers, n -> n > 10);

        assertThat(result).isEmpty();
    }

    // ========================================
    // findFirstValue 测试
    // ========================================

    @Test
    void shouldFindFirstValueMatchingElement() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        Integer result = StreamUtils.findFirstValue(numbers, n -> n > 2);

        assertThat(result).isEqualTo(3);
    }

    @Test
    void shouldReturnNullWhenFindFirstValueInEmptyCollection() {
        List<Integer> empty = Collections.emptyList();

        Integer result = StreamUtils.findFirstValue(empty, n -> n > 2);

        assertThat(result).isNull();
    }

    @Test
    void shouldReturnNullWhenNoMatchInFindFirstValue() {
        List<Integer> numbers = Arrays.asList(1, 2, 3);

        Integer result = StreamUtils.findFirstValue(numbers, n -> n > 10);

        assertThat(result).isNull();
    }

    // ========================================
    // findAny 测试
    // ========================================

    @Test
    void shouldFindAnyMatchingElement() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        Optional<Integer> result = StreamUtils.findAny(numbers, n -> n > 2);

        assertThat(result).isPresent();
        assertThat(result.get()).isGreaterThan(2);
    }

    @Test
    void shouldReturnEmptyWhenFindAnyInEmptyCollection() {
        List<Integer> empty = Collections.emptyList();

        Optional<Integer> result = StreamUtils.findAny(empty, n -> n > 2);

        assertThat(result).isEmpty();
    }

    // ========================================
    // findAnyValue 测试
    // ========================================

    @Test
    void shouldFindAnyValueMatchingElement() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        Integer result = StreamUtils.findAnyValue(numbers, n -> n > 2);

        assertThat(result).isNotNull().isGreaterThan(2);
    }

    @Test
    void shouldReturnNullWhenFindAnyValueInEmptyCollection() {
        List<Integer> empty = Collections.emptyList();

        Integer result = StreamUtils.findAnyValue(empty, n -> n > 2);

        assertThat(result).isNull();
    }

    // ========================================
    // join 测试
    // ========================================

    @Test
    void shouldJoinCollectionWithDefaultDelimiter() {
        List<User> users =
                Arrays.asList(
                        new User(1L, "Alice", 20),
                        new User(2L, "Bob", 25),
                        new User(3L, "Charlie", 30));

        String result = StreamUtils.join(users, User::getName);

        assertThat(result).isEqualTo("Alice,Bob,Charlie");
    }

    @Test
    void shouldJoinCollectionWithCustomDelimiter() {
        List<User> users = Arrays.asList(new User(1L, "Alice", 20), new User(2L, "Bob", 25));

        String result = StreamUtils.join(users, User::getName, " | ");

        assertThat(result).isEqualTo("Alice | Bob");
    }

    @Test
    void shouldReturnEmptyStringWhenJoinEmptyCollection() {
        List<User> empty = Collections.emptyList();

        String result = StreamUtils.join(empty, User::getName);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyStringWhenJoinNullCollection() {
        String result = StreamUtils.join(null, (User u) -> u.getName());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFilterNullValuesInJoin() {
        List<String> list = Arrays.asList("a", null, "b", "c");

        String result = StreamUtils.join(list, s -> s, ",");

        assertThat(result).isEqualTo("a,b,c");
    }

    // ========================================
    // sorted 测试
    // ========================================

    @Test
    void shouldSortCollectionByComparator() {
        List<User> users =
                Arrays.asList(
                        new User(1L, "Charlie", 30),
                        new User(2L, "Alice", 20),
                        new User(3L, "Bob", 25));

        List<User> result = StreamUtils.sorted(users, Comparator.comparing(User::getAge));

        assertThat(result)
                .hasSize(3)
                .extracting(User::getName)
                .containsExactly("Alice", "Bob", "Charlie");
    }

    @Test
    void shouldSortInDescendingOrder() {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);

        List<Integer> result = StreamUtils.sorted(numbers, Comparator.reverseOrder());

        assertThat(result).containsExactly(9, 6, 5, 4, 3, 2, 1, 1);
    }

    @Test
    void shouldReturnEmptyListWhenSortEmptyCollection() {
        List<Integer> empty = Collections.emptyList();

        List<Integer> result = StreamUtils.sorted(empty, Comparator.naturalOrder());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFilterNullValuesInSort() {
        List<Integer> list = Arrays.asList(3, null, 1, null, 2);

        List<Integer> result = StreamUtils.sorted(list, Comparator.naturalOrder());

        assertThat(result).containsExactly(1, 2, 3);
    }

    // ========================================
    // toIdentityMap 测试
    // ========================================

    @Test
    void shouldConvertCollectionToIdentityMap() {
        List<User> users =
                Arrays.asList(
                        new User(1L, "Alice", 20),
                        new User(2L, "Bob", 25),
                        new User(3L, "Charlie", 30));

        Map<Long, User> result = StreamUtils.toIdentityMap(users, User::getId);

        assertThat(result).hasSize(3).containsKeys(1L, 2L, 3L);
        assertThat(result.get(1L).getName()).isEqualTo("Alice");
    }

    @Test
    void shouldReturnEmptyMapWhenToIdentityMapWithEmptyCollection() {
        List<User> empty = Collections.emptyList();

        Map<Long, User> result = StreamUtils.toIdentityMap(empty, User::getId);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleDuplicateKeysInToIdentityMap() {
        List<User> users =
                Arrays.asList(
                        new User(1L, "Alice", 20), new User(1L, "Bob", 25) // 重复的 ID
                        );

        Map<Long, User> result = StreamUtils.toIdentityMap(users, User::getId);

        // 当有重复 key 时，保留第一个元素
        assertThat(result).hasSize(1);
        assertThat(result.get(1L).getName()).isEqualTo("Alice");
    }

    // ========================================
    // toMap 测试
    // ========================================

    @Test
    void shouldConvertCollectionToMap() {
        List<User> users =
                Arrays.asList(
                        new User(1L, "Alice", 20),
                        new User(2L, "Bob", 25),
                        new User(3L, "Charlie", 30));

        Map<Long, String> result = StreamUtils.toMap(users, User::getId, User::getName);

        assertThat(result)
                .hasSize(3)
                .containsEntry(1L, "Alice")
                .containsEntry(2L, "Bob")
                .containsEntry(3L, "Charlie");
    }

    @Test
    void shouldReturnEmptyMapWhenToMapWithEmptyCollection() {
        List<User> empty = Collections.emptyList();

        Map<Long, String> result = StreamUtils.toMap(empty, User::getId, User::getName);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleDuplicateKeysInToMap() {
        List<User> users = Arrays.asList(new User(1L, "Alice", 20), new User(1L, "Bob", 25));

        Map<Long, String> result = StreamUtils.toMap(users, User::getId, User::getName);

        assertThat(result).hasSize(1);
        assertThat(result.get(1L)).isEqualTo("Alice");
    }

    // ========================================
    // toMap (from Map) 测试
    // ========================================

    @Test
    void shouldTransformMapValues() {
        Map<String, Integer> sourceMap = new HashMap<>();
        sourceMap.put("one", 1);
        sourceMap.put("two", 2);
        sourceMap.put("three", 3);

        Map<String, String> result =
                StreamUtils.toMap(sourceMap, (key, value) -> key + "=" + value);

        assertThat(result)
                .hasSize(3)
                .containsEntry("one", "one=1")
                .containsEntry("two", "two=2")
                .containsEntry("three", "three=3");
    }

    @Test
    void shouldReturnEmptyMapWhenTransformEmptyMap() {
        Map<String, Integer> empty = Collections.emptyMap();

        Map<String, String> result = StreamUtils.toMap(empty, (key, value) -> key + "=" + value);

        assertThat(result).isEmpty();
    }

    // ========================================
    // groupByKey 测试
    // ========================================

    @Test
    void shouldGroupCollectionByKey() {
        List<User> users =
                Arrays.asList(
                        new User(1L, "Alice", 20, "1", "A"),
                        new User(2L, "Bob", 25, "1", "B"),
                        new User(3L, "Charlie", 30, "2", "A"),
                        new User(4L, "David", 35, "2", "B"));

        Map<String, List<User>> result = StreamUtils.groupByKey(users, User::getGrade);

        assertThat(result).hasSize(2).containsKeys("1", "2");
        assertThat(result.get("1")).hasSize(2);
        assertThat(result.get("2")).hasSize(2);
    }

    @Test
    void shouldReturnEmptyMapWhenGroupEmptyCollection() {
        List<User> empty = Collections.emptyList();

        Map<String, List<User>> result = StreamUtils.groupByKey(empty, User::getGrade);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldMaintainInsertionOrderInGroupByKey() {
        List<User> users =
                Arrays.asList(
                        new User(1L, "Alice", 20, "C", "A"),
                        new User(2L, "Bob", 25, "A", "B"),
                        new User(3L, "Charlie", 30, "B", "A"));

        Map<String, List<User>> result = StreamUtils.groupByKey(users, User::getGrade);

        // LinkedHashMap 保持插入顺序
        List<String> keys = new ArrayList<>(result.keySet());
        assertThat(keys).containsExactly("C", "A", "B");
    }

    // ========================================
    // groupBy2Key 测试
    // ========================================

    @Test
    void shouldGroupCollectionByTwoKeys() {
        List<User> users =
                Arrays.asList(
                        new User(1L, "Alice", 20, "1", "A"),
                        new User(2L, "Bob", 25, "1", "B"),
                        new User(3L, "Charlie", 30, "1", "A"),
                        new User(4L, "David", 35, "2", "A"));

        Map<String, Map<String, List<User>>> result =
                StreamUtils.groupBy2Key(users, User::getGrade, User::getClazz);

        assertThat(result).hasSize(2).containsKeys("1", "2");
        assertThat(result.get("1")).hasSize(2);
        assertThat(result.get("1").get("A")).hasSize(2);
        assertThat(result.get("1").get("B")).hasSize(1);
        assertThat(result.get("2").get("A")).hasSize(1);
    }

    @Test
    void shouldReturnEmptyMapWhenGroupBy2KeyWithEmptyCollection() {
        List<User> empty = Collections.emptyList();

        Map<String, Map<String, List<User>>> result =
                StreamUtils.groupBy2Key(empty, User::getGrade, User::getClazz);

        assertThat(result).isEmpty();
    }

    // ========================================
    // group2Map 测试
    // ========================================

    @Test
    void shouldGroupCollectionToNestedMap() {
        List<User> users =
                Arrays.asList(
                        new User(1L, "Alice", 20, "1", "A"),
                        new User(2L, "Bob", 25, "1", "B"),
                        new User(3L, "Charlie", 30, "2", "A"));

        Map<String, Map<String, User>> result =
                StreamUtils.group2Map(users, User::getGrade, User::getClazz);

        assertThat(result).hasSize(2).containsKeys("1", "2");
        assertThat(result.get("1").get("A").getName()).isEqualTo("Alice");
        assertThat(result.get("1").get("B").getName()).isEqualTo("Bob");
        assertThat(result.get("2").get("A").getName()).isEqualTo("Charlie");
    }

    @Test
    void shouldReturnEmptyMapWhenGroup2MapWithEmptyCollection() {
        List<User> empty = Collections.emptyList();

        Map<String, Map<String, User>> result =
                StreamUtils.group2Map(empty, User::getGrade, User::getClazz);

        assertThat(result).isEmpty();
    }

    // ========================================
    // toList 测试
    // ========================================

    @Test
    void shouldConvertCollectionToList() {
        List<User> users =
                Arrays.asList(
                        new User(1L, "Alice", 20),
                        new User(2L, "Bob", 25),
                        new User(3L, "Charlie", 30));

        List<String> result = StreamUtils.toList(users, User::getName);

        assertThat(result).hasSize(3).containsExactly("Alice", "Bob", "Charlie");
    }

    @Test
    void shouldReturnEmptyListWhenToListWithEmptyCollection() {
        List<User> empty = Collections.emptyList();

        List<String> result = StreamUtils.toList(empty, User::getName);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFilterNullValuesInToList() {
        List<User> users = Arrays.asList(new User(1L, "Alice", 20), null, new User(2L, "Bob", 25));

        List<String> result = StreamUtils.toList(users, u -> u != null ? u.getName() : null);

        // null 元素被过滤掉
        assertThat(result).hasSize(2);
    }

    // ========================================
    // toSet 测试
    // ========================================

    @Test
    void shouldConvertCollectionToSet() {
        List<User> users =
                Arrays.asList(
                        new User(1L, "Alice", 20),
                        new User(2L, "Bob", 25),
                        new User(3L, "Alice", 30) // 重复名字
                        );

        Set<String> result = StreamUtils.toSet(users, User::getName);

        assertThat(result).hasSize(2).containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    void shouldReturnEmptySetWhenToSetWithEmptyCollection() {
        List<User> empty = Collections.emptyList();

        Set<String> result = StreamUtils.toSet(empty, User::getName);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFilterNullValuesInToSet() {
        List<User> users = Arrays.asList(new User(1L, "Alice", 20), null, new User(2L, "Bob", 25));

        Set<String> result = StreamUtils.toSet(users, u -> u != null ? u.getName() : null);

        assertThat(result).hasSize(2);
    }

    // ========================================
    // merge 测试
    // ========================================

    @Test
    void shouldMergeTwoMaps() {
        Map<String, Integer> map1 = new HashMap<>();
        map1.put("a", 1);
        map1.put("b", 2);

        Map<String, Integer> map2 = new HashMap<>();
        map2.put("b", 3);
        map2.put("c", 4);

        Map<String, Integer> result =
                StreamUtils.merge(
                        map1, map2, (v1, v2) -> (v1 == null ? 0 : v1) + (v2 == null ? 0 : v2));

        assertThat(result)
                .hasSize(3)
                .containsEntry("a", 1) // 只在 map1
                .containsEntry("b", 5) // 2 + 3
                .containsEntry("c", 4); // 只在 map2
    }

    @Test
    void shouldReturnEmptyMapWhenMergeTwoEmptyMaps() {
        Map<String, Integer> empty1 = Collections.emptyMap();
        Map<String, Integer> empty2 = Collections.emptyMap();

        Map<String, Integer> result = StreamUtils.merge(empty1, empty2, Integer::sum);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleNullMap1InMerge() {
        Map<String, Integer> map2 = new HashMap<>();
        map2.put("a", 1);
        map2.put("b", 2);

        Map<String, Integer> result =
                StreamUtils.merge(null, map2, (v1, v2) -> v2 == null ? 0 : v2);

        assertThat(result).hasSize(2).containsEntry("a", 1).containsEntry("b", 2);
    }

    @Test
    void shouldHandleNullMap2InMerge() {
        Map<String, Integer> map1 = new HashMap<>();
        map1.put("a", 1);
        map1.put("b", 2);

        Map<String, Integer> result =
                StreamUtils.merge(map1, null, (v1, v2) -> v1 == null ? 0 : v1);

        assertThat(result).hasSize(2).containsEntry("a", 1).containsEntry("b", 2);
    }

    @Test
    void shouldHandleNullValuesInMerge() {
        Map<String, Integer> map1 = new HashMap<>();
        map1.put("a", 1);
        map1.put("b", null);

        Map<String, Integer> map2 = new HashMap<>();
        map2.put("b", 3);
        map2.put("c", null);

        Map<String, Integer> result =
                StreamUtils.merge(
                        map1,
                        map2,
                        (v1, v2) -> {
                            if (v1 == null && v2 == null) return 0;
                            if (v1 == null) return v2;
                            if (v2 == null) return v1;
                            return v1 + v2;
                        });

        assertThat(result)
                .hasSize(3)
                .containsEntry("a", 1)
                .containsEntry("b", 3)
                .containsEntry("c", 0);
    }

    // ========================================
    // 边界值和特殊情况测试
    // ========================================

    @Test
    void shouldHandleNullElementsInCollection() {
        List<Integer> list = Arrays.asList(1, null, 2, null, 3);

        // filter 不会自动过滤 null
        List<Integer> filtered = StreamUtils.filter(list, n -> n != null && n > 1);
        assertThat(filtered).containsExactly(2, 3);
    }

    @Test
    void shouldReturnMutableList() {
        List<Integer> numbers = Arrays.asList(1, 2, 3);

        List<Integer> result = StreamUtils.filter(numbers, n -> n > 1);

        // 验证返回的列表是可修改的
        assertThatCode(() -> result.add(4)).doesNotThrowAnyException();
        assertThat(result).hasSize(3); // filter 后是 2, 3, 然后添加 4
    }

    @Test
    void shouldHandleSingleElementCollection() {
        List<Integer> single = Collections.singletonList(42);

        List<Integer> filtered = StreamUtils.filter(single, n -> n > 0);
        assertThat(filtered).containsExactly(42);

        Integer found = StreamUtils.findFirstValue(single, n -> n == 42);
        assertThat(found).isEqualTo(42);

        String joined = StreamUtils.join(single, Object::toString);
        assertThat(joined).isEqualTo("42");
    }
}
