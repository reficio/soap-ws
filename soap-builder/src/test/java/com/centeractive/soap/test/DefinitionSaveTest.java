package com.centeractive.soap.test;

import com.centeractive.utils.ResourceUtils;
import com.centeractive.utils.Wsdl11Writer;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;
import java.io.File;
import java.net.URL;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 23/05/12
 * Time: 11:06 AM
 */
public class DefinitionSaveTest {

//    @Test
//    public void testPureWsdl4j() throws WSDLException, IOException {
//        URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("services/test07", "TestService.wsdl");
//        WSDLReader reader = new WSDLReaderImpl();
//        reader.setFeature("javax.wsdl.verbose", true);
//        reader.setFeature("javax.wsdl.importDocuments", true);
//        Definition def = reader.readWSDL(wsdlUrl.toString());
//
//
//        WSDLFactory factory = WSDLFactory.newInstance();
//        WSDLWriter writer = factory.newWSDLWriter();
////        writer.setFeature("javax.wsdl.verbose", true);
//        // writer.setFeature("javax.wsdl.importDocuments", true);
//        File f = new File("/opt/wsdl/");
//        FileUtils.deleteDirectory(f);
//        try {
//            f.mkdirs();
//        } catch (Exception e) {
//        }
//
//
//        File w = new File(f, "Test.wsdl");
//        Writer wa = new FileWriter(w);
//        writer.writeWSDL(def, wa);
//        for (Import i : def.getImports()) {
//
//        }
//    }


//    @Test
//    public void testWsdlReadManual() throws IOException {
//
//        File targetFolder = new File("/opt/wsdl/");
//        FileUtils.deleteDirectory(targetFolder);
//        try {
//            targetFolder.mkdirs();
//        } catch (Exception e) {
//        }
//        URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("services/test07", "TestService.wsdl");
//        File wsdlFile = new File(wsdlUrl.getFile());
//        WsdlImporter importer = new WsdlImporter();
//        importer.importWsdl(wsdlFile, targetFolder);
//    }


    @Test
    @Ignore
    public void testWsdl11Write() throws Exception {
        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLWriter writer = factory.newWSDLWriter();
        File f = new File("/opt/wsdl/");
        FileUtils.deleteDirectory(f);
        try {
            f.mkdirs();
        } catch (Exception e) {
        }
        URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("services/test07", "TestService.wsdl");
        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
        WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
        Definition def = wsdlReader.readWSDL(wsdlUrl.toString());

        Wsdl11Writer writer11 = new Wsdl11Writer(f);
        writer11.writeWSDL("TestService", def);
    }


    @Test
    @Ignore
    public void testWsdl11Write2() throws Exception {
        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLWriter writer = factory.newWSDLWriter();
        File f = new File("/opt/wsdl/");
        FileUtils.deleteDirectory(f);
        try {
            f.mkdirs();
        } catch (Exception e) {
        }
        // URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("services/test07", "TestService.wsdl");
        URL wsdlUrl = new URL("http://localhost:8088/mockTestServiceSoap?WSDL");
        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
        WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
        Definition def = wsdlReader.readWSDL(wsdlUrl.toString());

        Wsdl11Writer writer11 = new Wsdl11Writer(f);
        writer11.writeWSDL("TestService", def);
    }

//
//    @Test
//    @Ignore
//    public void urlSchema() throws MalformedURLException, WSDLException {
//        // http://www.ibspan.waw.pl/~gawinec/example.wsdl
//        URL wsdlUrl = new URL("http://www.ibspan.waw.pl/~gawinec/example.wsdl");
//        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
//        WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
//        Definition def = wsdlReader.readWSDL(wsdlUrl.toString());
//    }


//    @Test
//    public void testWsdlRead() throws Exception {
//
//
//        URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("services/test07", "TestService.wsdl");
//        // SoapBuilder builder = new SoapBuilder(wsdlUrl);
//
//
//        WSDLFactory wsdlFactory;
//
//        wsdlFactory = WSDLFactory.newInstance();
//        WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
////        wsdlReader.setFeature("javax.wsdl.verbose", true);
////        wsdlReader.setFeature("javax.wsdl.importDocuments", true);
//        Definition def = wsdlReader.readWSDL(wsdlUrl.toString());
//
//        File f = new File("/opt/wsdl/");
//        FileUtils.deleteDirectory(f);
//        try {
//            f.mkdirs();
//        } catch (Exception e) {
//        }
//
//
//    }

}
