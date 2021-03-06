/*
 * Copyright 2010-2013, the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cloudbees.clickstack.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Inspired by Gradle Application Plugin.
 *
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
public class AddClickStackDependencies extends DefaultTask {

    @Input
    CopySpec clickStackDistribution

    @Input
    Project project

    @TaskAction
    void addDependencies() {
        project.configurations.each { cfg ->
            if (cfg.ext.has("clickStackFolder")) {
                clickStackDistribution.from(cfg.collect { it }) {
                    into "$cfg.ext.clickStackFolder"
                }
            } else {
                logger.debug "Ignore $cfg.name"
            }
        }
    }

}