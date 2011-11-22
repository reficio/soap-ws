package com.centeractive.utils;

import org.apache.commons.io.FilenameUtils;

import java.io.InputStream;
import java.net.URL;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: Tom Bujok (tomasz.bujok@centeractive.com)
 * Date: 11/10/11
 * Time: 10:04 AM
 */
public class ResourceUtils {

    public static URL getResourceWithAbsolutePackagePath(String absolutePackagePath, String resourceName) {
        return getResourceWithAbsolutePackagePath(ResourceUtils.class, absolutePackagePath, resourceName);
    }

    public static URL getResourceWithAbsolutePackagePath(Class<?> clazz, String absolutePackagePath, String resourceName) {
        checkNotNull(clazz, "clazz cannot be null");
        String resourcePath = getResourcePath(absolutePackagePath, resourceName);
        URL resource = null;
        // first attempt - outside/inside jar file
        resource = clazz.getClass().getResource(resourcePath);
        // second attempt - servlet container - inside application lib folder
        if(resource == null) {
            if(resourcePath.charAt(0) == '/') {
                String resourcePathWithoutLeadingSlash = resourcePath.substring(1);
                resource = Thread.currentThread().getContextClassLoader().getResource(resourcePathWithoutLeadingSlash);
            }
        }
        return checkNotNull(resource, String.format("Resource [%s] loading failed", resourcePath));
    }

    public static InputStream getResourceWithAbsolutePackagePathAsStream(String absolutePackagePath, String resourceName) {
        return getResourceWithAbsolutePackagePathAsStream(ResourceUtils.class, absolutePackagePath, resourceName);
    }

    public static InputStream getResourceWithAbsolutePackagePathAsStream(Class<?> clazz, String absolutePackagePath, String resourceName) {
        checkNotNull(clazz, "clazz cannot be null");
        String resourcePath = getResourcePath(absolutePackagePath, resourceName);
        InputStream resource = null;
        // first attempt - outside/inside jar file
        resource = clazz.getClass().getResourceAsStream(resourcePath);
        // second attempt - servlet container - inside application lib folder
        if(resource == null) {
            ClassLoader classLoader = clazz.getClass().getClassLoader();
            if(classLoader!=null)
                resource = classLoader.getResourceAsStream(resourcePath);
        }
        return checkNotNull(resource, String.format("Resource [%s] loading failed", resourcePath));
    }

    private static String getResourcePath(String absolutePackagePath, String resourceName) {
        checkNotNull(absolutePackagePath, "absolutePackagePath cannot be null");
        checkNotNull(resourceName, "resourceName cannot be null");
        absolutePackagePath = formatArgument(absolutePackagePath);
        resourceName = formatArgument(resourceName);
        return constructResourcePath(absolutePackagePath, resourceName);
    }

    private static String formatArgument(String argument) {
        String argumentWithoutWhiteSpaces = argument.replaceAll("\\s+", "");
        return argumentWithoutWhiteSpaces;
    }

    private static String constructResourcePath(String packagePath, String resourceName) {
        String resourcePath = String.format("/%s/%s", packagePath, resourceName);
        String resourcePathUnixSeparators = FilenameUtils.separatorsToUnix(resourcePath);
        String resourcePathNoLeadingSeparators = removeLeadingUnixSeparators(resourcePathUnixSeparators);
        String normalizedResourcePath = FilenameUtils.normalizeNoEndSeparator(resourcePathNoLeadingSeparators, true);
        return normalizedResourcePath;
    }

    private static String removeLeadingUnixSeparators(String argument) {
        return argument.replaceAll("/+", "/");
    }

}
