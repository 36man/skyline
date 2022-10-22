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
package org.apache.skyline.commons.utils;

import org.apache.skyline.commons.support.HttpStatusHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.apache.skyline.commons.constant.CommonConstant.URI_TEMPLATE_VARIABLES_ATTRIBUTE;

/**
 * @author lijian
 * @since time: 2022-09-16 16:31
 */
public class WebUtils {

    private static final Logger LOG = LoggerFactory.getLogger(WebUtils.class);

    public static boolean setResponseStatus(ServerWebExchange exchange, HttpStatus httpStatus) {
        boolean response = exchange.getResponse().setStatusCode(httpStatus);
        if (!response && LOG.isWarnEnabled()) {
            LOG.warn("Unable to set status code to " + httpStatus + ". Response already committed.");
        }
        return response;
    }

    public static boolean setResponseStatus(ServerWebExchange exchange, HttpStatusHolder statusHolder) {
        if (exchange.getResponse().isCommitted()) {
            return false;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting response status to " + statusHolder);
        }
        if (statusHolder.getHttpStatus() != null) {
            return setResponseStatus(exchange, statusHolder.getHttpStatus());
        }
        if (statusHolder.getStatus() != null && exchange.getResponse() instanceof AbstractServerHttpResponse) { // non-standard
            ((AbstractServerHttpResponse) exchange.getResponse()).setRawStatusCode(statusHolder.getStatus());
            return true;
        }
        return false;
    }

    public static HttpStatus parse(String statusString) {
        HttpStatus httpStatus;

        try {
            int status = Integer.parseInt(statusString);
            httpStatus = HttpStatus.resolve(status);
        }
        catch (NumberFormatException e) {
            // try the enum string
            httpStatus = HttpStatus.valueOf(statusString.toUpperCase());
        }
        return httpStatus;
    }

    public static String expand(ServerWebExchange exchange, String template) {
        Assert.notNull(exchange, "exchange may not be null");
        Assert.notNull(template, "template may not be null");

        if (template.indexOf('{') == -1) { // short circuit
            return template;
        }

        Map<String, String> variables = getUriTemplateVariables(exchange);
        return UriComponentsBuilder.fromPath(template).build().expand(variables).getPath();
    }

    public static Map<String, String> getUriTemplateVariables(ServerWebExchange exchange) {
        return (Map<String, String>) exchange.getAttributes().getOrDefault(URI_TEMPLATE_VARIABLES_ATTRIBUTE, new HashMap<>());
    }

    public static void putUriTemplateVariables(ServerWebExchange exchange, Map<String, String> uriVariables) {
        if (exchange.getAttributes().containsKey(URI_TEMPLATE_VARIABLES_ATTRIBUTE)) {
            Object v = exchange.getAttributes().get(URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            Map<String, Object> existingVariables = CastUtils.cast(exchange.getAttributes().get(URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            Map<String, Object> newVariables = new HashMap<>();
            if (existingVariables != null) {
                newVariables.putAll(existingVariables);
            }
            newVariables.putAll(uriVariables);
            exchange.getAttributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, newVariables);
        } else {
            exchange.getAttributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriVariables);
        }
    }
}
