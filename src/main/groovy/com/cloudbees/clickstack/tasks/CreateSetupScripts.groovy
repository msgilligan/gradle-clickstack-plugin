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

import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.util.GUtil

/**
 * <p>A {@link org.gradle.api.Task} for creating OS dependent setup scripts.</p>
 * <p>Inspired by Gradle Application Plugin.</p>
 *
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
public class CreateSetupScripts extends ConventionTask {

    /**
     * The directory to write the scripts into.
     */
    File outputDir

    /**
     * The application's main class.
     */
    @Input
    String mainClassName

    /**
     * The application's default JVM options.
     */
    @Input
    @Optional
    Iterable<String> defaultJvmOpts = []

    /**
     * The application's name.
     */
    @Input
    String applicationName

    String scriptName = "setup"

    String optsEnvironmentVar

    String exitEnvironmentVar

    /**
     * The class path for the application.
     */
    @InputFiles
    FileCollection classpath

    /**
     * Returns the name of the application's OPTS environment variable.
     */
    @Input
    String getOptsEnvironmentVar() {
        if (optsEnvironmentVar) {
            return optsEnvironmentVar
        }
        if (!getApplicationName()) {
            return null
        }
        return "${GUtil.toConstant(getApplicationName())}_OPTS"
    }

    @Input
    String getExitEnvironmentVar() {
        if (exitEnvironmentVar) {
            return exitEnvironmentVar
        }
        if (!getApplicationName()) {
            return null
        }
        return "${GUtil.toConstant(getApplicationName())}_EXIT_CONSOLE"
    }

    @OutputFile
    File getUnixScript() {
        // return new File(getOutputDir(), getApplicationName())
        return new File(getOutputDir(), getScriptName())

    }

    @OutputFile
    File getWindowsScript() {
        //return new File(getOutputDir(), "${getApplicationName()}.bat")
        return new File(getOutputDir(), "${getScriptName()}.bat")
    }

    @TaskAction
    void generate() {
        def generator = new SetupScriptGenerator()
        generator.applicationName = getApplicationName()
        generator.mainClassName = getMainClassName()
        generator.defaultJvmOpts = getDefaultJvmOpts()
        generator.optsEnvironmentVar = getOptsEnvironmentVar()
        generator.exitEnvironmentVar = getExitEnvironmentVar()
        generator.classpath = getClasspath().collect { "lib/${it.name}" }
        // generator.scriptRelPath = "bin/${getUnixScript().name}"
        generator.scriptRelPath = "${getUnixScript().name}"
        generator.generateUnixScript(getUnixScript())
        generator.generateWindowsScript(getWindowsScript())
    }
}
