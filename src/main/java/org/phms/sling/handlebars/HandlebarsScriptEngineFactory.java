/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.phms.sling.handlebars;

import com.github.jknack.handlebars.Handlebars;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.RankedServices;
import org.apache.sling.scripting.api.AbstractScriptEngineFactory;
import org.osgi.framework.Constants;
import org.phms.sling.handlebars.helper.include.IncludeHelper;
import org.phms.sling.mvp.api.ContextGeneratorFactory;

import javax.script.ScriptEngine;
import java.util.Collection;
import java.util.Map;

@Component(
        label = "Apache Sling Scripting Handlebars",
        description = "Scripting engine for Handlebars templates",
        immediate = true,
        metatype = true
)
@Service
@Properties({
        @Property(name = Constants.SERVICE_VENDOR, value = "The Apache Software Foundation"),
        @Property(name = Constants.SERVICE_DESCRIPTION, value = "Scripting engine for Handlebars templates"),
        @Property(name = Constants.SERVICE_RANKING, intValue = 0, propertyPrivate = false)
})
public class HandlebarsScriptEngineFactory extends AbstractScriptEngineFactory {

    @Reference(name = "contextGeneratorFactory",
            cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            referenceInterface = ContextGeneratorFactory.class,
            policy = ReferencePolicy.DYNAMIC)

    private RankedServices<ContextGeneratorFactory> contextGeneratorFactories = new RankedServices<>();

    public void bindContextGeneratorFactory(ContextGeneratorFactory contextGeneratorFactory, Map<String, Object> props) {
        contextGeneratorFactories.bind(contextGeneratorFactory, props);
    }

    public void unbindContextGeneratorFactory(ContextGeneratorFactory contextGeneratorFactory, Map<String, Object> props) {
        contextGeneratorFactories.unbind(contextGeneratorFactory, props);
    }

    public final static String HANDLEBARS_SCRIPT_EXTENSION = "hbs";

    public final static String HANDLEBARS_MIME_TYPE = "text/x-handlebars";

    public final static String HANDLEBARS_SHORT_NAME = "handlebars";

    public HandlebarsScriptEngineFactory() {
        setExtensions(HANDLEBARS_SCRIPT_EXTENSION);
        setMimeTypes(HANDLEBARS_MIME_TYPE);
        setNames(HANDLEBARS_SHORT_NAME);
    }

    public String getLanguageName() {
        return "handlebars";
    }

    public String getLanguageVersion() {
        return "1.0";
    }

    public Collection<ContextGeneratorFactory> getContextGeneratorFactories() {
        return contextGeneratorFactories.get();
    }

    public ScriptEngine getScriptEngine() {
        Handlebars handlebars = createTemplateEngine();
        return new HandlebarsScriptEngine(this, handlebars);
    }

    private Handlebars createTemplateEngine() {
        Handlebars handlebars = new Handlebars();
        handlebars.registerHelper(IncludeHelper.NAME, new IncludeHelper());
        return handlebars;
    }

}
