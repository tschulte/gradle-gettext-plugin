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

import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

class UpdatePropertiesTask extends AbstractGettextTask {

    @Input
    FileCollection propertiesTemplateFiles

    def poFiles(poFiles) {
        from(poFiles) {
            include '**/*.po'
        }
    }

    def propertiesTemplateFiles(propertiesTemplateFiles) {
        this.propertiesTemplateFiles = project.fileTree(propertiesTemplateFiles) {
            include '**/*.properties'
            exclude '**/*_*.properties'
        }
    }

    def propertiesDir(propertiesDir) {
        into(propertiesDir) {
            include '**/*_*.properties'
        }
    }

    @Override
    protected void update(List<Map<String, String>> changedInputFiles) {
        changedInputFiles.each { poFile ->
            def propertiesTemplateFile = relativePath(propertiesTemplateFiles.findAll(sameBaseNameFilter(poFile)).first())
            exec "po2prop --personality=mozilla --removeuntranslated -t ${propertiesTemplateFile} ${poFile} ${relativePath(into)}"
        }
    }
}
