package org.dromara.common.core.domain;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.common.core.constant.HttpStatus;

/**
 * 响应信息主体.
 *
 * @author Lion Li
 */
@Data
@NoArgsConstructor
public class R<T> implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 成功. */
  public static final int SUCCESS = 200;

  /** 失败. */
  public static final int FAIL = 500;

  /** 消息状态码. */
  private int code;

  /** 消息内容. */
  private String msg;

  /** 数据对象. */
  private T data;

  /**
   * Returns success response.
   *
   * @param <T> the type of response data
   * @return success response
   */
  public static <T> R<T> ok() {
    return restResult(null, SUCCESS, "操作成功");
  }

  /**
   * Returns success response with data.
   *
   * @param data the response data
   * @param <T> the type of response data
   * @return success response with data
   */
  public static <T> R<T> ok(T data) {
    return restResult(data, SUCCESS, "操作成功");
  }

  /**
   * Returns success response with custom message.
   *
   * @param msg the response message
   * @param <T> the type of response data
   * @return success response with custom message
   */
  public static <T> R<T> ok(String msg) {
    return restResult(null, SUCCESS, msg);
  }

  /**
   * Returns success response with custom message and data.
   *
   * @param msg the response message
   * @param data the response data
   * @param <T> the type of response data
   * @return success response with message and data
   */
  public static <T> R<T> ok(String msg, T data) {
    return restResult(data, SUCCESS, msg);
  }

  /**
   * Returns failure response.
   *
   * @param <T> the type of response data
   * @return failure response
   */
  public static <T> R<T> fail() {
    return restResult(null, FAIL, "操作失败");
  }

  /**
   * Returns failure response with custom message.
   *
   * @param msg the response message
   * @param <T> the type of response data
   * @return failure response with message
   */
  public static <T> R<T> fail(String msg) {
    return restResult(null, FAIL, msg);
  }

  /**
   * Returns failure response with data.
   *
   * @param data the response data
   * @param <T> the type of response data
   * @return failure response with data
   */
  public static <T> R<T> fail(T data) {
    return restResult(data, FAIL, "操作失败");
  }

  /**
   * Returns failure response with custom message and data.
   *
   * @param msg the response message
   * @param data the response data
   * @param <T> the type of response data
   * @return failure response with message and data
   */
  public static <T> R<T> fail(String msg, T data) {
    return restResult(data, FAIL, msg);
  }

  /**
   * Returns failure response with custom code and message.
   *
   * @param code the response code
   * @param msg the response message
   * @param <T> the type of response data
   * @return failure response with code and message
   */
  public static <T> R<T> fail(int code, String msg) {
    return restResult(null, code, msg);
  }

  /**
   * 返回警告消息.
   *
   * @param msg 返回内容
   * @return 警告消息
   */
  public static <T> R<T> warn(String msg) {
    return restResult(null, HttpStatus.WARN, msg);
  }

  /**
   * 返回警告消息.
   *
   * @param msg 返回内容
   * @param data 数据对象
   * @return 警告消息
   */
  public static <T> R<T> warn(String msg, T data) {
    return restResult(data, HttpStatus.WARN, msg);
  }

  private static <T> R<T> restResult(T data, int code, String msg) {
    R<T> r = new R<>();
    r.setCode(code);
    r.setData(data);
    r.setMsg(msg);
    return r;
  }

  /**
   * Checks if the response is an error.
   *
   * @param ret the response object
   * @param <T> the type of response data
   * @return true if error, false otherwise
   */
  public static <T> Boolean isError(R<T> ret) {
    return !isSuccess(ret);
  }

  /**
   * Checks if the response is successful.
   *
   * @param ret the response object
   * @param <T> the type of response data
   * @return true if successful, false otherwise
   */
  public static <T> Boolean isSuccess(R<T> ret) {
    return R.SUCCESS == ret.getCode();
  }
}
