package io.choerodon.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.iam.api.dto.RoleAssignmentSearchDTO;
import io.choerodon.iam.infra.dataobject.UserDO;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author wuguokai
 * @author superlee
 */
public interface UserMapper extends BaseMapper<UserDO> {
    List<UserDO> fulltextSearch(@Param("userDO") UserDO userDO,
                                @Param("params") String[] params);

    List<UserDO> selectUserWithRolesBySourceIdAndType(
            @Param("roleAssignmentSearchDTO") RoleAssignmentSearchDTO roleAssignmentSearchDTO,
            @Param("sourceId") Long sourceId,
            @Param("sourceType") String sourceType,
            @Param("start") Integer start,
            @Param("size") Integer size,
            @Param("params") String[] params);

    int selectCountUsers(@Param("roleAssignmentSearchDTO")
                                 RoleAssignmentSearchDTO roleAssignmentSearchDTO,
                         @Param("sourceId") Long sourceId,
                         @Param("sourceType") String sourceType,
                         @Param("params") String[] params);

    List<UserDO> selectTheUsersOfProjectByParamAndProjectId(@Param("projectId") Long projectId,
                                                            @Param("param") String param);

    Integer selectUserCountFromMemberRoleByOptions(@Param("roleId") Long roleId,
                                                   @Param("memberType") String memberType,
                                                   @Param("sourceId") Long sourceId,
                                                   @Param("sourceType") String sourceType,
                                                   @Param("roleAssignmentSearchDTO")
                                                           RoleAssignmentSearchDTO roleAssignmentSearchDTO);

    List selectUsersFromMemberRoleByOptions(@Param("roleId") Long roleId,
                                            @Param("memberType") String memberType,
                                            @Param("sourceId") Long sourceId,
                                            @Param("sourceType") String sourceType,
                                            @Param("roleAssignmentSearchDTO")
                                                    RoleAssignmentSearchDTO roleAssignmentSearchDTO);
}