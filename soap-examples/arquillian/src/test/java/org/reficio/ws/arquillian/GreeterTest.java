package org.reficio.ws.arquillian;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reficio.ws.test.junit.Server;
import org.reficio.ws.test.junit.SoapRule;

import javax.inject.Inject;

/**
 * @author: Tom Bujok (tom.bujok@gmail.com)
 * <p/>
 * Reficio™ - Reestablish your software!
 * www.reficio.org
 */
@RunWith(Arquillian.class)
public class GreeterTest {

    @Rule
    public SoapRule rule = new SoapRule();

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(Greeter.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    Greeter greeter;

    @Test
    public void should_create_greeting() {
        Assert.assertEquals("Hello, Earthling!", greeter.createGreeting("Earthling"));
        greeter.greet(System.out, "Earthling");
    }

    // @Server annotation spawns an instance of a SoapServer for the lifespan of the test / method.
    // The SOAP server provides a SOAP auto-responder for the specified binding -> messages are generated and sent automatically.
    // Generated messages are compliant with the WSDL and the schema (including enumerations, etc.)
    // The @Server annotation may be also used to annotate a method -> it spawns a SoapServer for the lifespan of the test method.
    // In order to enable the @Server annotation a junit @Rule has to be defined (JUnit requirement):
    //   - org.junit.ClassRule in order to enable the @Server on a per-class basis
    //   - org.junit.Rule in order to enable the @Server on a per-class basis
    @Test
    @Server(wsdl = "classpath:wsdl/currency-convertor.wsdl", binding = "CurrencyConvertorSoap")
    public void getConversionRateSoapTest() throws Exception {
        String rate = greeter.getConversionRate("USD", "EUR");
        Assert.assertNotNull(rate);
        Float rateFloat = Float.parseFloat(rate);
        Assert.assertTrue(rateFloat > 0.75);
    }

}