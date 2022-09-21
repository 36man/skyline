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
package org.apache.skyline.commons.constant;

/**
 * @author lijian
 * @since time: 2022-09-13 15:39
 */
public final class CommonConstant {

    /**
     * URI template variables attribute name.
     */
    public static final String URI_TEMPLATE_VARIABLES_ATTRIBUTE = qualify("uriTemplateVariables");

    /**
     * Gateway predicate matched path attribute name.
     */
    public static final String GATEWAY_PREDICATE_MATCHED_PATH_ATTR = qualify("gatewayPredicateMatchedPathAttr");

    /**
     * Gateway predicate matched path route id attribute name.
     */
    public static final String GATEWAY_PREDICATE_MATCHED_PATH_API_IDENTITY = qualify(
            "gatewayPredicateMatchedPathApiIdentity");

    /**
     * Gateway predicate api identity.
     */
    public static final String GATEWAY_PREDICATE_API_IDENTITY = qualify("gatewayPredicateApiIdentity");

    public static final String GATEWAY_API_ATTR = qualify("gatewayApi");

    public static final String SYS_PREFIX = "skyline";

    public static final String PLUGIN_CONF_FILE_NAME = "skyline-plugin.def";


    private static String qualify(String attr) {
        return CommonConstant.SYS_PREFIX + "." + attr;
    }
}
