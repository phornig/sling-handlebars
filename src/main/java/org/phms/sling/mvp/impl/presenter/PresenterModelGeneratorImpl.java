package org.phms.sling.mvp.impl.presenter;

import aQute.bnd.annotation.component.Deactivate;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.ComponentContext;
import org.phms.sling.mvp.common.utils.ScriptContextAdapter;
import org.phms.sling.mvp.impl.presenter.serializer.BeanToMapSerializer;

import javax.script.ScriptContext;
import java.util.HashMap;
import java.util.Map;

@Component
@Service
@Properties(value = {
        @Property(name = "service.description", value = "Handlebars: Presenter Model Generator")}
)
public class PresenterModelGeneratorImpl implements PresenterModelGenerator {


    private PresenterBundleListener presenterBundleListener;

    @Reference
    private BeanToMapSerializer beanToMapSerializer;

    @Override
    public Map<String, Object> createModel(ScriptContext scriptContext) {
        Map<String, Object> model;
        Resource resource = new ScriptContextAdapter(scriptContext).getResource();
        Class<?> presenterType = getPresenterType(resource);
        if (presenterType != null) {
            Object presenter = resource.adaptTo(presenterType);
            model = beanToMapSerializer.convertToMap(presenter);
        } else {
            model = new HashMap<>();
        }
        return model;
    }

    private Class<?> getPresenterType(Resource resource) {
        String resourceType = resource.getResourceType();
        return presenterBundleListener.getPresenters().get(resourceType);
    }

    @Activate
    private void activate(ComponentContext componentContext) {
        presenterBundleListener = new PresenterBundleListener(componentContext.getBundleContext());

    }

    @Deactivate
    private void deactivate() {
        presenterBundleListener.unregisterAll();

    }
}
