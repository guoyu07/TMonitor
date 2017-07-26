package com.gomeplus.bigdata.TMonitor.Controllers;

import com.gomeplus.bigdata.TMonitor.VO.JobVO;
import com.gomeplus.bigdata.TMonitor.VO.TriggerVO;
import com.gomeplus.bigdata.TMonitor.VO.View;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ListJobController {
    @Autowired
    private Scheduler quartzScheduler;

    @SuppressWarnings("unchecked")
    @JsonView(View.filter.class)
    @RequestMapping(value = "/list/jobs", method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(value = "列出所有的job及其对应的trigger列表")
    public List<JobVO> listJobs() throws SchedulerException{
        List<JobVO> jobs = new ArrayList<JobVO>();

        for (String jobGroupName: quartzScheduler.getJobGroupNames()) {
            for (JobKey jobKey: quartzScheduler.getJobKeys(
                    GroupMatcher.jobGroupEquals(jobGroupName))) {
                String jobName = jobKey.getName();
                String jobGroup = jobKey.getGroup();
                List<TriggerVO> triggers = new ArrayList<TriggerVO>();

                JobVO jobVO = new JobVO();
                jobVO.setJobName(jobName);
                jobVO.setJobGroup(jobGroup);
                jobVO.setTriggers(triggers);
                jobVO.setJobDataMap(
                        quartzScheduler.getJobDetail(jobKey).getJobDataMap());

                List<Trigger> triggerList = (List<Trigger>)
                        quartzScheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger: triggerList) {
                    TriggerVO triggerVO = new TriggerVO();
                    triggerVO.setTriggerName(trigger.getKey().getName());
                    triggerVO.setTriggerGroup(trigger.getKey().getGroup());
                    triggerVO.setNextFireTime(trigger.getNextFireTime());

                    triggers.add(triggerVO);
                }

                jobs.add(jobVO);
            }
        }
        return jobs;
    }
}
