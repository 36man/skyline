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
package org.apache.skyline.engine.test;

import org.apache.commons.io.IOUtils;
import org.apache.skyline.commons.exception.SkylineException;
import org.apache.skyline.engine.support.SkylinePackagePath;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * @author lijian
 * @since time: 2022-09-07 17:34
 */
public class TestMain {

    public static void main(String[] args) throws Exception {
        File pluginDir = new File(SkylinePackagePath.getPath(), "plugin");
        if (!pluginDir.exists()) {
            boolean mkdirs = pluginDir.mkdirs();
            if (!mkdirs) {
                throw new SkylineException("make plugin dir error");
            }
        }

        try (InputStream input = new URL("http://localhost:9898/testPlugin.jar").openStream();
             OutputStream output = new FileOutputStream(new File(pluginDir, "testPlugin.jar"))) {
            IOUtils.copy(input, output);
        }
    }
}
