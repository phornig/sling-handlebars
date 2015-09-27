package org.phms.sling.mvp.impl.presenter.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.xss.XSSAPI;
import org.phms.sling.mvp.xss.XSSProtection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Service
@Properties(value = {
        @Property(name = "service.description", value = "Handlebars: Bean to Map Serializer")}
)
public class BeanToMapSerializerImpl implements BeanToMapSerializer {

    //TODO: Inject service
    private XSSAPI xssApi;

    private ObjectMapper objectMapper;

    @Activate
    protected void activate() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        SerializerFactory serializerFactory = BeanSerializerFactory
                .instance
                .withSerializerModifier(new XSSSerializerModifier(xssApi));
        objectMapper.setSerializerFactory(serializerFactory);

    }


    private static class XSSSerializerWrapper extends JsonSerializer {

        @Override
        public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            String safeValue = getSafeValue((String) value);
            //continue with default impl
            provider.findTypedValueSerializer(String.class, true, beanProperty).serialize(safeValue, jgen, provider);
        }

        private String getSafeValue(String originalValue) {
            String result;
            if (StringUtils.isNotBlank(originalValue) && xssApi != null) {
                switch (xssProtection.strategy()) {
                    case NONE:
                        result = originalValue;
                        break;
                    case HTML_FILTER:
                        result = xssApi.filterHTML(originalValue);
                        break;
                    case HTML:
                        result = xssApi.encodeForHTML(originalValue);
                        break;
                    case HTML_ATTR:
                        result = xssApi.encodeForHTMLAttr(originalValue);
                        break;
                    case JS:
                        result = xssApi.encodeForJSString(originalValue);
                        break;
                    case XML:
                        result = xssApi.encodeForXML(originalValue);
                        break;
                    case XML_ATTR:
                        result = xssApi.encodeForXMLAttr(originalValue);
                        break;
                    case HREF:
                        result = xssApi.getValidHref(originalValue);
                        break;
                    case PLAIN_TEXT:
                        result = stripHTML(originalValue);
                        break;
                    default:
                        throw new UnsupportedOperationException(xssProtection.strategy() + " filtering is not yet supported");
                }
            } else {
                result = originalValue;
            }
            return result;
        }

        private BeanProperty beanProperty;
        private XSSAPI xssApi;
        private XSSProtection xssProtection;

        public XSSSerializerWrapper(BeanProperty beanProperty, XSSProtection xssProtection, XSSAPI xssApi) {
            this.beanProperty = beanProperty;
            this.xssApi = xssApi;
            this.xssProtection = xssProtection;
        }

        private String stripHTML(String text) {
            if (StringUtils.isNotBlank(text)) {
                return text.replaceAll("<[^>]*>", " ").replaceAll("[ ]{2,}", " ").trim();
            }
            return text;
        }
    }


    @Override
    public Map<String, Object> convertToMap(Object bean) {
        //only serialize objects that implement this marker interface, which excludes properties that are know to cause endless loops
        if(bean instanceof JacksonSerializable){
           return objectMapper.convertValue(bean, Map.class);
        }
        return new HashMap<>();
    }

    private static class XSSSerializerModifier extends BeanSerializerModifier {
        private XSSAPI xssApi;
        private static final Logger LOG = LoggerFactory.getLogger(XSSSerializerModifier.class);

        public XSSSerializerModifier(XSSAPI xssApi) {
            this.xssApi = xssApi;
        }

        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
            for (int i = 0; i < beanProperties.size(); i++) {
                try {
                    BeanPropertyWriter beanPropertyWriter = beanProperties.get(i);
                    XSSProtection xssProtection = beanPropertyWriter.getAnnotation(XSSProtection.class);
                    if (xssProtection != null) {
                        beanPropertyWriter.assignSerializer(new XSSSerializerWrapper(beanPropertyWriter, xssProtection, xssApi));

                    }

                } catch (Exception e) {
                    LOG.error("" + e, e);
                }
            }
            return beanProperties;
        }
    }
}
