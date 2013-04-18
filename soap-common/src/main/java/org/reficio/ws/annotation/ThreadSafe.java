package org.reficio.ws.annotation;

import java.lang.annotation.*;

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 * <p/>
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
@Documented
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.CLASS)
public @interface ThreadSafe {
}
