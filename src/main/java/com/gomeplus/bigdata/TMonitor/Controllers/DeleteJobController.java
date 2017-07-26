package com.gomeplus.bigdata.TMonitor.Controllers;

import com.gomeplus.bigdata.TMonitor.VO.JobVO;
import com.gomeplus.bigdata.TMonitor.VO.TriggerVO;
import com.gomeplus.bigdata.TMonitor.VO.View;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DeleteJobController {
    @Autowired
    private Scheduler quartzScheduler;

    @SuppressWarnings("unchecked")
    @JsonView(View.filter.class)
    @RequestMapping(value = "/delete/job", method = RequestMethod.DELETE,
            produces = "application/json")
    @ApiOperation("根据job名称和jobGroup名称，删除job")
    public JobVO deleteJob(@RequestParam String jobName,
                           @RequestParam String jobGroup
    ) throws SchedulerException {
        JobVO deletedJob = new JobVO();
        deletedJob.setJobName(jobName);
        deletedJob.setJobGroup(jobGroup);
        List<TriggerVO> triggers = new ArrayList<TriggerVO>();
        deletedJob.setTriggers(triggers);

        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        List<org.quartz.Trigger> triggerList = (List<Trigger>)
                quartzScheduler.getTriggersOfJob(jobKey);
        for (Trigger trigger: triggerList) {
            TriggerKey triggerKey = trigger.getKey();

            TriggerVO triggerVO = new TriggerVO();
            triggerVO.setTriggerName(triggerKey.getName());
            triggerVO.setTriggerGroup(triggerKey.getGroup());
            triggerVO.setNextFireTime(trigger.getNextFireTime());

            quartzScheduler.pauseTrigger(triggerKey);
            quartzScheduler.unscheduleJob(triggerKey);
            quartzScheduler.deleteJob(jobKey);

            triggers.add(triggerVO);
        }

        return deletedJob;
    }
}
