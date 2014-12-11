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

class UpdatePotFromPropertiesTask extends AbstractGettextTask {

    @InputFiles
    FileCollection propertiesTemplateFiles

    @OutputDirectory
    File poTemplateDir

    def propertiesTemplateFiles(propertiesTemplateFiles) {
        this.propertiesTemplateFiles = project.fileTree(propertiesTemplateFiles) {
            include '**/*.properties'
            exclude '**/*_*.properties'
        }
    }

    def poTemplateDir(poTemplateDir) {
        this.poTemplateDir = project.file(poTemplateDir)
    }

    @TaskAction
    def updatePot(IncrementalTaskInputs inputs) {
        if (!inputs.incremental) {
            poTemplateDir.eachFileMatch(FileType.FILES, ~/.*\.pot/) {
                it.delete()
            }
        }
        def propertiesFiles = []
        inputs.outOfDate { outOfDate ->
            propertiesFiles << project.relativePath(outOfDate.file)
        }
        if (propertiesFiles) {
            exec "prop2po --progress=none --personality=mozilla --pot ${propertiesFiles.join(' ')} ${project.relativePath(poTemplateDir)}"
        }
        inputs.removed { removed ->
            String baseName = removed.file.name - '.properties'
            poTemplateDir.eachFileMatch(FileType.FILES, { it == "${baseName}.pot" }) {
                it.delete()
            }
        }
    }


}
