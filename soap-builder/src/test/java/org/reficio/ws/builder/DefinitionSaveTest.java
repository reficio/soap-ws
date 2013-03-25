/**
 * Copyright (c) 2012-2013 Reficio (TM) - Reestablish your software!. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.reficio.ws.builder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.reficio.ws.builder.core.Wsdl;

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
 * User: Tom Bujok (tom.bujok@gmail.com)
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
        File rootWsdl = new File(tempFolder, fileName);
        Wsdl.saveWsdl(wsdlUrl, rootWsdl);
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
