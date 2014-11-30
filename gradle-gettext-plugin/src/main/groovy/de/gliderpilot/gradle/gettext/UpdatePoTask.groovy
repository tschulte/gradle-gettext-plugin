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
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import org.gradle.api.tasks.incremental.InputFileDetails

class UpdatePoTask extends AbstractGettextTask {

    @InputFiles
    FileCollection templates

    @OutputFiles
    FileCollection translations

    def from(from) {
        this.templates = project.fileTree(from) {
            include '**/*.pot'
        }
    }

    def into(into) {
        this.translations = project.fileTree(into) {
            include '**/*.po'
        }
    }

    @TaskAction
    def updatePo(IncrementalTaskInputs inputs) {
        inputs.outOfDate { outOfDate ->
            File template = outOfDate.file
            String baseName = template.name - '.pot'
            translations.findAll { it.name.contains(baseName) }.each { translation ->
                int i = translation.name.indexOf('_')
                String lang = translation.name.substring(i + 1) - '.po'
                execute "msgmerge -vU --backup=off --lang=$lang $translation $template"
            }
        }
        inputs.removed { removed ->
            translations.findAll { it.name.contains(baseName) }*.delete()
        }
    }

}
