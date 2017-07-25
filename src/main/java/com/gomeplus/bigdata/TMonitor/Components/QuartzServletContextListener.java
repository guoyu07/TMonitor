package com.gomeplus.bigdata.TMonitor.Components;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@Slf4j
@WebListener
public class QuartzServletContextListener implements ServletContextListener {
    @Autowired
    private Scheduler quartzScheduler;

    public void contextInitialized(ServletContextEvent sce) {
        log.info("quartzScheduler is: " + quartzScheduler);
        try {
            quartzScheduler.start();
        } catch (SchedulerException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }

        log.info("quartzScheduler is started...");
    }

    public void contextDestroyed(ServletContextEvent sce) {
        try {
            quartzScheduler.shutdown(true);
        } catch (SchedulerException ex) {
            log.error(ex.getMessage());
            return;
        }

        log.info("quartzScheduler is shutdown...");
    }
}
