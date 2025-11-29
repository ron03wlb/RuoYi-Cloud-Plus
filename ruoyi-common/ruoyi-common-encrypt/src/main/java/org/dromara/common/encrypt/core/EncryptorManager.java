package org.dromara.common.encrypt.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.utils.ObjectUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.encrypt.annotation.EncryptField;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.util.ClassUtils;

/**
 * Manager class for encryption and decryption operations.
 *
 * <p>This class manages encryptor instances and field-level encryption caching. It scans entity
 * classes for encrypted fields and provides centralized encryption services.
 *
 * @author 老马
 * @version 4.6.0
 */
@Slf4j
@NoArgsConstructor
public class EncryptorManager {

  /** Cache for encryptor instances, keyed by EncryptContext hash code. */
  Map<Integer, IEncryptor> encryptorMap = new ConcurrentHashMap<>();

  /** Cache for encrypted fields of each entity class. */
  Map<Class<?>, Set<Field>> fieldCache = new ConcurrentHashMap<>();

  /**
   * Constructs an EncryptorManager and scans the specified package for encrypted fields.
   *
   * @param typeAliasesPackage the package path to scan for entity classes with encryption
   *     annotations
   */
  public EncryptorManager(String typeAliasesPackage) {
    scanEncryptClasses(typeAliasesPackage);
  }

  /**
   * Retrieves the cached encrypted fields for the given class.
   *
   * @param sourceClazz the class to retrieve encrypted fields for
   * @return a set of fields marked with {@link EncryptField} annotation, or null if none exist
   */
  public Set<Field> getFieldCache(Class<?> sourceClazz) {
    return ObjectUtils.notNullGetter(fieldCache, f -> f.get(sourceClazz));
  }

  /**
   * Registers and retrieves an encryptor instance from cache based on the encryption context.
   *
   * <p>If an encryptor for the given context already exists in cache, it is returned. Otherwise, a
   * new encryptor instance is created, cached, and returned.
   *
   * @param encryptContext the encryption context containing algorithm and key configurations
   * @return the encryptor instance for the given context
   */
  public IEncryptor registAndGetEncryptor(EncryptContext encryptContext) {
    int key = encryptContext.hashCode();
    if (encryptorMap.containsKey(key)) {
      return encryptorMap.get(key);
    }
    IEncryptor encryptor =
        ReflectUtil.newInstance(encryptContext.getAlgorithm().getClazz(), encryptContext);
    encryptorMap.put(key, encryptor);
    return encryptor;
  }

  /**
   * Removes an encryptor instance from cache.
   *
   * @param encryptContext the encryption context whose encryptor should be removed
   */
  public void removeEncryptor(EncryptContext encryptContext) {
    this.encryptorMap.remove(encryptContext.hashCode());
  }

  /**
   * Encrypts the given value using the specified encryption context.
   *
   * <p>The method caches the encryptor instance for performance. If the value is already encrypted
   * (starts with encryption header), it is returned unchanged.
   *
   * @param value the plaintext value to encrypt
   * @param encryptContext the encryption context containing algorithm and key configurations
   * @return the encrypted value with encryption header prefix
   */
  public String encrypt(String value, EncryptContext encryptContext) {
    if (StringUtils.startsWith(value, Constants.ENCRYPT_HEADER)) {
      return value;
    }
    IEncryptor encryptor = this.registAndGetEncryptor(encryptContext);
    String encrypt = encryptor.encrypt(value, encryptContext.getEncode());
    return Constants.ENCRYPT_HEADER + encrypt;
  }

  /**
   * Decrypts the given value using the specified encryption context.
   *
   * <p>If the value does not start with the encryption header, it is assumed to be unencrypted and
   * returned unchanged.
   *
   * @param value the encrypted value to decrypt
   * @param encryptContext the encryption context containing algorithm and key configurations
   * @return the decrypted plaintext value
   */
  public String decrypt(String value, EncryptContext encryptContext) {
    if (!StringUtils.startsWith(value, Constants.ENCRYPT_HEADER)) {
      return value;
    }
    IEncryptor encryptor = this.registAndGetEncryptor(encryptContext);
    String str = StringUtils.removeStart(value, Constants.ENCRYPT_HEADER);
    return encryptor.decrypt(str);
  }

  /**
   * Scans the specified package for entity classes with encrypted fields.
   *
   * @param typeAliasesPackage the package path to scan for entity classes
   */
  private void scanEncryptClasses(String typeAliasesPackage) {
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
    String[] packagePatternArray =
        StringUtils.splitPreserveAllTokens(
            typeAliasesPackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
    String classpath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;
    try {
      for (String packagePattern : packagePatternArray) {
        String path = ClassUtils.convertClassNameToResourcePath(packagePattern);
        Resource[] resources = resolver.getResources(classpath + path + "/*.class");
        for (Resource resource : resources) {
          ClassMetadata classMetadata = factory.getMetadataReader(resource).getClassMetadata();
          Class<?> clazz = Resources.classForName(classMetadata.getClassName());
          Set<Field> encryptFieldSet = getEncryptFieldSetFromClazz(clazz);
          if (CollUtil.isNotEmpty(encryptFieldSet)) {
            fieldCache.put(clazz, encryptFieldSet);
          }
        }
      }
    } catch (Exception e) {
      log.error("初始化数据安全缓存时出错:{}", e.getMessage());
    }
  }

  /**
   * Retrieves all encrypted fields from a class and its superclasses.
   *
   * @param clazz the class to scan for encrypted fields
   * @return a set of fields annotated with {@link EncryptField} and of type String
   */
  private Set<Field> getEncryptFieldSetFromClazz(Class<?> clazz) {
    Set<Field> fieldSet = new HashSet<>();
    // 判断clazz如果是接口,内部类,匿名类就直接返回
    if (clazz.isInterface() || clazz.isMemberClass() || clazz.isAnonymousClass()) {
      return fieldSet;
    }
    while (clazz != null) {
      Field[] fields = clazz.getDeclaredFields();
      fieldSet.addAll(Arrays.asList(fields));
      clazz = clazz.getSuperclass();
    }
    fieldSet =
        fieldSet.stream()
            .filter(
                field ->
                    field.isAnnotationPresent(EncryptField.class)
                        && field.getType() == String.class)
            .collect(Collectors.toSet());
    for (Field field : fieldSet) {
      field.setAccessible(true);
    }
    return fieldSet;
  }
}
