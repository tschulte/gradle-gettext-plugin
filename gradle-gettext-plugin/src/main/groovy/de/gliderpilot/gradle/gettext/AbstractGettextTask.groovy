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

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs
import org.gradle.process.ExecResult

abstract class AbstractGettextTask extends DefaultTask {

    @InputFiles
    FileCollection from

    @OutputDirectory
    File into

    @OutputFiles
    FileCollection outputFiles

    protected void from(Object from, Closure closure) {
        this.from = project.fileTree(from, closure)
    }

    protected void into(Object into, Closure closure) {
        this.into = project.file(into)
        outputFiles = project.fileTree(into, closure)
    }

    @TaskAction
    def performTask(IncrementalTaskInputs taskInputs) {
        if (!taskInputs.incremental) {
            deleteAllOutputFilesWithoutInputFiles()
        }
        def changedInputFiles = []
        taskInputs.outOfDate { outOfDate ->
            changedInputFiles << attributes(outOfDate.file)
        }
        if (changedInputFiles) {
            update(changedInputFiles)
        }
        taskInputs.removed { removed ->
            deleteOutputFilesFor(removed.file)
        }
    }

    protected ExecResult exec(String command) {
        project.exec {
            commandLine "bash", "-c"
            args command
        }
    }

    protected String relativePath(Object file) {
        project.relativePath(file)
    }

    protected Map<String, String> attributes(File file) {
        String fileName = file.name
        int indexOfUnderscore = fileName.indexOf('_')
        int indexOfDot = fileName.lastIndexOf('.')
        String baseName = indexOfUnderscore > 0 ? fileName.substring(0, indexOfUnderscore) : fileName.substring(0, indexOfDot)
        String locale = indexOfUnderscore > 0 ? fileName.substring(indexOfUnderscore + 1, indexOfDot) : null
        [file: relativePath(file), baseName: baseName, locale: locale]
    }

    protected abstract void update(List<Map<String, String>> changedInputFiles)

    protected void deleteAllOutputFilesWithoutInputFiles() {
        outputFiles.filter { from.filter(sameBaseNameFilter(it)).isEmpty() }*.delete()
    }

    protected void deleteOutputFilesFor(File inputFile) {
        outputFiles.filter(sameBaseNameFilter(inputFile))*.delete()
    }

    protected Closure sameBaseNameFilter(File file) {
        sameBaseNameFilter(attributes(file))
    }

    protected final Closure sameBaseNameFilter(Map<String, String> file) {
        return { attributes(it) == file.baseName }
    }
}
