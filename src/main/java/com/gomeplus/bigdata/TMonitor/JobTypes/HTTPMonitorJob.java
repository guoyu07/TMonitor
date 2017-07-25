package com.gomeplus.bigdata.TMonitor.JobTypes;

import com.gomeplus.bigdata.TMonitor.Models.HTTPRequestMaker;
import com.gomeplus.bigdata.TMonitor.Models.MailSender;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class HTTPMonitorJob implements Job {
    private final static MailSender mailSender = new MailSender();

    private Response makeRequest(HTTPRequestMaker httpRequestMaker,
                                 JobDataMap jobDataMap) throws IOException {
        int requestTimeoutMS = (Integer)jobDataMap.get("requestTimeoutMS");
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(requestTimeoutMS, TimeUnit.MILLISECONDS)
                .readTimeout(requestTimeoutMS, TimeUnit.MILLISECONDS)
                .writeTimeout(requestTimeoutMS, TimeUnit.MILLISECONDS)
                .build();

        Request.Builder requestBuilder = new Request.Builder()
                .url(httpRequestMaker.getRequestURL());
        if (httpRequestMaker.getRequestHeaders() != null)
            for (List<String> header: httpRequestMaker.getRequestHeaders())
                requestBuilder.addHeader(header.get(0), header.get(1));

        if (httpRequestMaker.getRequestBody() == null)
            requestBuilder.method(
                    httpRequestMaker.getRequestMethod(), (RequestBody) null);
        else {
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse((String) jobDataMap.get("mediaType")),
                    httpRequestMaker.getRequestBody());
            requestBuilder.method(httpRequestMaker.getRequestMethod(), requestBody);
        }

        Request request = requestBuilder.build();
        Call call = okHttpClient.newCall(request);
        return call.execute();
    }

    private String validateResponse(Response response,
                                    JobDataMap jobDataMap) throws IOException {
        int[] expectedCode = (int[]) jobDataMap.get("expectedCode");
        boolean isExpected = false;
        for (int code: expectedCode)
            if (code == response.code())
                isExpected = true;
        if (!isExpected)
            return "status code: " + response.code() + " is not satisfied";

        int minContentLength = (Integer) jobDataMap.get("minContentLength");
        String body = response.body().string();
        if (body.length() < minContentLength)
            return "minContentLength: " + minContentLength + " is not satisfied";
        String patternString;
        if (jobDataMap.get("bodyPattern") != null) {
            Pattern pattern = Pattern.compile(patternString =
                    (String) jobDataMap.get("bodyPattern"),
                    Pattern.DOTALL);
            Matcher matcher = pattern.matcher(body);
            if (!matcher.matches())
                return "bodyPattern: " + patternString + " is not satisfied";
        }

        return null;
    }

    public void alert(String errorMessage, JobDataMap jobDataMap) {
        log.error("errorMessage: " + errorMessage);

        if (jobDataMap.get("callbackURL") != null) {
            String callbackURL = (String) jobDataMap.get("callbackURL");
            System.out.println("callbackURL = " + callbackURL);
            return;
        }

        if (jobDataMap.get("toUser") == null)
            return;

        String toUser = (String) jobDataMap.get("toUser");
        try {
            mailSender.sendMessage(toUser, "API Monitor报警",
                    errorMessage, null);
        } catch (MessagingException ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
        }
    }

    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        HTTPRequestMaker httpRequestMaker = HTTPRequestMaker.fromMap(jobDataMap);
        log.info("httpRequestMaker = " + httpRequestMaker.toString());

        String errorMessage;
        try {
            Response response = makeRequest(httpRequestMaker, jobDataMap);
            errorMessage = validateResponse(response, jobDataMap);
            if (errorMessage == null)
                return;
        } catch (Exception ex) {
            ex.printStackTrace();
            errorMessage = ex.getMessage();
        }

        alert(errorMessage, jobDataMap);
    }
}
