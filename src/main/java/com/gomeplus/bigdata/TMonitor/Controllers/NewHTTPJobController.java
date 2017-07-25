package com.gomeplus.bigdata.TMonitor.Controllers;

import com.gomeplus.bigdata.TMonitor.JobTypes.HTTPMonitorJob;
import com.gomeplus.bigdata.TMonitor.VO.JobVO;
import com.gomeplus.bigdata.TMonitor.VO.View;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class NewHTTPJobController extends NewJobController {
    @Autowired
    private Scheduler quartzScheduler;

    @JsonView(View.filter.class)
    @RequestMapping(value = "/new/http/job", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiOperation(value="创建一个新任务")
    public JobVO newHTTPJob(@RequestParam String jobNamePrefix,
                            @RequestParam String jobGroup,
                            @RequestParam int interval,
                            @RequestParam String triggerNamePrefix,
                            @RequestParam String triggerGroup,

                            @RequestParam(required = false) String callbackURL,
                            @RequestParam String toUser,

                            @RequestParam String requestURL,
                            @RequestParam(required = false, defaultValue = "GET")
                                        String requestMethod,
                            @RequestParam(required = false, defaultValue = "3000")
                                        int requestTimeoutMS,
                            @RequestParam(required = false) String requestHeaders,
                            @RequestParam(required = false) String requestBody,
                            @RequestParam(required = false, defaultValue = "text/plain")
                                        String mediaType,

                            @RequestParam(value = "expectedCode[]") int[] expectedCode,
                            @RequestParam(required = false, defaultValue = "0")
                                        int minContentLength,
                            @RequestParam(required = false) String bodyPattern
    ) throws SchedulerException{
        Map<String, Object> map = new HashMap<String, Object>();
        if (callbackURL != null)
            map.put("callbackURL", callbackURL);
        map.put("toUser", toUser);

        map.put("requestURL", requestURL);
        map.put("requestMethod", requestMethod.toUpperCase());
        map.put("requestTimeoutMS", requestTimeoutMS);
        if (requestHeaders != null)
            map.put("requestHeaders", requestHeaders);
        if (requestBody != null) {
            map.put("requestBody", requestBody);
            map.put("mediaType", mediaType);
        }

        map.put("expectedCode", expectedCode);
        map.put("minContentLength", minContentLength);
        if (bodyPattern != null)
            map.put("bodyPattern", bodyPattern);

        JobVO jobVO = addJob(HTTPMonitorJob.class, jobNamePrefix,
                jobGroup, map, interval, triggerNamePrefix,
                triggerGroup, quartzScheduler);
        return jobVO;
    };
}
