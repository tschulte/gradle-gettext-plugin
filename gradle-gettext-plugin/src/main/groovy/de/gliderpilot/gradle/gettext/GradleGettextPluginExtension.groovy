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

import org.gradle.api.Project
import org.gradle.api.file.FileCollection

import javax.inject.Inject

class GradleGettextPluginExtension {

    private GradleGettextPlugin plugin
    private Project project

    @Inject
    GradleGettextPluginExtension(GradleGettextPlugin plugin, Project project) {
        this.plugin = plugin
        this.project = project
    }

    def languages(String... languages) {
        project.tasks.msgInit.languages(languages)
    }

    def xgettext(Closure closure) {

    }

    def prop2po(Closure closure) {

    }

    def poTemplateFiles(Object poTemplateFiles) {
        project.tasks.msgInit {
            delegate.poTemplateFiles poTemplateFiles
        }
        project.tasks.updatePo {
            delegate.poTemplateFiles poTemplateFiles
        }
        project.tasks.updatePot {
            delegate.poTemplateDir poTemplateFiles
        }
    }

    def poTemplateFiles(FileCollection poTemplateFiles) {
        project.tasks.msgInit {
            delegate.poTemplateFiles = poTemplateFiles
        }
        project.tasks.updatePo {
            delegate.poTemplateFiles = poTemplateFiles
        }
        // FIXME
        project.tasks.updatePot {
            delegate.poTemplateDir = poTemplateFiles
        }
    }

    def poFiles(Object poFiles) {
        project.tasks.msgInit {
            delegate.poDir poFiles
        }
        project.tasks.updateProperties {
            delegate.poFiles poFiles
        }
        project.tasks.updatePo {
            delegate.poFiles poFiles
        }
        project.tasks.importResourceBundles {
            delegate.delegate.poDir poFiles
        }
    }

    def poFiles(FileCollection poFiles) {
        project.tasks.msgInit {
            delegate.poDir = poFiles
        }
        project.tasks.updateProperties {
            delegate.poFiles = poFiles
        }
        project.tasks.updatePo {
            delegate.poFiles = poFiles
        }
        project.tasks.importResourceBundles {
            delegate.delegate.poDir = poFiles
        }
    }

    def propertiesTemplateFiles(Object propertiesTemplateFiles) {
        project.tasks.updateProperties {
            delegate.propertiesTemplateFiles propertiesTemplateFiles
        }
        project.tasks.updatePot {
            delegate.propertiesTemplateFiles propertiesTemplateFiles
        }
        project.tasks.importResourceBundles {
            delegate.propertiesTemplateFiles propertiesTemplateFiles
        }
    }

    def propertiesTemplateFiles(FileCollection propertiesTemplateFiles) {
        project.tasks.updateProperties {
            delegate.propertiesTemplateFiles = propertiesTemplateFiles
        }
        project.tasks.updatePot {
            delegate.propertiesTemplateFiles = propertiesTemplateFiles
        }
        project.tasks.importResourceBundles {
            delegate.propertiesTemplateFiles = propertiesTemplateFiles
        }
    }

    def propertiesFiles(Object propertiesFiles) {
        project.tasks.updateProperties {
            delegate.propertiesDir propertiesFiles
        }
        project.tasks.importResourceBundles {
            delegate.propertiesFiles propertiesFiles
        }
    }

    def propertiesFiles(FileCollection propertiesFiles) {
        project.tasks.updateProperties {
            delegate.propertiesDir = propertiesFiles
        }
        project.tasks.importResourceBundles {
            delegate.propertiesFiles = propertiesFiles
        }
    }
}
