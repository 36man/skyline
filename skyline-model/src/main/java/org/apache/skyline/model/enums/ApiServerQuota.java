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
package org.apache.skyline.model.enums;

import lombok.Getter;

/**
 * @author lijian
 * @since time: 2022-09-05 10:58
 */
public enum ApiServerQuota {
    Q_2C_2G(2, 2), Q_2C_4G(2, 4), Q_4C_4G(4, 4),
    Q_4C_8G(4, 8), Q_8C_8G(8, 8), Q_8C_16G(8, 16),
    Q_16C_16G(16, 16), Q_16C_32G(16, 32), Q_32C_32G(32, 32),
    Q_32C_64G(32, 64), Q_64C_64_G(64, 64), Q_32C_128G(32, 128),
    Q_64C_128G(64, 128), Q_64G_256G(64, 256);

    @Getter
    private final int cpuCore;
    @Getter
    private final int memory;

    ApiServerQuota(int cpuCore, int memory) {
        this.cpuCore = cpuCore;
        this.memory = memory;
    }

}
