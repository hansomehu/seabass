package com.stellarnaus.seabass.handler.handler;

import com.stellarnaus.seabass.common.domain.AnchorInfo;
import com.stellarnaus.seabass.common.domain.TaskInfo;
import com.stellarnaus.seabass.common.enums.AnchorState;
import com.stellarnaus.seabass.handler.flowcontrol.FlowControlParam;
import com.stellarnaus.seabass.handler.flowcontrol.FlowControlService;
import com.stellarnaus.seabass.support.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @author 3y
 * 发送各个渠道的handler
 */
public abstract class BaseHandler implements Handler {
    @Autowired
    private HandlerMapper handlerMapper;
    @Autowired
    private LogUtils logUtils;
    @Autowired
    private FlowControlService flowControlService;

    /**
     * 标识渠道的Code
     * 子类初始化的时候指定
     */
    // ChannelType.Class
    protected Integer channelCode;

    /**
     * 限流相关的参数
     * 子类初始化的时候指定
     */
    protected FlowControlParam flowControlParam;

    /**
     * 初始化渠道与Handler的映射关系
     */
    @PostConstruct // 加上该注解，子类在实例化的时候会调用该方法
    private void init() {
        // channelCode 具体的值会在抽象类的子类加载的时候导入，this则指代具体子类
        // 双亲委派机制
        handlerMapper.putHandler(channelCode, this);
    }

    /**
     * 流量控制
     *
     * @param taskInfo
     */
    public void flowControl(TaskInfo taskInfo) {
        // 只有子类指定了限流参数，才需要限流
        if (flowControlParam != null) {
            flowControlService.flowControl(taskInfo, flowControlParam);
        }
    }
    @Override
    public void doHandler(TaskInfo taskInfo) {
        flowControl(taskInfo);
        if (handler(taskInfo)) {
            logUtils.print(AnchorInfo.builder().state(AnchorState.SEND_SUCCESS.getCode()).businessId(taskInfo.getBusinessId()).ids(taskInfo.getReceiver()).build());
            return;
        }
        logUtils.print(AnchorInfo.builder().state(AnchorState.SEND_FAIL.getCode()).businessId(taskInfo.getBusinessId()).ids(taskInfo.getReceiver()).build());
    }


    /**
     * 统一处理的handler接口
     *
     * @param taskInfo
     * @return
     */
    public abstract boolean handler(TaskInfo taskInfo);


}
