package org.dromara.common.core.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 测试用用户实体.
 *
 * @author Test Team
 */
public class User implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private Long id;
  private String username;
  private String email;
  private Integer age;
  private LocalDateTime createTime;

  public User() {}

  public User(Long id, String username, String email, Integer age, LocalDateTime createTime) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.age = age;
    this.createTime = createTime;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public LocalDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(LocalDateTime createTime) {
    this.createTime = createTime;
  }
}
