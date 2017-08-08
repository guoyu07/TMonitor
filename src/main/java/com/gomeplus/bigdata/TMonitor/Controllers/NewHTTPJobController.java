package com.gomeplus.bigdata.TMonitor.Controllers;

import com.gomeplus.bigdata.TMonitor.JobTypes.HTTPMonitorJob;
import com.gomeplus.bigdata.TMonitor.VO.JobVO;
import com.gomeplus.bigdata.TMonitor.VO.View;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api(description = "创建一个新的HTTP监控任务")
@RestController
public class NewHTTPJobController extends NewJobController {
    @Autowired
    private Scheduler quartzScheduler;

    @JsonView(View.filter.class)
    @RequestMapping(value = "/new/http/job", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = "application/json")
    @ApiOperation(value="创建一个新的HTTP监控任务")
    public JobVO newHTTPJob(
                            @ApiParam(value = "job名称前缀", defaultValue = "jobNamePrefix")
                            @RequestParam String jobNamePrefix,
                            @ApiParam(value = "job组名称", defaultValue = "jobGroup")
                            @RequestParam String jobGroup,
                            @ApiParam(value = "时间间隔（单位：秒）", defaultValue = "60")
                            @RequestParam int interval,
                            @ApiParam(value = "trigger名称前缀",
                                    defaultValue = "triggerNamePrefix")
                            @RequestParam String triggerNamePrefix,
                            @ApiParam(value = "trigger组名称", defaultValue = "triggerGroup")
                            @RequestParam String triggerGroup,

                            @ApiParam(value = "报警时的回调URL")
                            @RequestParam(required = false) String callbackURL,
                            @ApiParam(value = "报警邮件的接收方")
                            @RequestParam(required = false) String toUser,

                            @ApiParam(value = "监控的URL")
                            @RequestParam String requestURL,
                            @ApiParam(value = "请求方法")
                            @RequestParam(required = false, defaultValue = "GET")
                                        String requestMethod,
                            @ApiParam(value = "请求的超时时间（单位：毫秒）")
                            @RequestParam(required = false, defaultValue = "3000")
                                        int requestTimeoutMS,
                            @ApiParam(value = "请求头列表。格式是header1=value1&header2=value2...")
                            @RequestParam(required = false) String requestHeaders,
                            @ApiParam(value = "请求体")
                            @RequestParam(required = false) String requestBody,
                            @ApiParam(value = "请求体的MIME类型")
                            @RequestParam(required = false, defaultValue = "text/plain")
                                        String mediaType,

                            @ApiParam(allowMultiple = true, value = "期望的响应码列表")
                            @RequestParam(value = "expectedCode[]") int[] expectedCode,
                            @ApiParam(value = "响应体的最小长度")
                            @RequestParam(required = false, defaultValue = "0")
                                        int minContentLength,
                            @ApiParam(value = "响应体应该满足的正则表达式")
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
