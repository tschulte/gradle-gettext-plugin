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
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

class UpdatePropertiesTask extends AbstractGettextTask {

    @InputFiles
    FileCollection poFiles

    @OutputDirectory
    File propertiesDir

    def poFiles(poFiles) {
        this.poFiles = project.fileTree(poFiles) {
            include '**/*.po'
        }
    }

    def propertiesDir(propertiesDir) {
        this.propertiesDir = project.file(propertiesDir)
    }

    @TaskAction
    def updateProperties(IncrementalTaskInputs inputs) {
        inputs.outOfDate { outOfDate ->
            def file = outOfDate.file
            int i = file.name.indexOf('_')
            String baseName = file.name.substring(0, i)
            File propertiesTemplate = new File(file.parent, "${baseName}.properties")
            exec "po2prop --personality=mozilla --removeuntranslated -t ${project.relativePath(propertiesTemplate)} ${project.relativePath(file)} ${project.relativePath(propertiesDir)}"
        }
        inputs.removed { removed ->
            new File(propertiesDir, removed.file.name.replace('.po', '.properties')).delete()
        }
    }


}
