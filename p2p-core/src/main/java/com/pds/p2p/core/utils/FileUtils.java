package com.pds.p2p.core.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 */
public class FileUtils {
    protected static Logger logger = LogManager.getLogger(FileUtils.class);

    /**
     * 清空目录
     *
     * @param content
     * @param filePath
     */
    public static void clearDir(String filePath) {
        try {
            File deletefile = new File(filePath);
            if (deletefile.exists()) {
                if (deletefile.isDirectory()) {
                    File[] files = deletefile.listFiles();
                    for (File file : files) {
                        clearDir(file.getAbsolutePath());
                        if (file.isDirectory()) {
                            file.delete();
                        }
                    }

                } else {
                    deletefile.delete();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将内容写入文件
     *
     * @param content
     * @param filePath
     */
    public static void writeFile(String content, String filePath) {
        logger.info("写文件[" + filePath + "]");
        if (createFile(filePath)) {
            FileWriterWithEncoding fileWriter = null;
            BufferedWriter bufferedWriter = null;
            try {
                fileWriter = new FileWriterWithEncoding(filePath, "utf-8", false);
                bufferedWriter = new BufferedWriter(fileWriter);

                bufferedWriter.write(content);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileWriter != null) {
                        fileWriter.close();
                    }
                    if (bufferedWriter != null) {
                        bufferedWriter.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            }

        } else {
            // logger.info("生成失败，文件已存在！");
        }

    }

    /**
     * 将内容读取出来
     *
     * @param content
     * @param filePath
     */
    public static String readFileToString(String filePath) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));

            String tmp = "";
            while ((tmp = br.readLine()) != null) {
                sb.append(tmp).append("\n");
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 创建单个文件
     *
     * @param descFileName 文件名，包含路径
     *
     * @return 如果创建成功，则返回true，否则返回false
     */
    public static boolean createFile(String descFileName) {
        File file = new File(descFileName);
        if (file.exists()) {
            file.delete();
            // logger.debug("文件 " + descFileName + " 已存在!");
            return true;
        }
        if (descFileName.endsWith("/")) {
            logger.debug(descFileName + " 为目录，不能创建目录!");
            return false;
        }
        if (!file.getParentFile().exists()) {
            // 如果文件所在的目录不存在，则创建目录
            if (!file.getParentFile().mkdirs()) {
                logger.debug("创建文件所在的目录失败!");
                return false;
            }
        }

        // 创建文件
        try {
            if (file.createNewFile()) {
                // logger.debug(descFileName + " 文件创建成功!");
                return true;
            } else {
                logger.debug(descFileName + " 文件创建失败!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(descFileName + " 文件创建失败!");
            return false;
        }

    }

    public static void copyFile(String srcPath, String desPath) {
        File src = new File(srcPath);
        File des = new File(desPath);
        int bytesum = 0;
        int byteread = 0;
        if (src.exists()) {
            // 文件存在时
            InputStream is = null;
            FileOutputStream os = null;
            try {
                is = new FileInputStream(src);
                // 读入原文件
                os = new FileOutputStream(des);
                byte[] buffer = new byte[1024];
                while ((byteread = is.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    System.out.println("读取进度：" + bytesum / src.length() + "%");
                    os.write(buffer, 0, byteread);
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

    public static void main(String[] args) {
        System.out.println(readFileToString("D:/testFM/a.gen"));
        // copyFile("D:\\wmsgenworkspace\\project\\genproject\\p1002\\project.zip",
        // "D:\\wmsgenworkspace\\project\\genproject\\p1001\\project.zip");
    }
}
