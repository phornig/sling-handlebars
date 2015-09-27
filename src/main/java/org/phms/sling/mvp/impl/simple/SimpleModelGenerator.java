package org.phms.sling.mvp.impl.simple;

import org.phms.sling.mvp.common.utils.ScriptContextAdapter;

import javax.script.Bindings;
import javax.script.ScriptContext;
import java.util.HashMap;
import java.util.Map;


public class SimpleModelGenerator{

    public Map<String, Object> createModel(ScriptContext scriptContext) {
        Map<String, Object> model = new HashMap<>();
        Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
        for (Object entryObj : bindings.entrySet()) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entryObj;
            model.put((String) entry.getKey(), entry.getValue());
        }

        model.put("properties", new ScriptContextAdapter(scriptContext).getResource().getValueMap());
        return model;
    }
}
