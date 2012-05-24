package com.centeractive.utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 5/23/12
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
class FileWriter {

        /**
         * Creates/ returns a file object
         *
         * @param rootLocation - Location to be written
         * @param packageName  - package, can be '.' separated
         * @param fileName     name of the file
         * @param extension    type of the file, java, cpp etc
         * @return the File that was created
         * @throws java.io.IOException
         * @throws Exception
         */
        public File createClassFile(File rootLocation,
                                    String packageName,
                                    String fileName,
                                    String extension) throws IOException,
                Exception {
            File returnFile = null;
            File root = rootLocation;

            if (packageName != null) {
                String directoryNames[] = packageName.split("\\.");
                File tempFile = null;
                int length = directoryNames.length;
                for (int i = 0; i < length; i++) {
                    tempFile = new File(root, directoryNames[i]);
                    root = tempFile;
                    if (!tempFile.exists()) {
                        tempFile.mkdir();
                    }
                }
            }

            if ((extension != null) && !fileName.endsWith(extension)) {
                fileName = fileName + extension;
            }

            returnFile = new File(root, fileName);

            if (!returnFile.exists()) {
                // returnFile.createNewFile();
            }
            return returnFile;
        }


    }
