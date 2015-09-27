package org.phms.sling.handlebars.context.presenter;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;
import org.phms.sling.mvp.api.ContextGenerator;
import org.phms.sling.mvp.api.ContextGeneratorFactory;
import org.phms.sling.mvp.impl.presenter.PresenterModelGenerator;

@Component
@Service(value = {ContextGeneratorFactory.class})
@Properties(value = {
        @Property(name = "service.description", value = "Handlebars: Presenter Context Generator Factory"),
        @Property(name = Constants.SERVICE_RANKING, intValue = -100)
}
)
public class HandlebarsPresenterContextGeneratorFactory implements ContextGeneratorFactory {

    @Reference
    protected PresenterModelGenerator modelGenerator;

    @Override
    public ContextGenerator getContextGenerator() {
        HandlebarsPresenterContextGenerator contextGenerator = new HandlebarsPresenterContextGenerator(modelGenerator);
        return contextGenerator;
    }

}
