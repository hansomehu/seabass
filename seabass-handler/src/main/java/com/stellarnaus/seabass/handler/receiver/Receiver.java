package com.stellarnaus.seabass.handler.receiver;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.stellarnaus.seabass.common.domain.AnchorInfo;
import com.stellarnaus.seabass.common.domain.LogParam;
import com.stellarnaus.seabass.common.domain.TaskInfo;
import com.stellarnaus.seabass.common.enums.AnchorState;
import com.stellarnaus.seabass.handler.pending.Task;
import com.stellarnaus.seabass.handler.pending.TaskThreadPoolMapper;
import com.stellarnaus.seabass.handler.utils.GroupIdMappingUtils;
import com.stellarnaus.seabass.support.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author 3y
 * 消费MQ的消息
 */
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Receiver {
    private static final String LOG_BIZ_TYPE = "Receiver#consumer";
    @Autowired
    private ApplicationContext context;

    @Autowired
    private TaskThreadPoolMapper taskPendingHolder;

    @Autowired
    private LogUtils logUtils;

    @KafkaListener(topics = "#{'${austin.business.topic.name}'}")
    public void consumer(ConsumerRecord<?, String> consumerRecord, @Header(KafkaHeaders.GROUP_ID) String topicGroupId) {
        Optional<String> kafkaMessage = Optional.ofNullable(consumerRecord.value());
        if (kafkaMessage.isPresent()) {

            List<TaskInfo> taskInfoLists = JSON.parseArray(kafkaMessage.get(), TaskInfo.class);

            String messageGroupId = GroupIdMappingUtils.getGroupIdByTaskInfo(CollUtil.getFirst(taskInfoLists.iterator()));

            /**
             * 每个消费者组 只消费 他们自身关心的消息
             */
            if (topicGroupId.equals(messageGroupId)) {
                for (TaskInfo taskInfo : taskInfoLists) {
                    logUtils.print(LogParam.builder().bizType(LOG_BIZ_TYPE).object(taskInfo).build(), AnchorInfo.builder().ids(taskInfo.getReceiver()).businessId(taskInfo.getBusinessId()).state(AnchorState.RECEIVE.getCode()).build());
                    // 找到对应的impl来构建task
                    Task task = context.getBean(Task.class).setTaskInfo(taskInfo);
                    taskPendingHolder.route(topicGroupId).execute(task);
                }
            }
        }
    }
}
