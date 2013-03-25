package org.reficio.ws.test.spock

import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 *
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD, ElementType.TYPE])
@ExtensionAnnotation(ServerExtension)
@interface Server {
    String wsdl();
    String binding();
    String path() default "/service";
    int port() default 51515;
}
