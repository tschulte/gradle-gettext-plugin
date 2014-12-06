/*
 * Copyright 2014 the original author or authors.
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
package de.gliderpilot.gradle.gettext

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet

class GradleGettextPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.extensions.create('gettext', GradleGettextPluginExtension, this, project)
        project.apply plugin: 'java-base'
        project.sourceSets.all { SourceSet sourceSet ->
            def poDir = "src/${sourceSet.name}/i18n"
            def propertiesDir = "src/${sourceSet.name}/i18n"
            def updatePropertiesTask = project.tasks.create(sourceSet.getTaskName('update', 'Properties'), UpdatePropertiesTask) {
                from poDir
                into propertiesDir
            }
            def updatePoTask = project.tasks.create(sourceSet.getTaskName('update', 'Po'), UpdatePoTask) {
                finalizedBy updatePropertiesTask
                from poDir
                into poDir
            }
            def updatePotTask = project.tasks.create(sourceSet.getTaskName('update', 'Pot'), UpdatePotFromPropertiesTask) {
                finalizedBy updatePoTask
                from propertiesDir
                into poDir
            }
            project.tasks.create(sourceSet.getTaskName('import', 'ResourceBundles'), ImportResourceBundlesTask) {
                finalizedBy updatePotTask
                from propertiesDir
                into poDir
            }
        }
    }
}
