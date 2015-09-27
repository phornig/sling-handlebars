package org.phms.sling.handlebars.context.simple;

import com.github.jknack.handlebars.Context;
import org.phms.sling.mvp.api.ContextGenerator;
import org.phms.sling.mvp.impl.simple.SimpleModelGenerator;

import javax.script.ScriptContext;
import java.util.Map;

public class HandlebarsSimpleContextGenerator implements ContextGenerator<Context> {

    private SimpleModelGenerator modelGenerator;

    public HandlebarsSimpleContextGenerator(SimpleModelGenerator modelGenerator) {
        this.modelGenerator = modelGenerator;
    }

    @Override
    public Context createContext(ScriptContext scriptContext) {
        Map<String, Object> model = modelGenerator.createModel(scriptContext);
        return Context.newBuilder(model).build();
    }


}
