package com.stellarnaus.seabass.handler.handler;


import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * channel->Handler的映射关系
 *
 * @author 3y
 */
@Component
public class HandlerMapper {

    private Map<Integer, Handler> channelCodeHandlerMap = new HashMap<Integer, Handler>(128);

    public void putHandler(Integer channelCode, Handler handler) {
        channelCodeHandlerMap.put(channelCode, handler);
    }

    public Handler route(Integer channelCode) {
        return channelCodeHandlerMap.get(channelCode);
    }
}
