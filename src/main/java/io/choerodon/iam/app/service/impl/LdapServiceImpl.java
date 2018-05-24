package io.choerodon.iam.app.service.impl;

import javax.naming.ldap.LdapContext;

import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.api.dto.LdapDTO;
import io.choerodon.iam.api.dto.UserDTO;
import io.choerodon.iam.app.service.LdapService;
import io.choerodon.iam.domain.oauth.entity.LdapE;
import io.choerodon.iam.domain.repository.LdapRepository;
import io.choerodon.iam.domain.repository.OrganizationRepository;
import io.choerodon.iam.infra.common.utils.ldap.LdapSyncUserTask;
import io.choerodon.iam.infra.common.utils.ldap.LdapUtil;
import io.choerodon.iam.infra.dataobject.LdapDO;

/**
 * @author wuguokai
 */
@Component
public class LdapServiceImpl implements LdapService {
    private LdapRepository ldapRepository;
    private OrganizationRepository organizationRepository;
    private LdapSyncUserTask ldapSyncUserTask;
    private static final String ORGANIZATION_NOT_EXIST_EXCEPTION = "error.organization.not.exist";
    private static final String LDAP_NOT_EXIST_EXCEPTION = "error.ldap.not.exist";

    public LdapServiceImpl(LdapRepository ldapRepository, OrganizationRepository organizationRepository,
                           LdapSyncUserTask ldapSyncUserTask) {
        this.ldapRepository = ldapRepository;
        this.organizationRepository = organizationRepository;
        this.ldapSyncUserTask = ldapSyncUserTask;
    }

    @Override
    public LdapDTO create(Long orgId, LdapDTO ldapDTO) {
        if (organizationRepository.selectByPrimaryKey(orgId) == null) {
            throw new CommonException(ORGANIZATION_NOT_EXIST_EXCEPTION);
        }
        ldapDTO.setOrganizationId(orgId);
        LdapE ldapE = ldapRepository.create(ConvertHelper.convert(ldapDTO, LdapE.class));
        return ConvertHelper.convert(ldapE, LdapDTO.class);
    }

    @Override
    public LdapDTO update(Long orgId, Long id, LdapDTO ldapDTO) {
        if (organizationRepository.selectByPrimaryKey(orgId) == null) {
            throw new CommonException(ORGANIZATION_NOT_EXIST_EXCEPTION);
        }
        if (ldapRepository.query(id) == null) {
            throw new CommonException(LDAP_NOT_EXIST_EXCEPTION);
        }
        LdapE ldapE = ldapRepository.update(id, ConvertHelper.convert(ldapDTO, LdapE.class));
        return ConvertHelper.convert(ldapE, LdapDTO.class);
    }

    @Override
    public LdapDTO queryByOrgId(Long orgId) {
        if (organizationRepository.selectByPrimaryKey(orgId) == null) {
            throw new CommonException(ORGANIZATION_NOT_EXIST_EXCEPTION);
        }
        LdapE ldapE = ldapRepository.queryByOrgId(orgId);
        if (ldapE == null) {
            throw new CommonException(LDAP_NOT_EXIST_EXCEPTION);
        }
        return ConvertHelper.convert(ldapE, LdapDTO.class);
    }

    @Override
    public Boolean delete(Long orgId, Long id) {
        if (organizationRepository.selectByPrimaryKey(orgId) == null) {
            throw new CommonException(ORGANIZATION_NOT_EXIST_EXCEPTION);
        }
        if (ldapRepository.query(id) == null) {
            throw new CommonException(LDAP_NOT_EXIST_EXCEPTION);
        }
        return ldapRepository.delete(id);
    }

    @Override
    public Boolean testConnect(Long orgId) {
        LdapDTO ldapDTO = queryByOrgId(orgId);
        LdapContext ldapContext = LdapUtil.ldapConnect(ldapDTO.getServerAddress(), ldapDTO.getBaseDn());
        Boolean isConnect = true;
        if (ldapContext == null) {
            isConnect = false;
        }
        return isConnect;
    }

    @Override
    public void syncLdapUser(Long orgId, UserDTO userDTO) {
        LdapDTO ldapDTO = queryByOrgId(orgId);
        LdapContext ldapContext = LdapUtil.authenticate(userDTO.getLoginName(),
                userDTO.getPassword(), ConvertHelper.convert(ldapDTO, LdapDO.class));
        if (ldapContext == null) {
            throw new CommonException("error.ldap.connect");
        }
        ldapSyncUserTask.syncLDAPUser(ldapContext, orgId, null);
    }
}