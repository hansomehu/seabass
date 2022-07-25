package com.stellarnaus.seabass.handler.deduplication.build;

import cn.hutool.core.date.DateUtil;
import com.stellarnaus.seabass.common.domain.TaskInfo;
import com.stellarnaus.seabass.common.enums.AnchorState;
import com.stellarnaus.seabass.common.enums.DeduplicationType;
import com.stellarnaus.seabass.handler.deduplication.DeduplicationParam;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author huskey
 * @date 2022/1/18
 */

@Service
public class FrequencyDeduplicationBuilder extends AbstractDeduplicationBuilder implements Builder {
    public FrequencyDeduplicationBuilder() {
        deduplicationType = DeduplicationType.FREQUENCY.getCode();
    }

    @Override
    public DeduplicationParam build(String deduplication, TaskInfo taskInfo) {
        DeduplicationParam deduplicationParam = getParamsFromConfig(deduplicationType, deduplication, taskInfo);
        if (deduplicationParam == null) {
            return null;
        }
        deduplicationParam.setDeduplicationTime((DateUtil.endOfDay(new Date()).getTime() - DateUtil.current()) / 1000);
        deduplicationParam.setAnchorState(AnchorState.RULE_DEDUPLICATION);
        return deduplicationParam;
    }
}
