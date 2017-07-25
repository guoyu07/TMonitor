package com.gomeplus.bigdata.TMonitor.VO;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Data
public class JobVO {
    @JsonView(View.filter.class)
    @Getter @Setter private String jobName;

    @JsonView(View.filter.class)
    @Getter @Setter private String jobGroup;

    @JsonView(View.filter.class)
    @Getter @Setter private Map<String, Object> jobDataMap;

    @JsonView(View.filter.class)
    @Getter @Setter private List<TriggerVO> triggers;
}
