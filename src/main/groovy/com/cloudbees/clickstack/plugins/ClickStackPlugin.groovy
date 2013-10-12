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

import com.cloudbees.clickstack.component.ClickStack
import com.cloudbees.clickstack.tasks.AddClickStackDependencies
import com.cloudbees.clickstack.tasks.CreateSetupScripts
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.bundling.Zip

/**
 * Inspired by Gradle Application Plugin.
 *
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
class ClickStackPlugin implements Plugin<Project> {

    static final String CLICKSTACK_PLUGIN_NAME = "clickStack"
    static final String CLICKSTACK_GROUP = CLICKSTACK_PLUGIN_NAME

    static final String TASK_RUN_NAME = "run"
    static final String TASK_SETUP_SCRIPTS_NAME = "setupScripts"
    static final String TASK_INSTALL_NAME = "installClickStack"

    static final String TASK_ADD_CLICKSTACK_DEPENDENCIES_NAME = "addClickStackDependencies"

    static final String TASK_DIST_CLICKSTACK_NAME = "distClickStack"

    private Project project
    private ClickStackPluginConvention pluginConvention

    void apply(final Project project) {
        this.project = project
        project.plugins.apply(org.gradle.api.plugins.JavaPlugin)

        addPluginConvention()
        addRunTask()
        addCreateScriptsTask()

        addAddClickStackDependenciesTask()

        configureDistSpec(pluginConvention.clickStackDistribution)

        addInstallClickStackTask()
        Zip clickstack = addDistClickStackTask()

        ArchivePublishArtifact clickstackArtifact = new ArchivePublishArtifact(clickstack);
        project.getExtensions().getByType(DefaultArtifactPublicationSet.class).addCandidate(clickstackArtifact);
        project.getComponents().add(new ClickStack(clickstackArtifact));

    }

    private void addPluginConvention() {
        pluginConvention = new ClickStackPluginConvention(project)
        pluginConvention.clickStackName = project.name
        pluginConvention.clickstackId = project.clickstackId
        project.convention.plugins.clickStack = pluginConvention
        if(project.hasProperty('clickstackInstallDir') && project.getProperty('clickstackInstallDir') != null) {
            pluginConvention.clickstackInstallDir = project.getProperty('clickstackInstallDir') +  "/" + pluginConvention.clickstackId
        } else {
            pluginConvention.clickstackInstallDir = project.buildDir.path + '/install/' + pluginConvention.clickstackId
        }
    }

    private void addRunTask() {
        def run = project.tasks.create(TASK_RUN_NAME, JavaExec)
        run.description = "Runs this clickStack"
        run.group = CLICKSTACK_GROUP
        run.classpath = project.sourceSets.main.runtimeClasspath
        run.conventionMapping.main = { pluginConvention.mainClassName }
        run.conventionMapping.jvmArgs = { pluginConvention.clickStackDefaultJvmArgs }
    }

    // @Todo: refactor this task configuration to extend a copy task and use replace tokens
    private void addCreateScriptsTask() {
        def startScripts = project.tasks.create(TASK_SETUP_SCRIPTS_NAME, CreateSetupScripts)
        startScripts.description = "Creates OS specific scripts to run the clickStack."
        startScripts.classpath = project.tasks[org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME].outputs.files + project.configurations.runtime
        startScripts.conventionMapping.mainClassName = { pluginConvention.mainClassName }
        startScripts.conventionMapping.applicationName = { pluginConvention.clickStackName }
        startScripts.conventionMapping.outputDir = { new File(project.buildDir, 'scripts') }
        startScripts.conventionMapping.defaultJvmOpts = { pluginConvention.clickStackDefaultJvmArgs }
    }

    private void addAddClickStackDependenciesTask() {
        def addClickStackDependencies = project.tasks.create(TASK_ADD_CLICKSTACK_DEPENDENCIES_NAME, AddClickStackDependencies)
        addClickStackDependencies.description = "Add dependencies to ClickStack"
        addClickStackDependencies.group = CLICKSTACK_GROUP
        addClickStackDependencies.project = project
        addClickStackDependencies.clickStackDistribution = pluginConvention.clickStackDistribution
    }

    private void addInstallClickStackTask() {
        def installTask = project.tasks.create(TASK_INSTALL_NAME, Sync)
        installTask.description = "Installs the ClickStack."
        installTask.group = CLICKSTACK_GROUP
        installTask.with pluginConvention.clickStackDistribution

        installTask.into { project.file(pluginConvention.clickstackInstallDir) }
        installTask.dependsOn TASK_ADD_CLICKSTACK_DEPENDENCIES_NAME, org.gradle.api.plugins.JavaPlugin.TEST_TASK_NAME
        installTask.doFirst {
            if (destinationDir.directory) {
                if (
                        !new File(destinationDir, 'lib').directory ||
                                !new File(destinationDir, 'dist').directory ||
                                !new File(destinationDir, 'deps').directory
                ) {
                    throw new GradleException("The specified installation directory '${destinationDir}' is neither empty nor does it contain an installation for '${pluginConvention.clickStackName}'.\n" +
                            "If you really want to install to this directory, delete it and run the install task again.\n" +
                            "Alternatively, choose a different installation directory."
                    )
                }
            }
        }
        installTask.doLast {
            project.ant.chmod(file: "${destinationDir.absolutePath}/setup", perm: 'ugo+x')
            logger.quiet("ClickStack installed in ${destinationDir}")
        }
    }

    private Zip addDistClickStackTask() {
        def archiveTask = project.tasks.create(TASK_DIST_CLICKSTACK_NAME, Zip)
        archiveTask.description = "Bundles the project as a JVM application with libs and OS specific scripts."
        archiveTask.group = CLICKSTACK_GROUP
        archiveTask.conventionMapping.baseName = { pluginConvention.clickStackName }
        archiveTask.dependsOn TASK_ADD_CLICKSTACK_DEPENDENCIES_NAME, org.gradle.api.plugins.JavaPlugin.TEST_TASK_NAME

        def baseDir = ""
        archiveTask.into(baseDir) {
            with(pluginConvention.clickStackDistribution)
        }
        archiveTask
        archiveTask.doLast {
            logger.quiet("ClickStack archive created in ${destinationDir}")
        }
    }

    private CopySpec configureDistSpec(CopySpec distSpec) {
        def jar = project.tasks[org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME]
        def startScripts = project.tasks[TASK_SETUP_SCRIPTS_NAME]

        distSpec.with {
            // src/dist files are intended to be directly copied in the application root directory
            into("dist") {
                from(project.file("src/dist"))
            }
            // src/clickstack-resources files are intended to be packaged in the clickstack and used during setup phase
            into("resources") {
                from(project.file("src/clickstack-resources"))
            }

            into("lib") {
                from(jar)
                from(project.configurations.runtime)
            }
            into("") {
                from(startScripts)
                fileMode = 0755
            }
        }

        distSpec
    }
}