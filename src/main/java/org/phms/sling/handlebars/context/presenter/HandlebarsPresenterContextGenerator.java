
package org.phms.sling.handlebars.context.presenter;

import com.github.jknack.handlebars.Context;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.phms.sling.mvp.api.ContextGenerator;
import org.phms.sling.mvp.impl.presenter.PresenterModelGenerator;

import javax.script.Bindings;
import javax.script.ScriptContext;
import java.util.HashMap;
import java.util.Map;


public class HandlebarsPresenterContextGenerator implements ContextGenerator<Context> {

    public static final String REQUEST_KEY = "_request";
    public static final String RESPONSE_KEY = "_response";
    public static final String RESOLVER_KEY = "_resolver";

    private PresenterModelGenerator modelGenerator;

    public HandlebarsPresenterContextGenerator(PresenterModelGenerator modelGenerator) {
        this.modelGenerator = modelGenerator;
    }

    private Map<String, Object> getBaseContext(SlingScriptHelper scriptHelper) {
        Map<String, Object> baseContextMap = new HashMap<String, Object>();

        baseContextMap.put(REQUEST_KEY, scriptHelper.getRequest());
        baseContextMap.put(RESPONSE_KEY, scriptHelper.getResponse());
        baseContextMap.put(RESOLVER_KEY, scriptHelper.getRequest().getResourceResolver());
        return baseContextMap;
    }

    private SlingScriptHelper getSlingScriptHelper(ScriptContext context) {
        final Bindings props = context.getBindings(ScriptContext.ENGINE_SCOPE);
        return (SlingScriptHelper) props.get(SlingBindings.SLING);
    }


    @Override
    public Context createContext(ScriptContext scriptContext) {
        Map<String, Object> presentationModel = modelGenerator.createModel(scriptContext);

        if (presentationModel != null) {
            Map<String, Object> baseContextMap = getBaseContext(getSlingScriptHelper(scriptContext));
            presentationModel.putAll(baseContextMap);
            return Context.newBuilder(presentationModel).build();
        } else {
            return null;
        }
    }


}
