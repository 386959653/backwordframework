package com.pds.p2p.core.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.Closure;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pds.p2p.core.jdbc.lock.JdbcSemaphore;

public class JdbcSemaphoreTest {
    static int n = 0;

    public static void main(String[] args) throws InterruptedException {
        final ApplicationContext ac = new ClassPathXmlApplicationContext("classpath*:/spring-context*.xml");
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 1000; ++i) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    final JdbcSemaphore jdbcSemaphore = ac.getBean(JdbcSemaphore.class);
                    jdbcSemaphore.executeWithLock("www", new Closure<Number>() {
                        @Override
                        public void execute(Number arg) {
                            System.out.println(Thread.currentThread().getName() + "::" + (++n));
                        }
                    }, 300);
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(20000, TimeUnit.DAYS);

    }
}
