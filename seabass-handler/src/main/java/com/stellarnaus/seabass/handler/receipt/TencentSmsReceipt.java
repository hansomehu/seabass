package com.stellarnaus.seabass.handler.receipt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.stellarnaus.seabass.common.constant.SendAccountConstant;
import com.stellarnaus.seabass.common.dto.account.TencentSmsAccount;
import com.stellarnaus.seabass.common.enums.SmsStatus;
import com.stellarnaus.seabass.support.dao.SmsRecordDao;
import com.stellarnaus.seabass.support.domain.SmsRecord;
import com.stellarnaus.seabass.support.utils.AccountUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 拉取腾讯云短信回执信息
 *
 * @author 3y
 */
@Component
@Slf4j
public class TencentSmsReceipt {


    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private SmsRecordDao smsRecordDao;


    /**
     * 拉取消息并入库
     */
    public void pull() {

        // 获取腾讯云账号信息
        TencentSmsAccount account = accountUtils.getAccount(10, SendAccountConstant.SMS_ACCOUNT_KEY, SendAccountConstant.SMS_PREFIX, TencentSmsAccount.class);
        try {
            SmsClient client = getSmsClient(account);

            // 每次拉取10条
            PullSmsSendStatusRequest req = new PullSmsSendStatusRequest();
            req.setLimit(10L);
            req.setSmsSdkAppId(account.getSmsSdkAppId());

            PullSmsSendStatusResponse resp = client.PullSmsSendStatus(req);
            List<SmsRecord> smsRecordList = new ArrayList<>();
            if (resp != null && resp.getPullSmsSendStatusSet() != null && resp.getPullSmsSendStatusSet().length > 0) {
                log.debug("receipt sms:{}", JSON.toJSONString(resp.getPullSmsSendStatusSet()));
                for (PullSmsSendStatus pullSmsSendStatus : resp.getPullSmsSendStatusSet()) {
                    SmsRecord smsRecord = SmsRecord.builder()
                            .sendDate(Integer.valueOf(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN)))
                            .messageTemplateId(0L)
                            .phone(Long.valueOf(pullSmsSendStatus.getSubscriberNumber()))
                            .supplierId(account.getSupplierId())
                            .supplierName(account.getSupplierName())
                            .msgContent("")
                            .seriesId(pullSmsSendStatus.getSerialNo())
                            .chargingNum(0)
                            .status("SUCCESS".equals(pullSmsSendStatus.getReportStatus()) ? SmsStatus.RECEIVE_SUCCESS.getCode() : SmsStatus.RECEIVE_FAIL.getCode())
                            .reportContent(pullSmsSendStatus.getDescription())
                            .updated(Math.toIntExact(pullSmsSendStatus.getUserReceiveTime()))
                            .created(Math.toIntExact(DateUtil.currentSeconds()))
                            .build();
                    smsRecordList.add(smsRecord);
                }
            }
            if (!CollUtil.isEmpty(smsRecordList)) {
                smsRecordDao.saveAll(smsRecordList);
            }
        } catch (Exception e) {
            log.error("TencentSmsReceipt#init fail!{}", Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 构造smsClient
     *
     * @param account
     * @return
     */
    private SmsClient getSmsClient(TencentSmsAccount account) {
        Credential cred = new Credential(account.getSecretId(), account.getSecretKey());
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(account.getUrl());
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        return new SmsClient(cred, account.getRegion(), clientProfile);
    }


}
