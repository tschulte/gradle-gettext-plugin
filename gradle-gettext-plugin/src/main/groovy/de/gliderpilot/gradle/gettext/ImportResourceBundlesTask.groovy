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
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class ImportResourceBundlesTask extends AbstractGettextTask {

    @InputFiles
    FileCollection propertiesFiles

    @OutputDirectory
    File poDir

    def propertiesFiles(propertiesFiles) {
        this.propertiesFiles = project.fileTree(propertiesFiles) {
            include '**/*_*.properties'
        }
    }

    def poDir(poDir) {
        this.poDir = project.file(poDir)
    }

    @TaskAction
    def importBundles() {
        propertiesFiles.each { file ->
            int i = file.name.indexOf('_')
            String baseName = file.name.substring(0, i)
            String poFileName = (file.name - '.properties') + '.po'
            File propertiesTemplate = new File(file.parent, "${baseName}.properties")
            if (!new File(poDir, poFileName).exists()) {
                exec "prop2po --personality=mozilla -t ${project.relativePath(propertiesTemplate)} ${project.relativePath(file)} ${project.relativePath(poDir)}"
            } else {
                logger.info("not importing ${file.name}, because po file already exists")
            }
        }
    }


}
