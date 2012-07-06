/**
 * Copyright (c) 2012 centeractive ag. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.centeractive.ws.builder;

import com.centeractive.ws.builder.core.SoapParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import javax.wsdl.WSDLException;
import java.io.File;
import java.io.IOException;
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

    public static File createTempFolder(String name) throws IOException {
        File tempFolder = File.createTempFile(name, Long.toString(System.nanoTime()));
        if (!tempFolder.delete()) {
            throw new RuntimeException("cannot delete tmp file");
        }
        if (!tempFolder.mkdir()) {
            throw new RuntimeException("cannot create tmp folder");
        }
        return tempFolder;
    }

    public static File getGeneratedFolder(int serviceId) throws WSDLException, IOException {
        URL wsdlUrl = ServiceComplianceTest.getDefinitionUrl(serviceId);
        File tempFolder = File.createTempFile("maven-temp", Long.toString(System.nanoTime()));
        if (!tempFolder.delete()) {
            throw new RuntimeException("cannot delete tmp file");
        }
        if (!tempFolder.mkdir()) {
            throw new RuntimeException("cannot create tmp folder");
        }
        String fileName = FilenameUtils.getBaseName(wsdlUrl.toString());
        SoapParser.saveWsdl(wsdlUrl, fileName, tempFolder);
        // builder.saveWsdl(fileName, tempFolder);
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
        if (files.isEmpty() == false) {
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
        for (int serviceId = 1; serviceId <= 18; serviceId++) {
            testDefinitionSave(serviceId);
        }
    }

}
