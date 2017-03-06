package com.pds.p2p.core.poi.excel.xlsx;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;

public class Test {
    public static void main(String[] args) {
        ClassPathResource datHeader = new ClassPathResource("sheetfooter.txt", Test.class);
        try {
            datHeader.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
