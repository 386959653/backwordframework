package com.pds.p2p.system.quartz;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class TaskTest {
    private static Logger logger = LogManager.getLogger(TaskTest.class);

    public void run() {
        for (int i = 0; i < 1; i++) {
            logger.debug(i + " run......................................" + (new Date()));
        }

    }

    public void run1() {
        for (int i = 0; i < 1; i++) {
            logger.debug(i + " run1......................................" + (new Date()));
        }
    }

    public static void main(String[] args) {
        String c = null;
        Map<String, Charset> charsets = Charset.availableCharsets();
        for (Map.Entry<String, Charset> entry : charsets.entrySet()) {
            System.out.println(entry.getKey());
        }

    }
}
