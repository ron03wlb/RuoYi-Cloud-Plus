package org.dromara.workflow.api;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.dromara.workflow.api.domain.RemoteCompleteTask;
import org.dromara.workflow.api.domain.RemoteStartProcess;
import org.dromara.workflow.api.domain.RemoteStartProcessReturn;

/**
 * 工作流服务(降级处理).
 *
 * @author Lion Li
 */
@Slf4j
public class RemoteWorkflowServiceMock implements RemoteWorkflowService {

  @Override
  public boolean deleteInstance(List<Long> businessIds) {
    log.warn(
        "Workflow service degradation triggered: method=deleteInstance, businessIds={}",
        businessIds);
    return false;
  }

  @Override
  public String getBusinessStatusByTaskId(Long taskId) {
    log.warn(
        "Workflow service degradation triggered: method=getBusinessStatusByTaskId, taskId={}",
        taskId);
    return null;
  }

  @Override
  public String getBusinessStatus(String businessId) {
    log.warn(
        "Workflow service degradation triggered: method=getBusinessStatus, businessId={}",
        businessId);
    return null;
  }

  @Override
  public void setVariable(Long instanceId, Map<String, Object> variable) {
    log.warn(
        "Workflow service degradation triggered: method=setVariable, instanceId={},"
            + " variableCount={}",
        instanceId,
        variable != null ? variable.size() : 0);
  }

  @Override
  public Map<String, Object> instanceVariable(Long instanceId) {
    log.warn(
        "Workflow service degradation triggered: method=instanceVariable, instanceId={}",
        instanceId);
    return null;
  }

  @Override
  public Long getInstanceIdByBusinessId(String businessId) {
    log.warn(
        "Workflow service degradation triggered: method=getInstanceIdByBusinessId, businessId={}",
        businessId);
    return null;
  }

  @Override
  public void syncDef(String tenantId) {
    log.warn("Workflow service degradation triggered: method=syncDef, tenantId={}", tenantId);
  }

  @Override
  public RemoteStartProcessReturn startWorkFlow(RemoteStartProcess startProcess) {
    log.warn(
        "Workflow service degradation triggered: method=startWorkFlow, flowCode={}, businessId={}",
        startProcess != null ? startProcess.getFlowCode() : null,
        startProcess != null ? startProcess.getBusinessId() : null);
    return null;
  }

  @Override
  public boolean completeTask(RemoteCompleteTask completeTask) {
    log.warn(
        "Workflow service degradation triggered: method=completeTask, taskId={}",
        completeTask != null ? completeTask.getTaskId() : null);
    return false;
  }

  @Override
  public boolean completeTask(Long taskId, String message) {
    log.warn(
        "Workflow service degradation triggered: method=completeTask, taskId={}, message={}",
        taskId,
        message);
    return false;
  }

  @Override
  public boolean startCompleteTask(RemoteStartProcess startProcess) {
    log.warn(
        "Workflow service degradation triggered: method=startCompleteTask, flowCode={},"
            + " businessId={}",
        startProcess != null ? startProcess.getFlowCode() : null,
        startProcess != null ? startProcess.getBusinessId() : null);
    return false;
  }
}
