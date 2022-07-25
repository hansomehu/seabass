package com.stellarnaus.seabass.service.api.impl.action;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Throwables;
import com.stellarnaus.seabass.common.enums.RespStatusEnum;
import com.stellarnaus.seabass.common.vo.BasicResultVO;
import com.stellarnaus.seabass.service.api.impl.domain.SendTaskModel;
import com.stellarnaus.seabass.support.pipeline.BusinessProcess;
import com.stellarnaus.seabass.support.pipeline.ProcessContext;
import com.stellarnaus.seabass.support.utils.KafkaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author 3y
 * 将消息发送到MQ
 */
@Slf4j
@Service
public class SendMqAction implements BusinessProcess<SendTaskModel> {

    @Autowired
    private KafkaUtils kafkaUtils;

    @Value("${austin.business.topic.name}")
    private String topicName;

    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel sendTaskModel = context.getProcessModel();
        String message = JSON.toJSONString(sendTaskModel.getTaskInfo(), new SerializerFeature[]{SerializerFeature.WriteClassName});

        try {
            kafkaUtils.send(topicName, message);
        } catch (Exception e) {
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR));
            log.error("send kafka fail! e:{},params:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(CollUtil.getFirst(sendTaskModel.getTaskInfo().listIterator())));
        }
    }
}
