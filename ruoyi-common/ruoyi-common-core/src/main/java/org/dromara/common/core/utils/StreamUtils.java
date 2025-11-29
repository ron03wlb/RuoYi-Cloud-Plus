package org.dromara.common.core.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * stream 流工具类.
 *
 * @author Lion Li
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StreamUtils {

  /**
   * 将collection过滤.
   *
   * @param collection 需要转化的集合
   * @param function 过滤方法
   * @return 过滤后的list
   */
  public static <E> List<E> filter(Collection<E> collection, Predicate<E> function) {
    if (CollUtil.isEmpty(collection)) {
      return CollUtil.newArrayList();
    }
    return collection.stream()
        .filter(function)
        // 注意此处不要使用 .toList() 新语法 因为返回的是不可变List 会导致序列化问题
        .collect(Collectors.toList());
  }

  /**
   * 找到流中满足条件的第一个元素.
   *
   * @param collection 需要查询的集合
   * @param function 过滤方法
   * @return 找到符合条件的第一个元素，没有则返回 Optional.empty()
   */
  public static <E> Optional<E> findFirst(Collection<E> collection, Predicate<E> function) {
    if (CollUtil.isEmpty(collection)) {
      return Optional.empty();
    }
    return collection.stream().filter(function).findFirst();
  }

  /**
   * 找到流中满足条件的第一个元素值.
   *
   * @param collection 需要查询的集合
   * @param function 过滤方法
   * @return 找到符合条件的第一个元素，没有则返回 null
   */
  public static <E> E findFirstValue(Collection<E> collection, Predicate<E> function) {
    return findFirst(collection, function).orElse(null);
  }

  /**
   * 找到流中任意一个满足条件的元素.
   *
   * @param collection 需要查询的集合
   * @param function 过滤方法
   * @return 找到符合条件的任意一个元素，没有则返回 Optional.empty()
   */
  public static <E> Optional<E> findAny(Collection<E> collection, Predicate<E> function) {
    if (CollUtil.isEmpty(collection)) {
      return Optional.empty();
    }
    return collection.stream().filter(function).findAny();
  }

  /**
   * 找到流中任意一个满足条件的元素值.
   *
   * @param collection 需要查询的集合
   * @param function 过滤方法
   * @return 找到符合条件的任意一个元素，没有则返回null
   */
  public static <E> E findAnyValue(Collection<E> collection, Predicate<E> function) {
    return findAny(collection, function).orElse(null);
  }

  /**
   * 将collection拼接.
   *
   * @param collection 需要转化的集合
   * @param function 拼接方法
   * @return 拼接后的list
   */
  public static <E> String join(Collection<E> collection, Function<E, String> function) {
    return join(collection, function, StringUtils.SEPARATOR);
  }

  /**
   * 将collection拼接.
   *
   * @param collection 需要转化的集合
   * @param function 拼接方法
   * @param delimiter 拼接符
   * @return 拼接后的list
   */
  public static <E> String join(
      Collection<E> collection, Function<E, String> function, CharSequence delimiter) {
    if (CollUtil.isEmpty(collection)) {
      return StringUtils.EMPTY;
    }
    return collection.stream()
        .map(function)
        .filter(Objects::nonNull)
        .collect(Collectors.joining(delimiter));
  }

  /**
   * 将collection排序.
   *
   * @param collection 需要转化的集合
   * @param comparing 排序方法
   * @return 排序后的list
   */
  public static <E> List<E> sorted(Collection<E> collection, Comparator<E> comparing) {
    if (CollUtil.isEmpty(collection)) {
      return CollUtil.newArrayList();
    }
    return collection.stream()
        .filter(Objects::nonNull)
        .sorted(comparing)
        // 注意此处不要使用 .toList() 新语法 因为返回的是不可变List 会导致序列化问题
        .collect(Collectors.toList());
  }

  /**
   * Converts collection to identity map with same type.<br>
   * <B>{@code Collection<V> ----> Map<K,V>}</B>
   *
   * @param collection the collection to convert
   * @param key lambda method to convert V type to K type
   * @param <V> generic type in collection
   * @param <K> key type in map
   * @return converted map
   */
  public static <V, K> Map<K, V> toIdentityMap(Collection<V> collection, Function<V, K> key) {
    if (CollUtil.isEmpty(collection)) {
      return MapUtil.newHashMap();
    }
    return collection.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(key, Function.identity(), (l, r) -> l));
  }

  /**
   * Converts Collection to map (value type differs from collection generic).<br>
   * <B>{@code Collection<E> -----> Map<K,V> }</B>
   *
   * @param collection the collection to convert
   * @param key lambda method to convert E type to K type
   * @param value lambda method to convert E type to V type
   * @param <E> generic type in collection
   * @param <K> key type in map
   * @param <V> value type in map
   * @return converted map
   */
  public static <E, K, V> Map<K, V> toMap(
      Collection<E> collection, Function<E, K> key, Function<E, V> value) {
    if (CollUtil.isEmpty(collection)) {
      return MapUtil.newHashMap();
    }
    return collection.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(key, value, (l, r) -> l));
  }

  /**
   * Gets data from map as new Map's value, key remains unchanged.
   *
   * @param map the map to process
   * @param take value extraction function
   * @param <K> key type in map
   * @param <E> value type in map
   * @param <V> value type in new map
   * @return new map
   */
  public static <K, E, V> Map<K, V> toMap(Map<K, E> map, BiFunction<K, E, V> take) {
    if (CollUtil.isEmpty(map)) {
      return MapUtil.newHashMap();
    }
    return toMap(
        map.entrySet(), Map.Entry::getKey, entry -> take.apply(entry.getKey(), entry.getValue()));
  }

  /**
   * Groups collection into map by rule (e.g. same class id).<br>
   * <B>{@code Collection<E> -------> Map<K,List<E>> } </B>
   *
   * @param collection the collection to classify
   * @param key the classification rule
   * @param <E> generic type in collection
   * @param <K> key type in map
   * @return classified map
   */
  public static <E, K> Map<K, List<E>> groupByKey(Collection<E> collection, Function<E, K> key) {
    if (CollUtil.isEmpty(collection)) {
      return MapUtil.newHashMap();
    }
    return collection.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.groupingBy(key, LinkedHashMap::new, Collectors.toList()));
  }

  /**
   * Groups collection into two-level map by two rules (e.g. same grade id and class id).<br>
   * <B>{@code Collection<E> ---> Map<T,Map<U,List<E>>> } </B>
   *
   * @param collection the collection to classify
   * @param key1 the first classification rule
   * @param key2 the second classification rule
   * @param <E> collection element type
   * @param <K> key type in first map
   * @param <U> key type in second map
   * @return classified map
   */
  public static <E, K, U> Map<K, Map<U, List<E>>> groupBy2Key(
      Collection<E> collection, Function<E, K> key1, Function<E, U> key2) {
    if (CollUtil.isEmpty(collection)) {
      return MapUtil.newHashMap();
    }
    return collection.stream()
        .filter(Objects::nonNull)
        .collect(
            Collectors.groupingBy(
                key1,
                LinkedHashMap::new,
                Collectors.groupingBy(key2, LinkedHashMap::new, Collectors.toList())));
  }

  /**
   * Groups collection into two-level map by two rules (e.g. same grade id and class id).<br>
   * <B>{@code Collection<E> ---> Map<T,Map<U,E>> } </B>
   *
   * @param collection the collection to classify
   * @param key1 the first classification rule
   * @param key2 the second classification rule
   * @param <T> key type in first map
   * @param <U> key type in second map
   * @param <E> generic type in collection
   * @return classified map
   */
  public static <E, T, U> Map<T, Map<U, E>> group2Map(
      Collection<E> collection, Function<E, T> key1, Function<E, U> key2) {
    if (CollUtil.isEmpty(collection)) {
      return MapUtil.newHashMap();
    }
    return collection.stream()
        .filter(Objects::nonNull)
        .collect(
            Collectors.groupingBy(
                key1,
                LinkedHashMap::new,
                Collectors.toMap(key2, Function.identity(), (l, r) -> l)));
  }

  /**
   * Converts collection to List with different generic types.<br>
   * <B>{@code Collection<E> ------> List<T> } </B>
   *
   * @param collection the collection to convert
   * @param function lambda expression to convert collection generic to list generic
   * @param <E> generic type in collection
   * @param <T> generic type in List
   * @return converted list
   */
  public static <E, T> List<T> toList(Collection<E> collection, Function<E, T> function) {
    if (CollUtil.isEmpty(collection)) {
      return CollUtil.newArrayList();
    }
    return collection.stream()
        .map(function)
        .filter(Objects::nonNull)
        // 注意此处不要使用 .toList() 新语法 因为返回的是不可变List 会导致序列化问题
        .collect(Collectors.toList());
  }

  /**
   * Converts collection to Set with different generic types.<br>
   * <B>{@code Collection<E> ------> Set<T> } </B>
   *
   * @param collection the collection to convert
   * @param function lambda expression to convert collection generic to set generic
   * @param <E> generic type in collection
   * @param <T> generic type in Set
   * @return converted Set
   */
  public static <E, T> Set<T> toSet(Collection<E> collection, Function<E, T> function) {
    if (CollUtil.isEmpty(collection)) {
      return CollUtil.newHashSet();
    }
    return collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.toSet());
  }

  /**
   * 合并两个相同key类型的map.
   *
   * @param map1 第一个需要合并的 map
   * @param map2 第二个需要合并的 map
   * @param merge 合并的lambda，将key value1 value2合并成最终的类型,注意value可能为空的情况
   * @param <K> map中的key类型
   * @param <X> 第一个 map的value类型
   * @param <Y> 第二个 map的value类型
   * @param <V> 最终map的value类型
   * @return 合并后的map
   */
  public static <K, X, Y, V> Map<K, V> merge(
      Map<K, X> map1, Map<K, Y> map2, BiFunction<X, Y, V> merge) {
    if (CollUtil.isEmpty(map1) && CollUtil.isEmpty(map2)) {
      // 如果两个 map 都为空，则直接返回空的 map
      return MapUtil.newHashMap();
    } else if (CollUtil.isEmpty(map1)) {
      // 如果 map1 为空，则直接处理返回 map2
      return toMap(
          map2.entrySet(), Map.Entry::getKey, entry -> merge.apply(null, entry.getValue()));
    } else if (CollUtil.isEmpty(map2)) {
      // 如果 map2 为空，则直接处理返回 map1
      return toMap(
          map1.entrySet(), Map.Entry::getKey, entry -> merge.apply(entry.getValue(), null));
    }
    Set<K> keySet = new HashSet<>();
    keySet.addAll(map1.keySet());
    keySet.addAll(map2.keySet());
    return toMap(keySet, key -> key, key -> merge.apply(map1.get(key), map2.get(key)));
  }
}
