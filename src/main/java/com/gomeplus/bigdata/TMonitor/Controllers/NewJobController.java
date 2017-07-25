package com.gomeplus.bigdata.TMonitor.Controllers;

import com.gomeplus.bigdata.TMonitor.VO.JobVO;
import com.gomeplus.bigdata.TMonitor.VO.TriggerVO;
import org.quartz.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class NewJobController {
    public final JobVO addJob(Class<? extends Job> jobClass,
                              String jobNamePrefix,
                              String jobGroup,
                              Map<? extends String, ?> map,
                              int interval,
                              String triggerNamePrefix,
                              String triggerGroup,
                              Scheduler scheduler
    ) throws SchedulerException {
        JobVO newJob = new JobVO();

        JobDetail jobDetail = JobBuilder
                .newJob(jobClass)
                .withIdentity(
                        jobNamePrefix + ":" + UUID.randomUUID().toString(),
                        jobGroup
                )
                .build();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        for (Map.Entry<? extends String, ?> entry: map.entrySet())
            jobDataMap.put(entry.getKey(), entry.getValue());

        newJob.setJobName(jobDetail.getKey().getName());
        newJob.setJobGroup(jobDetail.getKey().getGroup());
        newJob.setJobDataMap(jobDataMap);

        Trigger simpleTrigger = TriggerBuilder
                .newTrigger()
                .withSchedule(SimpleScheduleBuilder
                        .simpleSchedule()
                        .withIntervalInSeconds(interval)
                        .repeatForever())
                .withIdentity(
                        triggerNamePrefix + ":" + UUID.randomUUID().toString(),
                        triggerGroup)
                .build();

        TriggerVO triggerVO = new TriggerVO();
        triggerVO.setTriggerName(simpleTrigger.getKey().getName());
        triggerVO.setTriggerGroup(simpleTrigger.getKey().getGroup());
        triggerVO.setNextFireTime(simpleTrigger.getNextFireTime());

        List<TriggerVO> triggerVOList = new ArrayList<TriggerVO>();
        triggerVOList.add(triggerVO);
        newJob.setTriggers(triggerVOList);

        scheduler.scheduleJob(jobDetail, simpleTrigger);
        return newJob;
    }
}

