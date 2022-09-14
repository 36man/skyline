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
package org.apache.skyline.engine.predicate;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.skyline.engine.support.NameUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

/**
 * @author lijian
 * @since time: 2022-09-06 17:08
 */
@Validated
public class PredicateDefinition {

    @NotNull
    private String name;

    private Map<String, Object> args = new LinkedHashMap<>();

    public PredicateDefinition() {
    }

    public PredicateDefinition(String text) {
        int eqIdx = text.indexOf('=');
        if (eqIdx <= 0) {
            throw new ValidationException(
                    "Unable to parse PredicateDefinition text '" + text + "'" + ", must be of the form name=value");
        }
        setName(text.substring(0, eqIdx));

        String[] args = tokenizeToStringArray(text.substring(eqIdx + 1), ",");

        for (int i = 0; i < args.length; i++) {
            this.args.put(NameUtils.generateName(i), args[i]);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }

    public void addArg(String key, String value) {
        this.args.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PredicateDefinition that = (PredicateDefinition) o;
        return Objects.equals(name, that.name) && Objects.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, args);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
