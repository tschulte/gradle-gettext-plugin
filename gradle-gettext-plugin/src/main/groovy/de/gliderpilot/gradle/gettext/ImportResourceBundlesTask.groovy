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

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

class ImportResourceBundlesTask extends AbstractGettextTask {

    @Input
    FileCollection propertiesFiles

    def propertiesTemplateFiles(propertiesTemplateFiles) {
        from(propertiesTemplateFiles) {
            include '**/*.properties'
            exclude '**/*_*.properties'
        }
    }

    def propertiesFiles(propertiesFiles) {
        this.propertiesFiles = project.fileTree(propertiesFiles) {
            include '**/*_*.properties'
        }
    }

    def poDir(poDir) {
        into(poDir) {
            include '**/*.po'
        }
    }

    @Override
    protected void update(List<Map<String, String>> changedInputFiles) {
        changedInputFiles.each { file ->
            String poFileName = "${file.baseName}_${file.locale}.po"
            if (!new File(into, poFileName).exists()) {
                def propertiesTemplateFile = from.filter { it.name == "${file.baseName}.properties" }
                if (propertiesTemplateFile.isEmpty()) {
                    logger.info("not importing ${file.file}, because no ${file.baseName}.properties exists")
                } else {
                    exec "prop2po --personality=mozilla -t ${relativePath(propertiesTemplateFile.singleFile)} ${file.file} ${relativePath(into)}"
                }
            } else {
                logger.info("not importing ${file.file}, because po file already exists")
            }
        }
    }

    @Override
    protected void deleteAllOutputFilesWithoutInputFiles() {
        // do nothing
    }

    @Override
    protected void deleteOutputFilesFor(File inputFile) {
        // do nothing
    }

}
