package com.gomeplus.bigdata.TMonitor.VO;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
public class TriggerVO {
    @JsonView(View.filter.class)
    @Getter @Setter private String triggerName;

    @JsonView(View.filter.class)
    @Getter @Setter private String triggerGroup;

    @JsonView(View.filter.class)
    @Getter @Setter private Date nextFireTime;
}
