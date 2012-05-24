package com.centeractive.soap.test;

import com.centeractive.SoapBuilder;
import com.centeractive.utils.ResourceUtils;
import com.centeractive.utils.Wsdl11Writer;
import org.junit.Test;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 23/05/12
 * Time: 11:06 AM
 */
public class DefinitionSaveTest {


    @Test
    public void testWsdlRead() throws Exception {



        URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("services/test07", "TestService.wsdl");
        // SoapBuilder builder = new SoapBuilder(wsdlUrl);


        WSDLFactory wsdlFactory;

            wsdlFactory = WSDLFactory.newInstance();
            WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
            wsdlReader.setFeature("javax.wsdl.verbose", true);
            wsdlReader.setFeature("javax.wsdl.importDocuments", true);
            Definition def = wsdlReader.readWSDL(wsdlUrl.toString());

            File f = new File("/opt/wsdl/");
            try {
                f.mkdirs();
            } catch (Exception e) {
            }



            Wsdl11Writer writer = new Wsdl11Writer(f);
            writer.writeWSDL("TestService", def, new HashMap());



//        WSDLFactory factory = WSDLFactory.newInstance();
//        WSDLWriter writer = factory.newWSDLWriter();
//        File f = new File("/opt/wsdl/");
//        try {
//            f.mkdirs();
//        } catch (Exception e) {
//        }
//        File w = new File(f, "Test.wsdl");
//        Writer wa = new FileWriter(w);
//        writer.writeWSDL(def, wa);
    }

}
