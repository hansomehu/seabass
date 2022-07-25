package com.stellarnaus.seabass.handler.receipt;


import com.stellarnaus.seabass.support.config.SupportThreadPoolConfig;
//import com.tencentcloudapi.sms.v20210111.SmsClient;
//import com.tencentcloudapi.sms.v20210111.models.PullSmsSendStatus;
//import com.tencentcloudapi.sms.v20210111.models.PullSmsSendStatusRequest;
//import com.tencentcloudapi.sms.v20210111.models.PullSmsSendStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 拉取短信回执信息
 *
 * @author 3y
 */
@Component
@Slf4j
public class SmsReceipt {

    @Autowired
    private TencentSmsReceipt tencentSmsReceipt;

    @Autowired
    private YunPianSmsReceipt yunPianSmsReceipt;

    @PostConstruct
    private void init() {
        SupportThreadPoolConfig.getPendingSingleThreadPool().execute(() -> {
            while (true) {
                tencentSmsReceipt.pull();
                yunPianSmsReceipt.pull();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }
            }
        });
    }
}
