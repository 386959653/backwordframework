package com.pds.p2p.core.freemark;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkConfigBuilder {
    public static void main(String[] args) {
        try {
            Configuration configuration = new FreemarkConfigBuilder().addTemplateLoader("class://cn/epanel/io").build();
            Writer out = new OutputStreamWriter(System.out);
            /*
             * ${now}
${state}
${city}
${created}-2014年XX月XX日
${month}
${day}
			 */
            Map<String, String> rootMap = new ImmutableMap.Builder<String, String>()
                    .put("now", "xxxxxx")
                    .put("state", "xxxxx")
                    .put("city", "xxx")
                    .put("created", "xxx")
                    .put("month", "xxx")
                    .put("day", "xxx")
                    .build();

            List<String> list = ImmutableList.of("1", "2", "3", "4", "5", "6");

            configuration.getTemplate("t1.xml").process(rootMap, out);
            configuration.getTemplate("tab_tr.ftl").process(ImmutableMap.of("tcs", list), out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    private List<TemplateLoader> templateLoaders = Lists.newArrayList();

    public FreemarkConfigBuilder() {

    }

    public FreemarkConfigBuilder addTemplateLoader(String templatePath) throws IOException {
        templateLoaders.add(createTemplateLoader(templatePath));
        return this;
    }

    public Configuration build() {
        Configuration configuration = new Configuration();
        TemplateLoader[] loaders = templateLoaders.toArray(new TemplateLoader[] {});
        MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);
        configuration.setTemplateLoader(mtl);
        configuration.setObjectWrapper(new DefaultObjectWrapper());
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        return configuration;
    }

    protected TemplateLoader createTemplateLoader(String templatePath) throws IOException {
        if (templatePath.startsWith("class://")) {
            // substring(7) is intentional as we "reuse" the last slash
            return new ClassTemplateLoader(getClass(), templatePath.substring(7));
        } else if (templatePath.startsWith("file://")) {
            templatePath = templatePath.substring(7);
            return new FileTemplateLoader(new File(templatePath));
        } else {
            StringTemplateLoader loader = new StringTemplateLoader();
            String[] strs = StringUtils.splitByWholeSeparator(templatePath, "://", 2);
            loader.putTemplate(strs[0], strs[1]);
            return loader;
        }
    }

}
