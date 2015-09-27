package org.phms.sling.mvp.impl.presenter;

import javax.script.ScriptContext;
import java.util.Map;

public interface PresenterModelGenerator {
    Map<String, Object> createModel(ScriptContext scriptContext);
}
