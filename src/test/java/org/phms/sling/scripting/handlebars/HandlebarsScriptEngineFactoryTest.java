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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.phms.sling.handlebars.HandlebarsScriptEngineFactory;

public class HandlebarsScriptEngineFactoryTest {

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
        Assert.assertArrayEquals(new String[]{"hbs"},
                scriptEngineFactory.getExtensions().toArray());
        Assert.assertEquals("handlebars", scriptEngineFactory.getLanguageName());
        Assert.assertEquals("1.0", scriptEngineFactory.getLanguageVersion());
        Assert.assertArrayEquals(new String[]{"text/x-handlebars"},
                scriptEngineFactory.getMimeTypes().toArray());
    }


}
