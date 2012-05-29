package com.centeractive.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 5/29/12
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class WsdlImporter {

    // <wsdl:import namespace="http://schemas.eviware.com/TestService/v1/" location="wsdls/TestBinding.wsdl"/>
    private static final String IMPORT_TAG_REGEX = "<[\\p{ASCII}&&[^<>]]*?import[\\p{ASCII}&&[^<>]]*?location=\"[\\p{ASCII}&&[^<>]]*?\"";
    private static final String LOCATION_REGEX = "location=\"[\\p{ASCII}&&[^<>]]*?\"";
    private static final String IMPORT_LOCATION_ATTRIBUTE = "location";

    private static final String INCLUDE_TAG_REGEX = "<[\\p{ASCII}&&[^<>]]*?include[\\p{ASCII}&&[^<>]]*?schemaLocation=\"[\\p{ASCII}&&[^<>]]*?\"";
    private static final String SCHEMA_LOCATION_REGEX = "schemaLocation=\"[\\p{ASCII}&&[^<>]]*?\"";
    private static final String INCLUDE_LOCATION_ATTRIBUTE = "schemaLocation";

    public void importWsdl(File wsdlFile, File targetFolder) {
        processWsdlFile(wsdlFile.getParentFile(), wsdlFile, targetFolder);
    }

    private void importXsd(File rootFolder, File wsdlFile, String wsdlContent, File targetFolder) {
        Pattern pattern = Pattern.compile(INCLUDE_TAG_REGEX, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(wsdlContent);
        while (matcher.find()) {
            File includedXsdFile = getDependencyXsdFile(wsdlFile, matcher.group());
            processXsdFile(rootFolder, includedXsdFile, targetFolder);
        }
    }

    private void processXsdFile(File rootFolder, File xsdFile, File targetFolder) {
        try {
            if (xsdFile == null || xsdFile.exists() == false) {
                throw new RuntimeException("File does not exist " + xsdFile.getPath());
            }
            String wsdlContent = FileUtils.readFileToString(xsdFile);
            Pattern pattern = Pattern.compile(INCLUDE_TAG_REGEX, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(wsdlContent);
            while (matcher.find()) {
                File includedXsdFile = getDependencyXsdFile(xsdFile, matcher.group());
                processWsdlFile(rootFolder, includedXsdFile, targetFolder);
            }
            String pathDifference = StringUtils.remove(xsdFile.getParentFile().getPath(), rootFolder.getPath());
            if (pathDifference.equals(xsdFile.getParentFile().getPath())) {
                throw new RuntimeException("Absolute paths not supported in wsdl imports and schema includes");
            }
            File diffTargetFolder = new File(targetFolder, pathDifference);
            diffTargetFolder.mkdirs();
            FileUtils.copyFileToDirectory(xsdFile, diffTargetFolder);
        } catch (IOException e) {
            throw new RuntimeException("Error while importing wsdl", e);
        }
    }

    private void processWsdlFile(File rootFolder, File wsdlFile, File targetFolder) {
        try {
            if (wsdlFile == null || wsdlFile.exists() == false) {
                throw new RuntimeException("File does not exist " + wsdlFile.getPath());
            }
            String wsdlContent = FileUtils.readFileToString(wsdlFile);
            Pattern pattern = Pattern.compile(IMPORT_TAG_REGEX, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(wsdlContent);
            while (matcher.find()) {
                File includedWsdlFile = getDependencyWsdlFile(wsdlFile, matcher.group());
                processWsdlFile(rootFolder, includedWsdlFile, targetFolder);
            }
            String pathDifference = StringUtils.remove(wsdlFile.getParentFile().getPath(), rootFolder.getPath());
            if (pathDifference.equals(wsdlFile.getParentFile().getPath())) {
                throw new RuntimeException("Absolute paths not supported in wsdl imports and schema includes");
            }
            File diffTargetFolder = new File(targetFolder, pathDifference);
            diffTargetFolder.mkdirs();
            FileUtils.copyFileToDirectory(wsdlFile, diffTargetFolder);
            // importXsd(rootFolder, wsdlFile, wsdlContent, targetFolder);
        } catch (IOException e) {
            throw new RuntimeException("Error while importing wsdl", e);
        }
    }

    private File getDependencyWsdlFile(File wsdlFile, String importString) {
        Pattern pattern = Pattern.compile(LOCATION_REGEX, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(importString);
        while (matcher.find()) {
            String location = matcher.group().trim();
            location = StringUtils.remove(location, IMPORT_LOCATION_ATTRIBUTE + "=\"");
            location = StringUtils.remove(location, "\"");
            File includedWsdlFile = new File(wsdlFile.getParentFile(), location);
            return includedWsdlFile;
        }
        return null;
    }

    private File getDependencyXsdFile(File xsdFile, String importString) {
        Pattern pattern = Pattern.compile(SCHEMA_LOCATION_REGEX, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(importString);
        while (matcher.find()) {
            String location = matcher.group().trim();
            location = StringUtils.remove(location, INCLUDE_LOCATION_ATTRIBUTE + "=\"");
            location = StringUtils.remove(location, "\"");
            File includedXsdFile = new File(xsdFile.getParentFile(), location);
            return includedXsdFile;
        }
        return null;
    }


}
