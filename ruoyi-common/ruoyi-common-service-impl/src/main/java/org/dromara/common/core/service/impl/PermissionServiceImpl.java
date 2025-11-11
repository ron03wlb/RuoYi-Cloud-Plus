package org.dromara.common.core.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.service.PermissionService;
import org.dromara.system.api.RemotePermissionService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 权限服务
 *
 * @author Lion Li
 */
@Service
@Profile("!test")
public class PermissionServiceImpl implements PermissionService {

    @DubboReference
    private RemotePermissionService remotePermissionService;

    @Override
    public Set<String> getRolePermission(Long userId) {
        return remotePermissionService.getRolePermission(userId);
    }

    @Override
    public Set<String> getMenuPermission(Long userId) {
        return remotePermissionService.getMenuPermission(userId);
    }

}
