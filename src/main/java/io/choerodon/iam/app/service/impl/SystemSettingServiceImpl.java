package io.choerodon.iam.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.api.dto.SystemSettingDTO;
import io.choerodon.iam.api.dto.payload.SystemSettingEventPayload;
import io.choerodon.iam.app.service.SystemSettingService;
import io.choerodon.iam.domain.repository.SystemSettingRepository;
import io.choerodon.iam.infra.common.utils.MockMultipartFile;
import io.choerodon.iam.infra.common.utils.SagaTopic;
import io.choerodon.iam.infra.dataobject.SystemSettingDO;
import io.choerodon.iam.infra.feign.FileFeignClient;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

/**
 * @author zmf
 * @since 2018-10-15
 */
@Service
@Saga(code = SagaTopic.SystemSetting.SYSTEM_SETTING_UPDATE, description = "iam更改系统设置", inputSchemaClass = SystemSettingEventPayload.class)
public class SystemSettingServiceImpl implements SystemSettingService {
    private final FileFeignClient fileFeignClient;
    private final SystemSettingRepository systemSettingRepository;
    private final SagaClient sagaClient;
    private final ObjectMapper objectMapper;
    private final String ERROR_UPDATE_SYSTEM_SETTING_EVENT_SEND = "error.system.setting.update.send.event";

    @Autowired
    public SystemSettingServiceImpl(FileFeignClient fileFeignClient, SystemSettingRepository systemSettingRepository, SagaClient sagaClient) {
        this.fileFeignClient = fileFeignClient;
        this.systemSettingRepository = systemSettingRepository;
        this.sagaClient = sagaClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String uploadFavicon(MultipartFile file) {
        return uploadFile(file);
    }

    @Override
    public String uploadSystemLogo(MultipartFile file) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(file.getInputStream()).forceSize(80, 80).toOutputStream(outputStream);
            file = new MockMultipartFile(file.getName(), file.getOriginalFilename(), file.getContentType(), outputStream.toByteArray());
            return uploadFile(file);
        } catch (Exception e) {
            throw new CommonException("error.setting.logo.save.failure");
        }
    }

    @Override
    public SystemSettingDTO addSetting(SystemSettingDTO systemSettingDTO) {
        // 执行业务代码
        SystemSettingDTO dto = systemSettingRepository.addSetting(convert(systemSettingDTO));

        // 触发 saga 流程
        triggerSagaFlow(dto);

        return dto;
    }

    @Override
    public SystemSettingDTO updateSetting(SystemSettingDTO systemSettingDTO) {
        // 执行业务代码
        SystemSettingDTO dto = systemSettingRepository.updateSetting(convert(systemSettingDTO));

        // 触发 saga 流程
        triggerSagaFlow(dto);

        return dto;
    }

    /**
     * 触发 saga 流程
     *
     * @param dto 返回的 dto
     */
    private void triggerSagaFlow(final SystemSettingDTO dto) {
        try {
            SystemSettingEventPayload payload = new SystemSettingEventPayload();
            BeanUtils.copyProperties(dto, payload);
            sagaClient.startSaga(SagaTopic.SystemSetting.SYSTEM_SETTING_UPDATE, new StartInstanceDTO(objectMapper.writeValueAsString(payload)));
        } catch (Exception e) {
            throw new CommonException(ERROR_UPDATE_SYSTEM_SETTING_EVENT_SEND, e);
        }
    }

    @Override
    public void resetSetting() {
        // 执行业务代码
        systemSettingRepository.resetSetting();

        // 触发 saga 流程
        try {
            sagaClient.startSaga(SagaTopic.SystemSetting.SYSTEM_SETTING_UPDATE, new StartInstanceDTO(""));
        } catch (Exception e) {
            throw new CommonException(ERROR_UPDATE_SYSTEM_SETTING_EVENT_SEND, e);
        }
    }

    @Override
    public SystemSettingDTO getSetting() {
        return systemSettingRepository.getSetting();
    }

    private String uploadFile(MultipartFile file) {
        return fileFeignClient.uploadFile("iam-service", file.getOriginalFilename(), file).getBody();
    }

    private SystemSettingDO convert(SystemSettingDTO systemSettingDTO) {
        SystemSettingDO systemSettingDO = new SystemSettingDO();
        BeanUtils.copyProperties(systemSettingDTO, systemSettingDO);
        return systemSettingDO;
    }
}
