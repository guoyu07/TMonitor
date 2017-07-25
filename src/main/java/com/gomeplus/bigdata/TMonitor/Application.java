package com.gomeplus.bigdata.TMonitor;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
@ServletComponentScan(basePackages = "com.gomeplus.bigdata.TMonitor")
public class Application {
    @Bean(name = "quartzScheduler")
    public Scheduler getQuartzScheduler() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        return schedulerFactory.getScheduler();
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        SpringApplication.run(Application.class, args);

        log.info("==========");
        log.info("server is started in " +
                (System.currentTimeMillis() - startTime) / 1000 + "s");
        log.info("==========");
    }
}

