package com.stellarnaus.seabass.handler.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 限流枚举
 *
 * @author 3y
 */
@Getter
@ToString
@AllArgsConstructor
public enum RateLimitStrategy {


    RATE_LIMIT_ON_REQUEST_NUM(10, "根据真实请求数限流"),
    RATE_LIMIT_ON_SEND_USER_NUM(20, "根据发送用户数限流"),
    ;

    private Integer code;
    private String description;


}
