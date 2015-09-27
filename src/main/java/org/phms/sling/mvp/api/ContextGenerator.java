package org.phms.sling.mvp.api;


import javax.script.ScriptContext;

public interface ContextGenerator<T> {

    T createContext(ScriptContext scriptContext);
}
