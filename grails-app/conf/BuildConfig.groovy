grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.war.file = "target/identifiers_org.war"
grails.servlet.version = "2.5"
grails.project.source.level = 1.7
grails.project.target.level = 1.7
grails.project.dependency.resolver = "maven"

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

        runtime 'mysql:mysql-connector-java:5.1.17'
        runtime 'joda-time:joda-time:2.3'
        runtime 'org.openrdf.sesame:sesame-query:2.7.10'
        runtimt 'org.openrdf.sesame:sesame-runtime:2.7.10'
        runtime 'org.openrdf.sesame:sesame-queryalgebra-model:2.7.10'
        runtime 'org.openrdf.sesame:sesame-queryalgebra-evaluation:2.7.10'
        runtime 'org.openrdf.sesame:sesame-sail-memory:2.7.10'
        runtime 'org.openrdf.sesame:sesame-repository-sail:2.7.10'
        runtime 'org.openrdf.sesame:sesame-rio-turtle:2.7.10'
        runtime 'org.openrdf.sesame:sesame-rio-rdfxml:2.7.10'
        runtime 'org.openrdf.sesame:sesame-queryresultio-text:2.7.10'
        //runtime 'org.openrdf.sesame:sesame-http-workbench:2.7.10'
        //runtime 'org.openrdf.sesame:sesame-http-server:2.7.10'
        runtime 'org.openrdf.sesame:sesame-queryresultio-sparqljson:2.7.10'
        runtime 'org.openrdf.sesame:sesame-queryresultio-binary:2.7.10'
        runtime "junit:junit:4.8.1"

        runtime 'javax.servlet:jstl:1.1.2'
        runtime 'taglibs:standard:1.1.2'
    }

    plugins {
        build ":tomcat:7.0.54"

        // plugins for the compile step
        compile ":scaffold-core:1.3.2"
        compile ':cache:1.1.8'
        compile ':asset-pipeline:2.8.0'

        runtime ":jquery:1.11.1"
        runtime ":resources:1.2.14"
        runtime ":functional-test:1.2.7"
        runtime ":hibernate4:4.3.8.1"


    }

}
