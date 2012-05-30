package com.centeractive.ws.builder;

import com.centeractive.ws.builder.core.SoapBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 23/05/12
 * Time: 11:06 AM
 */
public class DefinitionSaveTest {

    public static File getServiceFolder(int serviceId) {
        URL definitionUrl = ServiceComplianceTest.getDefinitionUrl(serviceId);
        File definitionFile = new File(definitionUrl.getFile());
        File serviceFolder = new File(definitionUrl.getFile()).getParentFile();
        if (serviceFolder.exists() == false) {
            throw new RuntimeException("Cannot get service folder for service " + serviceId);
        }
        return serviceFolder;
    }

    public static File getGeneratedFolder(int serviceId) throws WSDLException, IOException {
        URL wsdlUrl = ServiceComplianceTest.getDefinitionUrl(serviceId);
        SoapBuilder builder = new SoapBuilder(wsdlUrl);
        File tempFolder = File.createTempFile("maven-temp", Long.toString(System.nanoTime()));
        if (!tempFolder.delete()) {
            throw new RuntimeException("cannot delete tmp file");
        }
        if (!tempFolder.mkdir()) {
            throw new RuntimeException("cannot create tmp folder");
        }
        String fileName = FilenameUtils.getBaseName(wsdlUrl.toString());
        builder.saveWsdl(fileName, tempFolder);
        tempFolder.deleteOnExit();
        return tempFolder;
    }

    public static List<String> getFileNames(File folder) {
        final boolean RECURSIVE = true;
        String[] extensions = new String[]{"wsdl", "xsd"};
        Collection<File> files = FileUtils.listFiles(folder, extensions, RECURSIVE);
        List<String> fileNames = new ArrayList<String>();
        for (File file : files) {
            fileNames.add(file.getName());
        }
        return fileNames;
    }

    public static File findFile(File folder, String name) {
        final boolean RECURSIVE = true;
        String[] extensions = new String[]{FilenameUtils.getExtension(name)};
        Collection<File> files = FileUtils.listFiles(folder, extensions, RECURSIVE);
        if(files.isEmpty() == false) {
            return files.iterator().next();
        }
        throw new RuntimeException("File not found " + name);
    }

    public static void testDefinitionSave(int serviceId) {
        try {
            File serviceFolder = getServiceFolder(serviceId);
            File generatedFolder = getGeneratedFolder(serviceId);
            List<String> serviceFileNames = getFileNames(serviceFolder);
            List<String> generatedFileNames = getFileNames(generatedFolder);
            Collections.sort(serviceFileNames);
            Collections.sort(generatedFileNames);
            assertEquals("serviceId " + serviceId, serviceFileNames.size(), generatedFileNames.size());
            for (int i = 0; i < serviceFileNames.size(); i++) {
                String srvFileName = serviceFileNames.get(i);
                String genFileName = null;
                for (int j = 0; j < generatedFileNames.size(); j++) {
                    String tmp = generatedFileNames.get(j);
                    if (tmp.endsWith(srvFileName) || tmp.equals(srvFileName)) {
                        genFileName = generatedFileNames.get(j);
                        break;
                    }
                }
                assertNotNull("serviceId " + serviceId + " " + srvFileName + " vs. " + genFileName, genFileName);
                // TODO - XMLs are not identical due to different import/includes paths
                // String srvContent = FileUtils.readFileToString(findFile(serviceFolder, srvFileName));
                // String genContent = FileUtils.readFileToString(findFile(generatedFolder, genFileName));
                // assertTrue("serviceId " + serviceId + " " + srvFileName + " vs. " + genFileName,
                //         XMLUnit.compareXML(srvContent, genContent).identical());
            }
        } catch (Exception e) {
            throw new RuntimeException("serviceId " + serviceId, e);
        }
    }

    @Test
    public void testDefinitionSaveService() {
        for (int i = 1; i <= 18; i++) {
            testDefinitionSave(i);
        }
    }

    @Test
    public void testDefinitionSaveService6() {
        testDefinitionSave(15);
    }


//    @Test
//    @Ignore
//    public void testWsdl11Write() throws Exception {
//        WSDLFactory factory = WSDLFactory.newInstance();
//        WSDLWriter writer = factory.newWSDLWriter();
//        File f = new File("/opt/wsdl/");
//        FileUtils.deleteDirectory(f);
//        try {
//            f.mkdirs();
//        } catch (Exception e) {
//        }
//        URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("services/test07", "TestService.wsdl");
//        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
//        WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
//        Definition def = wsdlReader.readWSDL(wsdlUrl.toString());
//
//        Wsdl11Writer writer11 = new Wsdl11Writer(f);
//        writer11.writeWSDL("TestService", def);
//    }
//
//
//    @Test
//    @Ignore
//    public void testWsdl11Write2() throws Exception {
//        WSDLFactory factory = WSDLFactory.newInstance();
//        WSDLWriter writer = factory.newWSDLWriter();
//        File f = new File("/opt/wsdl/");
//        FileUtils.deleteDirectory(f);
//        try {
//            f.mkdirs();
//        } catch (Exception e) {
//        }
//        // URL wsdlUrl = ResourceUtils.getResourceWithAbsolutePackagePath("services/test07", "TestService.wsdl");
//        URL wsdlUrl = new URL("http://localhost:8088/mockTestServiceSoap?WSDL");
//        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
//        WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
//        Definition def = wsdlReader.readWSDL(wsdlUrl.toString());
//
//        Wsdl11Writer writer11 = new Wsdl11Writer(f);
//        writer11.writeWSDL("TestService", def);
//    }

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



//    @Test
//    public void urlSchema() throws WSDLException, MalformedURLException {
//        // http://www.ibspan.waw.pl/~gawinec/example.wsdl
//        URL wsdlUrl = new URL("http://wsf.cdyne.com/WeatherWS/Weather.asmx?WSDL");
//        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
//        WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
//        Definition def = wsdlReader.readWSDL(wsdlUrl.toString());
//        File f = new File("/opt/wsdl");
//        if(f.mkdirs() == false) {
//            throw new RuntimeException("cannot create a folder");
//        }
//        SoapBuilder.saveWsdl("aaa", wsdlUrl, f);
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
