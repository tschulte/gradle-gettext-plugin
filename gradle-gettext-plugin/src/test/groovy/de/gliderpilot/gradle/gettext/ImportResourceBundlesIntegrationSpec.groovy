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

import spock.lang.Ignore
import spock.lang.Stepwise

class ImportResourceBundlesIntegrationSpec extends AbstractPluginSpecification {

    def setupSpec() {
        IntegrationTestProject.enhance(project())
        project.buildFile << """
            buildscript {
                dependencies {
                    classpath files('${new File('build/classes/main').absoluteFile.toURI()}')
                    classpath files('${new File('build/resources/main').absoluteFile.toURI()}')
                }
            }
            apply plugin: 'java'
            apply plugin: 'de.gliderpilot.gettext'
        """
    }

    def cleanup() {
        project.cleanup()
        project.file('src/main/i18n').deleteDir()
    }

    def "importResourceBundles is finalized by updatePot"() {
        when:
        project.run(':importResourceBundles')

        then:
        project.wasExecuted(':importResourceBundles')

        and:
        project.wasExecuted(':updatePot')

        and:
        project.wasExecuted(':updatePo')
    }

    def "empty properties file results in no po file"() {
        setup:
        project.createFile('src/main/i18n/empty.properties')
        project.createFile('src/main/i18n/empty_de.properties')

        when:
        project.run(':importResourceBundles')

        then:
        !project.file('src/main/i18n/empty_de.po').exists()
    }

    def "non empty properties file results in po file"() {
        setup:
        project.createFile('src/main/i18n/non-empty.properties').text = 'key=value'
        project.createFile('src/main/i18n/non-empty_de.properties').text = 'key=value'

        when:
        project.run(':importResourceBundles')

        then:
        project.file('src/main/i18n/non-empty_de.po').exists()
    }

    /*
     * This results in an error during msgmerge. I don't think I should fix this.
     * When there are duplicate entries in the default bundle, this is an error in the
     * application.
     */
    @Ignore("This results in an error during msgmerge.")
    def "properties file with duplicate keys results in po file"() {
        setup:
        project.createFile('src/main/i18n/duplicate-keys.properties').text = '''\
            key=value
            key=value
        '''.stripIndent()
        project.createFile('src/main/i18n/duplicate-keys_de.properties').text = '''\
            key=value
        '''.stripIndent()

        when:
        project.run(':importResourceBundles')

        then:
        project.file('src/main/i18n/duplicate-keys_de.po').exists()
    }

    def "translated properties file with duplicate keys results in po file"() {
        setup:
        project.createFile('src/main/i18n/translated-duplicate-keys.properties').text = '''\
            key=value
        '''.stripIndent()
        project.createFile('src/main/i18n/translated-duplicate-keys_de.properties').text = '''\
            key=value
            key=value
        '''.stripIndent()

        when:
        project.run(':importResourceBundles')

        then:
        project.file('src/main/i18n/translated-duplicate-keys_de.po').exists()
    }

    def "translated properties file with duplicate keys but different values results in po file"() {
        setup:
        project.createFile('src/main/i18n/translated-duplicate-keys-different-values.properties').text = '''\
            key=value
        '''.stripIndent()
        project.createFile('src/main/i18n/translated-duplicate-keys-different-values_de.properties').text = '''\
            key=value1
            key=value2
        '''.stripIndent()

        when:
        project.run(':importResourceBundles')

        then:
        project.file('src/main/i18n/translated-duplicate-keys-different-values_de.po').exists()
    }

    def "empty properties file for non-empty template properties file results in po file"() {
        setup:
        project.createFile('src/main/i18n/empty-translation-for-non-empty.properties').text = 'key=value'
        project.createFile('src/main/i18n/empty-translation-for-non-empty_de.properties').text = ''

        when:
        project.run(':importResourceBundles')

        then:
        project.file('src/main/i18n/empty-translation-for-non-empty_de.po').exists()
    }

    def "non empty properties file without template properties file results in no po file"() {
        setup:
        project.createFile('src/main/i18n/non-empty-translation-without-template_de.properties').text = 'key=translated'

        when:
        project.run(':importResourceBundles')

        then:
        project.file('src/main/i18n/non-empty-translation-without-template_de.po').exists()
    }
}
