[![Build Status](https://travis-ci.org/tschulte/gradle-gettext-plugin.svg?branch=master)](https://travis-ci.org/tschulte/gradle-gettext-plugin)
[![Coverage Status](https://coveralls.io/repos/tschulte/gradle-gettext-plugin/badge.png?branch=master)](https://coveralls.io/r/tschulte/gradle-gettext-plugin?branch=master)

Gradle plugin for GNU gettext
-----------------------------

To use in an application add following to your build.gradle:

```groovy
buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath 'de.gliderpilot.gradle.gettext:gradle-gettext-plugin:+'
    }
}

apply plugin: 'de.gliderpilot.gettext'
```
