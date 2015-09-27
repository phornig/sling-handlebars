package org.phms.sling.mvp.xss;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XSSProtection {

    public enum Strategy {
        HTML_FILTER, HTML, HTML_ATTR, JS, XML, XML_ATTR, HREF, PLAIN_TEXT, NONE
    }

    Strategy strategy() default Strategy.HTML;
}
