package io.choerodon.iam.infra.dataobject;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.*;
import java.util.List;

/**
 * @author wuguokai
 */
@ModifyAudit
@VersionAudit
@Table(name = "fd_project")
public class ProjectDO extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String code;

    private Long organizationId;

    @Transient
    private String organizationName;

    @Column(name = "is_enabled")
    private Boolean enabled;

    @Transient
    private List<RoleDO> roles;

    private String type;

    @Transient
    private String typeName;

    public ProjectDO() {
    }

    public ProjectDO(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<RoleDO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDO> roles) {
        this.roles = roles;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "ProjectDO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", organizationId=" + organizationId +
                ", organizationName='" + organizationName + '\'' +
                ", enabled=" + enabled +
                ", roles=" + roles +
                ", type='" + type + '\'' +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
