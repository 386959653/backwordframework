package com.pds.p2p.core.fastjson;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.AbstractView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/***
 * 创建使用spring mvc view 建立 json
 *
 * @author wen
 */
public class SpringJsonView extends AbstractView {

    public static final String DEFAULT_CONTENT_TYPE = "application/json";

    public final static Charset UTF8 = Charset.forName("UTF-8");

    private Charset charset = UTF8;

    private SerializerFeature[] serializerFeature;

    private Set<String> renderedAttributes;

    private boolean disableCaching = true;

    private boolean updateContentLength = false;

    private boolean useModel = false;

    public SpringJsonView() {
        setContentType(DEFAULT_CONTENT_TYPE);
        setExposePathVariables(false);
    }

    public void setRenderedAttributes(Set<String> renderedAttributes) {
        this.renderedAttributes = renderedAttributes;
    }

    public void setSerializerFeature(SerializerFeature[] serializerFeature) {
        this.serializerFeature = serializerFeature;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public SerializerFeature[] getFeatures() {
        return serializerFeature;
    }

    public void setFeatures(SerializerFeature... features) {
        this.serializerFeature = features;
    }

    public boolean isUseModel() {
        return useModel;
    }

    public void setUseModel(boolean useModel) {
        this.useModel = useModel;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        Object value = filterModel(model);

        byte[] bytes;
        if (charset == UTF8) {
            if (serializerFeature != null) {
                bytes = JSON.toJSONBytes(value, serializerFeature);
            } else {
                bytes = JSON.toJSONBytes(value);
            }
        } else {
            String text;
            if (serializerFeature != null) {
                text = JSON.toJSONString(value, serializerFeature);
            } else {
                text = JSON.toJSONString(value);
            }
            bytes = text.getBytes(charset);
        }

        OutputStream stream = this.updateContentLength ? createTemporaryOutputStream() : response.getOutputStream();
        stream.write(bytes);

        if (this.updateContentLength) {
            writeToResponse(response, (ByteArrayOutputStream) stream);
        }
    }

    @Override
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        setResponseContentType(request, response);
        response.setCharacterEncoding(UTF8.name());
        if (this.disableCaching) {
            response.addHeader("Pragma", "no-cache");
            response.addHeader("Cache-Control", "no-cache, no-store, max-age=0");
            response.addDateHeader("Expires", 1L);
        }
    }

    /**
     * Disables caching of the generated JSON.
     * <p>
     * Default is {@code true}, which will prevent the client from caching the
     * generated JSON.
     */
    public void setDisableCaching(boolean disableCaching) {
        this.disableCaching = disableCaching;
    }

    /**
     * Whether to update the 'Content-Length' header of the response. When set
     * to {@code true}, the response is buffered in order to determine the
     * content length and set the 'Content-Length' header of the response.
     * <p>
     * The default setting is {@code false}.
     */
    public void setUpdateContentLength(boolean updateContentLength) {
        this.updateContentLength = updateContentLength;
    }

    /**
     * Filters out undesired attributes from the given model. The return value
     * can be either another {@link Map}, or a single value object.
     * <p>
     * <p>
     * Default implementation removes {@link BindingResult} instances and
     * entries not included in the {@link #setRenderedAttributes(Set)
     * renderedAttributes} property.
     *
     * @param model the model, as passed on to {@link #renderMergedOutputModel}
     *
     * @return the object to be rendered
     */
    protected Object filterModel(Map<String, Object> model) {
        Map<String, Object> result = new HashMap<String, Object>(model.size());
        Set<String> renderedAttributes =
                !CollectionUtils.isEmpty(this.renderedAttributes) ? this.renderedAttributes : model.keySet();
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            if (!(entry.getValue() instanceof BindingResult) && renderedAttributes.contains(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        if (MapUtils.isNotEmpty(result) && !this.isUseModel()) {
            return result.values().iterator().next();
        }
        return result;
    }

}
