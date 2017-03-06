package com.pds.p2p.core.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/***
 * @author wangfei
 */
public class FreeMarkers {

    private static Configuration configuration = new Configuration();

    static {
        try {
            configuration.setSettings(
                    FreeMarkers.class.getClassLoader().getResourceAsStream("com/pds/utils/freemarker.properties"));
            configuration.setCacheStorage(new freemarker.cache.MruCacheStorage(20, 100));
            configuration.setAutoFlush(true);
            buildConfiguration(null);
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param templateString
     * @param model
     *
     * @return String
     *
     * @throws Exception
     * @function 解析字符串
     * @date 2014-12-18 下午3:54:53
     * @author v_zoupengfei
     */
    public static String renderString(String templateString, Object model) throws Exception {
        StringWriter result = null;
        try {
            result = new StringWriter();
            Template t = new Template("name", new StringReader(templateString), configuration);

            t.process(model, result);
            configuration.clearTemplateCache();
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(FreeMarkers.class.getName() + ":替换字符串错误" + e);
        } finally {
            if (result != null) {
                result.close();
            }
        }
    }

    /**
     * @param tmpPath
     * @param model
     *
     * @return String
     *
     * @throws Exception
     * @function 接卸模板文件
     * @date 2014-12-18 下午3:55:08
     * @author v_zoupengfei
     */
    public static String renderTemplate(String tmpPath, Object model) throws Exception {
        StringWriter result = null;
        try {
            Template template = configuration.getTemplate(tmpPath);
            result = new StringWriter();
            template.process(model, result);
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(FreeMarkers.class.getName() + ":生成模板错误" + e);
        } finally {
            if (result != null) {
                result.close();
            }
        }
    }

    /**
     * @param tmpPath
     * @param model
     * @param filePath
     *
     * @return void
     *
     * @throws Exception
     * @function 解析模板文件并保存到某个文件下
     * @date 2014-12-18 下午3:55:17
     * @author v_zoupengfei
     */
    public static void renderTemplateAndSaveFile(String tmpPath, Object model, String filePath) throws Exception {
        try {
            FileUtils.writeFile(renderTemplate(tmpPath, model), filePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(FreeMarkers.class.getName() + ":生成模板错误" + e);
        }
    }

    /**
     * @return Configuration
     *
     * @throws IOException
     * @function 获取配置
     * @date 2014-12-18 下午3:55:42
     * @author v_zoupengfei
     */
    public static Configuration getConfiguration() throws IOException {
        return configuration;
    }

    /**
     * @param directory
     *
     * @return Configuration
     *
     * @throws IOException
     * @function 构建模板文件夹
     * @date 2014-12-18 下午3:55:50
     * @author v_zoupengfei
     */
    public static Configuration buildConfiguration(String directory) throws IOException {

        if (StringUtils.isNotEmpty(directory)) {
            configuration.setDirectoryForTemplateLoading(new File(directory));
        } else {
            Resource path = new DefaultResourceLoader().getResource("");
            configuration.setDirectoryForTemplateLoading(path.getFile());
        }
        return configuration;
    }

    /**
     * @param args
     *
     * @return void
     *
     * @throws Exception
     * @function 测试
     * @date 2014-12-18 下午3:56:08
     * @author v_zoupengfei
     */

    public static void main(String[] args) throws Exception {
        Map model = new HashMap();
        model.put("id", "1");
        model.put("dateFormat", "yyyy-MM-dd");
        // FileUtils.readFileToString("freemarker.properties");
        //System.out.println("aaa"+FileUtils.readFileToString("/com/pds/utils/freemarker.properties"));
        //  org.apache.commons.io.FileUtils.readFileToString(new File(uri))
        String str = FreeMarkers.class.getClassLoader().getResource("com/pds/utils/test.properties").getPath();
        File file = new File(str);
        Properties properties = new PropertiesLoader("com/pds/utils/test.properties").getProperties();
        // org.apache.commons.io.FileUtils.get
        String template = properties.getProperty("testcode");
        System.out.println(template);
        System.out.println(FreeMarkers.renderString(template, model));
        // URI uri=URI.create("/com/pds/utils/freemarker.properties");
        //  FileSystems.getDefault().get
        //System.out.println(str);
        //File file =new File(pathname)
        
    /*     // renderTemplate string
        String result = FreeMarkers.renderString("${className}Service sldsfldsajflds", model);
		System.out.println(result);
		// renderTemplate file
		String result2=FreeMarkers.renderTemplate("template/demo.ftl",model);
		System.out.println(result2);
		// renderTemplate file and save it
		FreeMarkers.renderTemplateAndSaveFile("template/demo.ftl",model,"d:/freemarker/demo.txt");*/

    }

}