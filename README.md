gradle-clickstack-plugin
========================

The Gradle clickstack plugin provide common clickstack related tasks to gradle DSL. 
It extends Gradle applicaton plugin and allows bundling clickstack using Java custom code and Gradle tasks.

# Usage

    apply plugin: 'java'
    apply plugin: 'clickstack'

    clickstackName = 'tomcat8-clickstack'


see https://github.com/CloudBees-community/tomcat-clickstack/blob/master/build.gradle for a working sample

# Tasks

# Configuration

You should declare in `~/.gradle/gradle.properties`

```

# optional - path to the 'plugins' dir of your genapp install
# used by 'installClickStack' if declared
clickstackInstallDir=/path/to/your/genapp/plugins



# optional - credentials for 'uploadArchives' task
# usually credentials for https://repository-community.forge.cloudbees.com/snapshot 
repoUsername=my_login_
repoPassword=my_password

```
