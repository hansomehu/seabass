package com.stellarnaus.seabass.handler.deduplication.service;


import com.stellarnaus.seabass.handler.deduplication.DeduplicationParam;

/**
 * @author huskey
 * @date 2022/1/18
 */
public interface DeduplicationService {

    /**
     * 去重
     * @param param
     */
    void deduplication(DeduplicationParam param);
}
