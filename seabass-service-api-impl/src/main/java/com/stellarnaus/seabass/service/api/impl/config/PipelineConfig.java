package com.stellarnaus.seabass.service.api.impl.config;


import com.stellarnaus.seabass.service.api.enums.BusinessCode;
import com.stellarnaus.seabass.service.api.impl.action.AfterParamCheckAction;
import com.stellarnaus.seabass.service.api.impl.action.AssembleAction;
import com.stellarnaus.seabass.service.api.impl.action.PreParamCheckAction;
import com.stellarnaus.seabass.service.api.impl.action.SendMqAction;
import com.stellarnaus.seabass.support.pipeline.ProcessController;
import com.stellarnaus.seabass.support.pipeline.ProcessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * api层的pipeline配置类
 * @author 3y
 */
@Configuration
public class PipelineConfig {

    @Autowired
    private PreParamCheckAction preParamCheckAction;
    @Autowired
    private AssembleAction assembleAction;
    @Autowired
    private AfterParamCheckAction afterParamCheckAction;
    @Autowired
    private SendMqAction sendMqAction;

    /**
     * 普通发送执行流程
     * 1. 前置参数校验
     * 2. 组装参数
     * 3. 后置参数校验
     * 4. 发送消息至MQ
     * @return
     */
    @Bean("commonSendTemplate")
    public ProcessTemplate commonSendTemplate() {
        // 业务链执行模板
        ProcessTemplate processTemplate = new ProcessTemplate();
        processTemplate.setProcessList(Arrays.asList(preParamCheckAction, assembleAction,
                afterParamCheckAction, sendMqAction));
        return processTemplate;
    }

    /**
     * pipeline流程控制器，负责在代码执行前检查上下文、执行模板等异常
     * 目前暂定只有 普通发送的流程
     * 后续扩展则加BusinessCode和ProcessTemplate
     *
     * @return
     */
    @Bean
    public ProcessController processControllerBuilder() {
        ProcessController processController = new ProcessController();
        Map<String, ProcessTemplate> templateConfig = new HashMap<>(4);
        // 注入的是 commonSendTemplate Bean
        // 目前暂定只有 普通发送的流程
        templateConfig.put(BusinessCode.COMMON_SEND.getCode(), commonSendTemplate());
        processController.setTemplateConfig(templateConfig);
        return processController;
    }

}
