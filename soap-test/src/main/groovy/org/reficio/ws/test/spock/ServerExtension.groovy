package org.reficio.ws.test.spock

import org.reficio.ws.test.ServerProcessor
import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 *
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
class ServerExtension extends AbstractAnnotationDrivenExtension<Server> {

    def featureAnnotations = [:]

    void visitSpecAnnotation(Server annotation, SpecInfo spec) {
        spec.addListener(new AbstractRunListener() {

            private ServerProcessor processor

            @Override
            public void beforeSpec(SpecInfo specInfo) {
                processor = new ServerProcessor(annotation, spec.getClass())
                processor.initServer()
            }

            @Override
            public void afterSpec(SpecInfo specInfo) {
                processor.stopServer()
            }

        });
    }

    void visitFeatureAnnotation(Server annotation, FeatureInfo feature) {
        if (featureAnnotations.isEmpty()) {
            feature.getParent().addListener(new AbstractRunListener() {
                @Override
                public void beforeFeature(FeatureInfo featureInfo) {
                    ServerProcessor processor = featureAnnotations[featureInfo]
                    if (processor) {
                        processor.initServer()
                    }
                }

                @Override
                public void afterFeature(FeatureInfo featureInfo) {
                    ServerProcessor processor = featureAnnotations[featureInfo]
                    if (processor) {
                        processor.stopServer()
                    }
                }
            });
        }
        featureAnnotations[feature] = new ServerProcessor(annotation, feature.getClass())
    }

}
