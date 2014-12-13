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

abstract class AbstractGradleGettextPluginIntegrationSpec extends AbstractPluginSpecification {

    def setupSpec() {
        IntegrationTestProject.enhance(project())
        project.buildFile << """
            buildscript {
                dependencies {
                    classpath files('${new File('build/classes/main').absoluteFile.toURI()}')
                    classpath files('${new File('build/resources/main').absoluteFile.toURI()}')
                }
            }
            apply plugin: 'de.gliderpilot.gettext'
        """
    }

    def cleanup() {
        project.cleanup()
        project.file('src/main/i18n').deleteDir()
    }

}
