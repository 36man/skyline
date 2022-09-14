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
package org.apache.skyline.commons.exception;

/**
 * @author lijian
 * @since time: 2022-09-06 17:54
 */
public class SkylineException extends RuntimeException {
    private static final long serialVersionUID = 2054514133208644597L;

    public SkylineException() {
        super();
    }

    public SkylineException(String message) {
        super(message);
    }

    public SkylineException(String message, Throwable cause) {
        super(message, cause);
    }

    public SkylineException(Throwable cause) {
        super(cause);
    }

    protected SkylineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
