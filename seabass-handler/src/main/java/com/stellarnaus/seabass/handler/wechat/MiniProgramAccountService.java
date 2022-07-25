package com.stellarnaus.seabass.handler.wechat;

import com.stellarnaus.seabass.handler.domain.wechat.WeChatMiniProgramParam;

/**
 * @author sunql
 */
public interface MiniProgramAccountService {

    /**
     * 发送订阅消息
     *
     * @param miniProgramParam 订阅消息参数
     * @return
     * @throws Exception
     */
    void send(WeChatMiniProgramParam miniProgramParam) throws Exception;

}
