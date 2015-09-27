package org.phms.sling.mvp.common.utils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;

import javax.script.Bindings;
import javax.script.ScriptContext;
import java.io.Writer;

public class ScriptContextAdapter {
    private SlingHttpServletRequest request;
    private Resource resource;
    private SlingScriptHelper scriptHelper;
    private Writer writer;

    public ScriptContextAdapter(ScriptContext context) {
        Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
        scriptHelper = (SlingScriptHelper) bindings.get(SlingBindings.SLING);
        request = scriptHelper.getRequest();
        resource = request.getResource();
        writer = context.getWriter();
    }

    public SlingHttpServletRequest getRequest() {
        return request;
    }

    public Resource getResource() {
        return resource;
    }

    public SlingScriptHelper getScriptHelper() {
        return scriptHelper;
    }

    public Writer getWriter() {
        return writer;
    }
}
