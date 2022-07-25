package com.stellarnaus.seabass.service.api.impl.domain;

import com.stellarnaus.seabass.common.domain.TaskInfo;
import com.stellarnaus.seabass.service.api.domain.MessageParam;
import com.stellarnaus.seabass.support.pipeline.ProcessModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description 发送消息任务模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendTaskModel implements ProcessModel {

    /**
     * 消息模板Id
     */
    private Long messageTemplateId;

    /**
     * 请求参数
     */
    private List<MessageParam> messageParamList;

    /**
     * 发送任务的信息
     */
    private List<TaskInfo> taskInfo;


}
