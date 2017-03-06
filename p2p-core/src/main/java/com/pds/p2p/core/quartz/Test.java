package com.pds.p2p.core.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.LoggerFactory;

public class Test {
    private static org.slf4j.Logger log = LoggerFactory.getLogger(Test.class);
    private static int n = 0;

    public static void main(String[] args) {
        try {
            QuartzManager quartzManager = new QuartzManager();
            quartzManager.addJob("hi", TestJob.class, "59 4 0-23 * * ?");

            quartzManager.start();
            //Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
            quartzManager.modifyJobTime("hi", "0/5 * * * * ?");
            //quartzManager.addJob("hi-2", TestJob1.class, 2, IntervalUnit.SECOND, 2, IntervalUnit.SECOND );

        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class TestJob implements Job {
        @Override
        public void execute(JobExecutionContext context)
                throws JobExecutionException {
            log.info(context.toString());
            log.info(++n + " hi");
        }
    }

    public static class TestJob1 implements Job {
        @Override
        public void execute(JobExecutionContext context)
                throws JobExecutionException {
            log.info(++n + " TestJob1");
        }

    }
}
