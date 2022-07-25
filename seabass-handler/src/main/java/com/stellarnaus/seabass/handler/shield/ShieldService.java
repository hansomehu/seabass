package com.stellarnaus.seabass.handler.shield;

import com.stellarnaus.seabass.common.domain.TaskInfo;

/**
 * 屏蔽服务
 *
 * @author 3y
 */
public interface ShieldService {


    /**
     * 屏蔽消息
     * @param taskInfo
     */
    void shield(TaskInfo taskInfo);
}
