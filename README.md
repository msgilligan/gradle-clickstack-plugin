gradle-clickstack-plugin
========================

The Gradle clickstack plugin provide common clickstack related tasks to gradle DSL. 
It extends Gradle applicaton plugin and allows bundling clickstack using Java custom code and Gradle tasks.

## Usage

    apply plugin: 'java'
    apply plugin: 'clickstack'

    clickstackName = 'tomcat8-clickstack'


see https://github.com/CloudBees-community/tomcat8-clickstack/blob/master/build.gradle for a working sample


