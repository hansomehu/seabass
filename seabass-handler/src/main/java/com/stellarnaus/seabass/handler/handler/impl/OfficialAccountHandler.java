package com.stellarnaus.seabass.handler.handler.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.stellarnaus.seabass.common.domain.TaskInfo;
import com.stellarnaus.seabass.common.dto.model.OfficialAccountsContentModel;
import com.stellarnaus.seabass.common.enums.ChannelType;
import com.stellarnaus.seabass.handler.domain.wechat.WeChatOfficialParam;
import com.stellarnaus.seabass.handler.handler.BaseHandler;
import com.stellarnaus.seabass.handler.handler.Handler;
import com.stellarnaus.seabass.handler.wechat.OfficialAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zyg
 * 微信服务号推送处理
 */
@Component
@Slf4j
public class OfficialAccountHandler extends BaseHandler implements Handler {

    @Autowired
    private OfficialAccountService officialAccountService;

    public OfficialAccountHandler() {
        channelCode = ChannelType.OFFICIAL_ACCOUNT.getCode();
    }

    @Override
    public boolean handler(TaskInfo taskInfo) {
        // 构建微信模板消息
        OfficialAccountsContentModel contentModel = (OfficialAccountsContentModel) taskInfo.getContentModel();
        WeChatOfficialParam officialParam = WeChatOfficialParam.builder()
                .openIds(taskInfo.getReceiver())
                .messageTemplateId(taskInfo.getMessageTemplateId())
                .sendAccount(taskInfo.getSendAccount())
                .data(contentModel.getMap())
                .build();

        // 微信模板消息需要记录响应结果
        try {
            List<String> messageIds = officialAccountService.send(officialParam);
            log.info("OfficialAccountHandler#handler successfully messageIds:{}", messageIds);
            return true;
        } catch (Exception e) {
            log.error("OfficialAccountHandler#handler fail:{},params:{}",
                    Throwables.getStackTraceAsString(e), JSON.toJSONString(taskInfo));
        }
        return false;
    }

}

