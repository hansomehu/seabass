package com.stellarnaus.seabass.handler.handler;

import com.stellarnaus.seabass.common.domain.TaskInfo;

/**
 * @author 3y
 * 消息处理器
 */
public interface Handler {

    /**
     * 处理器
     * @param taskInfo
     */
    void doHandler(TaskInfo taskInfo);

}
