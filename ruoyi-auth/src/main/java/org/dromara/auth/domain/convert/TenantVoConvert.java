package org.dromara.auth.domain.convert;

import io.github.linpeilie.BaseMapper;
import org.dromara.auth.domain.vo.TenantListVo;
import org.dromara.system.api.domain.vo.RemoteTenantVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Tenant view object converter for mapping between RemoteTenantVo and TenantListVo.
 *
 * @author zhujie
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TenantVoConvert extends BaseMapper<RemoteTenantVo, TenantListVo> {}
