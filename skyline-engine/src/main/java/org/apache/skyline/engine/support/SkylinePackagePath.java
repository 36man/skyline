/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.skyline.engine.support;

import org.apache.skyline.commons.exception.PackageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author lijian
 * @since time: 2022-09-14 09:12
 */
public class SkylinePackagePath {

    private static final Logger LOG = LoggerFactory.getLogger(SkylinePackagePath.class);

    private static File PACKAGE_PATH;

    public static File getPath() throws PackageNotFoundException {
        if (PACKAGE_PATH == null) {
            PACKAGE_PATH = findPath();
        }
        return PACKAGE_PATH;
    }

    public static boolean isPathFound() {
        return PACKAGE_PATH != null;
    }

    private static File findPath() throws PackageNotFoundException {
        String classResourcePath = SkylinePackagePath.class.getName().replaceAll("\\.", "/") + ".class";

        URL resource = ClassLoader.getSystemClassLoader().getResource(classResourcePath);
        if (resource != null) {
            String urlString = resource.toString();

            LOG.debug("The beacon class location is {}.", urlString);

            int insidePathIndex = urlString.indexOf('!');
            boolean isInJar = insidePathIndex > -1;

            if (isInJar) {
                urlString = urlString.substring(urlString.indexOf("file:"), insidePathIndex);
                File agentJarFile = null;
                try {
                    agentJarFile = new File(new URL(urlString).toURI());
                } catch (MalformedURLException | URISyntaxException e) {
                    LOG.error("Can not locate agent jar file by url:" + urlString, e);
                }
                if (agentJarFile.exists()) {
                    return agentJarFile.getParentFile();
                }
            } else {
                int prefixLength = "file:".length();
                String classLocation = urlString.substring(
                        prefixLength, urlString.length() - classResourcePath.length());
                return new File(classLocation);
            }
        }

        LOG.error("Can not locate agent jar file.");
        throw new PackageNotFoundException("Can not locate agent jar file.");
    }
}
