package com.stellarnaus.seabass.service.api.impl.service;

import cn.monitor4all.logRecord.annotation.OperationLog;
import com.stellarnaus.seabass.common.vo.BasicResultVO;
import com.stellarnaus.seabass.service.api.domain.BatchSendRequest;
import com.stellarnaus.seabass.service.api.domain.SendRequest;
import com.stellarnaus.seabass.service.api.domain.SendResponse;
import com.stellarnaus.seabass.service.api.impl.domain.SendTaskModel;
import com.stellarnaus.seabass.service.api.service.SendService;
import com.stellarnaus.seabass.support.pipeline.ProcessContext;
import com.stellarnaus.seabass.support.pipeline.ProcessController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 发送接口
 */
@Service
public class SendServiceImpl implements SendService {

    // 这里注入的是PipelineConfig类中的processController Bean，里面初始化好了templateConfig
    @Autowired
    private ProcessController processController;

    @Override
    @OperationLog(bizType = "SendService#send", bizId = "#sendRequest.messageTemplateId", msg = "#sendRequest")
    public SendResponse send(SendRequest sendRequest) {

        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .messageTemplateId(sendRequest.getMessageTemplateId())
                .messageParamList(Collections.singletonList(sendRequest.getMessageParam()))
                .build();

        ProcessContext context = ProcessContext.builder()
                .code(sendRequest.getCode())
                .processModel(sendTaskModel) // sendTaskModel中包含了任务核心信息：需要发送的数据、各种参数等
                .needBreak(false)
                .response(BasicResultVO.success()).build();

        ProcessContext process = processController.process(context);

        return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg());
    }

    @Override
    @OperationLog(bizType = "SendService#batchSend", bizId = "#batchSendRequest.messageTemplateId", msg = "#batchSendRequest")
    public SendResponse batchSend(BatchSendRequest batchSendRequest) {
        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .messageTemplateId(batchSendRequest.getMessageTemplateId())
                .messageParamList(batchSendRequest.getMessageParamList())
                .build();

        ProcessContext context = ProcessContext.builder()
                .code(batchSendRequest.getCode())
                .processModel(sendTaskModel)
                .needBreak(false)
                .response(BasicResultVO.success()).build();

        ProcessContext process = processController.process(context);

        return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg());
    }


}
