package org.phms.sling.handlebars.helper.include;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Handlebars.SafeString;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import java.io.IOException;


public class IncludeHelper implements Helper<Object> {

    static final String PATH = "path";

    static final String RESOURCE_TYPE = "resourceType";

    static final String SELECTOR = "template";
    static final String ADD_SELECTORS = "addSelectors";
    static final String REPLACE_SELECTORS = "replaceSelectors";
    static final String REPLACE_SUFFIX = "replaceSuffix";

    private static final Logger LOG = LoggerFactory.getLogger(IncludeHelper.class);

    public static final String NAME = "include";

    private RequestDispatcherOptions getRequestDispatcherOptions(OptionsAdapter optionsAdapter) {
        RequestDispatcherOptions opts = new RequestDispatcherOptions();

        if (optionsAdapter.getSelector() != null) {
            opts.setReplaceSelectors(optionsAdapter.getSelector());
        }

        if (optionsAdapter.getReplaceSelectors() != null) {
            opts.setReplaceSelectors(optionsAdapter.getReplaceSelectors());
        }

        if (optionsAdapter.getAddSelectors() != null) {
            opts.setAddSelectors(optionsAdapter.getAddSelectors());
        }

        if (StringUtils.isNotBlank(optionsAdapter.getReplaceSelectors())) {
            opts.setReplaceSuffix(optionsAdapter.getReplaceSelectors());
        }

        return opts;
    }

    private SafeString include(final Options options) throws IOException, ServletException {
        OptionsAdapter optionsAdapter = new OptionsAdapter(options);

        return new Handlebars.SafeString(includeResource(optionsAdapter));
    }


    private String includeResource(OptionsAdapter optionsAdapter) throws IOException, ServletException {
        RequestDispatcherOptions opts = getRequestDispatcherOptions(optionsAdapter);

        if (StringUtils.isNotBlank(optionsAdapter.getForceResourceType())) {
            opts.setForceResourceType(optionsAdapter.getForceResourceType());
        }

        return returnInclude(optionsAdapter, opts);
    }


    private String returnInclude(OptionsAdapter optionsAdapter, RequestDispatcherOptions opts) throws IOException, ServletException {
        RequestDispatcher dispatcher = optionsAdapter.getRequest().getRequestDispatcher(optionsAdapter.getResource(), opts);

        if (dispatcher != null) {

            return dispatcherInclude(optionsAdapter.getRequest(), optionsAdapter.getResponse(), dispatcher);
        } else {
            LOG.error("could not include " + optionsAdapter.getResource().getPath());

            return "";
        }
    }

    private String dispatcherInclude(SlingHttpServletRequest request, SlingHttpServletResponse response, RequestDispatcher dispatcher) throws IOException, ServletException {

        try (StringResponseWrapper wrapper = new StringResponseWrapper(response)) {
            dispatcher.include(request, wrapper);
            return wrapper.getStringOutput();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public CharSequence apply(final Object context, final Options options) throws IOException {

        try {
            return include(options);
        } catch (final IOException e) {
            LOG.error("error including template", e);
        } catch (final ServletException e) {
            LOG.error("error including template", e);
        }

        return new Handlebars.SafeString("");
    }
}
