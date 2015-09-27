package org.phms.sling.handlebars.context.simple;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.phms.sling.mvp.api.ContextGenerator;
import org.phms.sling.mvp.api.ContextGeneratorFactory;
import org.phms.sling.mvp.impl.simple.SimpleModelGenerator;

@Component
@Service(value = {ContextGeneratorFactory.class})
@Properties(value = {
        @Property(name = "service.description", value = "Handlebars: Simple Context Generator Factory")}
)
public class HandlebarsSimpleContextGeneratorFactory implements ContextGeneratorFactory {
    @Override
    public ContextGenerator getContextGenerator() {
        HandlebarsSimpleContextGenerator result = new HandlebarsSimpleContextGenerator(new SimpleModelGenerator());
        return result;
    }
}
