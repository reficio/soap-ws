package com.centeractive.utils;

import org.apache.ws.commons.schema.XmlSchema;

import javax.xml.transform.OutputKeys;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 5/23/12
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SchemaWriter {

    private File baseFolder = null;

    public SchemaWriter(File baseFolder) {
        this.baseFolder = baseFolder;
    }

    public void writeSchema(XmlSchema schema, String schemaFileName) {
        try {
            if (schema != null) {
                //create a output file
                File outputFile = new FileWriter().createClassFile(baseFolder,
                                                             null,
                                                             schemaFileName.substring(0,
                                                                                      schemaFileName.lastIndexOf(
                                                                                              ".")),
                                                             ".xsd");
                FileOutputStream fos = new FileOutputStream(outputFile);

                //set the options for the schemas
                schema.write(fos,getDefaultOptionMap());
                fos.flush();
                fos.close();
                // XMLPrettyPrinter.prettify(outputFile);
            }
        } catch (Exception e) {
            throw new RuntimeException("Schema writing failed!", e);
        }
    }

    private Map getDefaultOptionMap(){
        Map options = new HashMap();
        options.put(OutputKeys.OMIT_XML_DECLARATION,"no");
        options.put(OutputKeys.INDENT,"yes");

        return options;
    }


}
