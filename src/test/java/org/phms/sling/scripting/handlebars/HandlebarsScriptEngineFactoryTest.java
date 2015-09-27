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
package org.phms.sling.scripting.handlebars;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.phms.sling.handlebars.HandlebarsScriptEngineFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlebarsScriptEngineFactoryTest {

    public class Person {

        private Name firstName;

        private Name lastName;

        public Person(String firstName, String lastName) {
            this.firstName = new Name(firstName);
            this.lastName = new Name(lastName);
        }

        public Name getFirstName() {
            return firstName;
        }

        public Name getLastName() {
            return lastName;
        }
    }

    public class Name {

        private String _name;

        public Name(String name) {
            this._name = name;
        }

        public String getName() {
            return _name;
        }

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlebarsScriptEngineFactoryTest.class);

    private HandlebarsScriptEngineFactory scriptEngineFactory;

    @Before
    public void before() {
        scriptEngineFactory = new HandlebarsScriptEngineFactory();
    }

    @Test
    public void testProperties() {
        Assert.assertEquals("HandlebarsScriptEngineFactory",
            scriptEngineFactory.getEngineName());
        Assert.assertEquals("0", scriptEngineFactory.getEngineVersion());
        Assert.assertArrayEquals(new String[] { "handlebars" },
            scriptEngineFactory.getExtensions().toArray());
        Assert.assertEquals("handlebars", scriptEngineFactory.getLanguageName());
        Assert.assertEquals("1.0", scriptEngineFactory.getLanguageVersion());
        Assert.assertArrayEquals(new String[] { "text/x-handlebars" },
            scriptEngineFactory.getMimeTypes().toArray());
    }

    @Test
    public void testScriptEngine() throws ScriptException {
        ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
        LOGGER.info("Script engine is {} ", scriptEngine);
        StringReader reader = new StringReader(" Something really Simple");
        StringWriter writer = new StringWriter();
        SimpleScriptContext scriptContext = new SimpleScriptContext();
        Bindings bindings = scriptEngine.createBindings();
        scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        scriptContext.setWriter(writer);
        LOGGER.info("Calling with {} ", scriptContext);
        Object o = scriptEngine.eval(reader, scriptContext);
        Assert.assertNull(o);
        Assert.assertEquals(" Something really Simple", writer.toString());
    }

    /**
     * Test a simple binding.
     * @throws ScriptException
     */
    @Test
    public void testTemplateSimpleBinding() throws ScriptException {
        ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
        LOGGER.info("Script engine is {} ", scriptEngine);
        StringReader reader = new StringReader(
            " Something really Simple with {{ replace_me }}");
        StringWriter writer = new StringWriter();
        SimpleScriptContext scriptContext = new SimpleScriptContext();
        Bindings bindings = scriptEngine.createBindings();
        scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        scriptContext.setWriter(writer);
        bindings.put("replace_me", "Hello World!");
        LOGGER.info("Calling with {} ", scriptContext);
        Object o = scriptEngine.eval(reader, scriptContext);
        Assert.assertNull(o);
        Assert.assertEquals(" Something really Simple with Hello World!",
            writer.toString());
    }

    /**
     * Test a simple map binding.
     * @throws ScriptException
     */
    @Test
    public void testTemplateMapBinding() throws ScriptException {
        ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
        LOGGER.info("Script engine is {} ", scriptEngine);
        StringReader reader = new StringReader(
            " Something really Simple with {{ replace_me.name }}");
        StringWriter writer = new StringWriter();
        SimpleScriptContext scriptContext = new SimpleScriptContext();
        Bindings bindings = scriptEngine.createBindings();
        scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        scriptContext.setWriter(writer);
        Map<String, Object> replace_me = new HashMap<String, Object>();
        replace_me.put("name", "ieb");
        bindings.put("replace_me", replace_me);
        LOGGER.info("Calling with {} ", scriptContext);
        Object o = scriptEngine.eval(reader, scriptContext);
        Assert.assertNull(o);
        Assert.assertEquals(" Something really Simple with ieb",
            writer.toString());
    }

    /**
     * Tests that its possible to access bean getters by name using dotted
     * annotation.
     * 
     * @throws ScriptException
     */
    @Test
    public void testTemplateMethodBinding() throws ScriptException {
        ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
        LOGGER.info("Script engine is {} ", scriptEngine);
        StringReader reader = new StringReader(
            " Something really Simple with {{ replace_me.name }}");
        StringWriter writer = new StringWriter();
        SimpleScriptContext scriptContext = new SimpleScriptContext();
        Bindings bindings = scriptEngine.createBindings();
        scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        scriptContext.setWriter(writer);
        bindings.put("replace_me", new Name("ian"));
        LOGGER.info("Calling with {} ", scriptContext);
        Object o = scriptEngine.eval(reader, scriptContext);
        Assert.assertNull(o);
        Assert.assertEquals(" Something really Simple with ian",
            writer.toString());
    }

    /**
     * Tests value binding through multiple beans.
     * @throws ScriptException
     */
    @Test
    public void testTemplateDeepMethodBinding() throws ScriptException {
        ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
        LOGGER.info("Script engine is {} ", scriptEngine);
        StringReader reader = new StringReader(
            " Something really Simple with {{ replace_me.firstName.name }} {{ replace_me.lastName.name }}");
        StringWriter writer = new StringWriter();
        SimpleScriptContext scriptContext = new SimpleScriptContext();
        Bindings bindings = scriptEngine.createBindings();
        scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        scriptContext.setWriter(writer);
        bindings.put("replace_me", new Person("ian", "boston"));
        LOGGER.info("Calling with {} ", scriptContext);
        Object o = scriptEngine.eval(reader, scriptContext);
        Assert.assertNull(o);
        Assert.assertEquals(" Something really Simple with ian boston",
            writer.toString());
    }

    /**
     * Test for safe defaults to XXS strings.
     * @throws ScriptException
     */
    @Test
    public void testTemplateDeepMethodBindingXSS() throws ScriptException {
        ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
        LOGGER.info("Script engine is {} ", scriptEngine);
        StringReader reader = new StringReader(
            " Something really Simple with {{ replace_me.firstName.name }} {{ replace_me.lastName.name }}");
        StringWriter writer = new StringWriter();
        SimpleScriptContext scriptContext = new SimpleScriptContext();
        Bindings bindings = scriptEngine.createBindings();
        scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        scriptContext.setWriter(writer);
        bindings.put("replace_me", new Person(
            "ian<script type=\"text/javascript",
            "\">alert('GotYa!');</script>boston"));
        LOGGER.info("Calling with {} ", scriptContext);
        Object o = scriptEngine.eval(reader, scriptContext);
        Assert.assertNull(o);
        Assert.assertEquals(
            " Something really Simple with ian&lt;script type=&quot;text/javascript &quot;&gt;alert(&#x27;GotYa!&#x27;);&lt;/script&gt;boston",
            writer.toString());
    }

    /**
     * Tests ability to escape to raw output when required.
     * @throws ScriptException
     */
    @Test
    public void testTemplateDeepMethodBindingRaw() throws ScriptException {
        ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
        LOGGER.info("Script engine is {} ", scriptEngine);
        StringReader reader = new StringReader(
            " Something really Simple with {{{ replace_me.firstName.name }}} {{{ replace_me.lastName.name }}}");
        StringWriter writer = new StringWriter();
        SimpleScriptContext scriptContext = new SimpleScriptContext();
        Bindings bindings = scriptEngine.createBindings();
        scriptContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        scriptContext.setWriter(writer);
        bindings.put("replace_me", new Person(
            "ian<script type=\"text/javascript",
            "\">alert('GotYa!');</script>boston"));
        LOGGER.info("Calling with {} ", scriptContext);
        Object o = scriptEngine.eval(reader, scriptContext);
        Assert.assertNull(o);
        Assert.assertEquals(
            " Something really Simple with ian<script type=\"text/javascript \">alert('GotYa!');</script>boston",
            writer.toString());
    }

}
