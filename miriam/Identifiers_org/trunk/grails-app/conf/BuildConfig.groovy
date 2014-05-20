grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.war.file = "target/identifiers_org.war"
grails.servlet.version = "2.5"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.13'
        runtime 'joda-time:joda-time:2.3'
        runtime 'org.openrdf.sesame:sesame-query:2.7.10'
        runtimt 'org.openrdf.sesame:sesame-runtime:2.7.10'
        runtime 'org.openrdf.sesame:sesame-queryalgebra-model:2.7.10'
        runtime 'org.openrdf.sesame:sesame-queryalgebra-evaluation:2.7.10'
        runtime 'org.openrdf.sesame:sesame-sail-memory:2.7.10'
        runtime 'org.openrdf.sesame:sesame-repository-sail:2.7.10'
        runtime 'org.openrdf.sesame:sesame-rio-turtle:2.7.10'
        runtime 'org.openrdf.sesame:sesame-queryresultio-text:2.7.10'
        //runtime 'org.openrdf.sesame:sesame-http-workbench:2.7.10'
        //runtime 'org.openrdf.sesame:sesame-http-server:2.7.10'
        runtime 'org.openrdf.sesame:sesame-queryresultio-sparqljson:2.7.10'
        runtime "junit:junit:4.8.1"
    }

    plugins {
        runtime ":jquery:1.6.1.1"
        runtime ":resources:1.0"
        runtime ":functional-test:1.2.7"
        runtime ":hibernate:$grailsVersion"
        //build ":tomcat:$grailsVersion"
        build ":tomcat:$grailsVersion"

    }

}
