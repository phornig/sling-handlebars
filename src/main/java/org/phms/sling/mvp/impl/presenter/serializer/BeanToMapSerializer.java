package org.phms.sling.mvp.impl.presenter.serializer;

import java.util.Map;

public interface BeanToMapSerializer {
    Map<String, Object> convertToMap(Object bean);
}
