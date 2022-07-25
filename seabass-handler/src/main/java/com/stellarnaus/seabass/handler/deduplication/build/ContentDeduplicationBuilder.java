package com.stellarnaus.seabass.handler.deduplication.build;

import com.stellarnaus.seabass.common.domain.TaskInfo;
import com.stellarnaus.seabass.common.enums.AnchorState;
import com.stellarnaus.seabass.common.enums.DeduplicationType;
import com.stellarnaus.seabass.handler.deduplication.DeduplicationParam;
import org.springframework.stereotype.Service;


/**
 * @author huskey
 * @date 2022/1/18
 */
@Service
public class ContentDeduplicationBuilder extends AbstractDeduplicationBuilder implements Builder {

    public ContentDeduplicationBuilder() {
        deduplicationType = DeduplicationType.CONTENT.getCode();
    }

    @Override
    public DeduplicationParam build(String deduplication, TaskInfo taskInfo) {
        DeduplicationParam deduplicationParam = getParamsFromConfig(deduplicationType, deduplication, taskInfo);
        if (deduplicationParam == null) {
           return null;
        }
        deduplicationParam.setAnchorState(AnchorState.CONTENT_DEDUPLICATION);
        return deduplicationParam;

    }
}
