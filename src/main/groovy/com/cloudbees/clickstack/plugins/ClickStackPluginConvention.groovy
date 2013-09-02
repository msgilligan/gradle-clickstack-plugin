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
package com.cloudbees.clickstack.plugins

import org.gradle.api.Project
import org.gradle.api.file.CopySpec

/**
 * Inspired by Gradle Application Plugin.
 *
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
class ClickStackPluginConvention {
    /**
     * The name of the clickStack.
     */
    String clickStackName

    /**
     * The fully qualified name of the application's main class.
     */
    String mainClassName

    /**
     * Array of string arguments to pass to the JVM when running the application
     */
    Iterable<String> clickStackDefaultJvmArgs = []

    /**
     * <p>The specification of the contents of the distribution.</p>
     * <p>
     * Use this {@link org.gradle.api.file.CopySpec} to include extra files/resource in the application distribution.
     * <pre autoTested=''>
     * apply plugin: 'clickStack'
     *
     * clickStackDistribution.from("some/dir") {
     *   include "*.txt"
     * }
     * </pre>
     * <p>
     * Note that the clickStack plugin pre configures this spec to; include the contents of "{@code src/dist}",
     * copy the clickStack "{@code setup}" scripts into the "{@code .}" directory, and copy the built 'fat' jar
     * into the "{@code .}" directory.
     */
    CopySpec clickStackDistribution

    final Project project

    ClickStackPluginConvention(Project project) {
        this.project = project
        clickStackDistribution = project.copySpec {}
    }
}
