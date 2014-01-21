// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = true   // why is it 'false' by default???
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      //xml: ['text/xml', 'application/xml'],
                      //text: 'text/plain',
                      //js: 'text/javascript',
                      //rss: 'application/rss+xml',
                      //atom: 'application/atom+xml',
                      //css: 'text/css',
                      //csv: 'text/csv',
                      //json: ['application/json','text/json'],
                      //form: 'application/x-www-form-urlencoded',
                      //multipartForm: 'multipart/form-data'
                      rdf: 'application/rdf+xml',
                      all: '*/*'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'
// usage of JQuery
grails.views.javascript.library="jquery"

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "http://identifiers.org"
        subdomain = "http://info.identifiers.org"
        staticpages = "http://www.ebi.ac.uk/compneur-srv/identifiers-org/prod/"
    }
    development {
        grails.serverURL = "http://dev.identifiers.org"
        subdomain = "http://dev.info.identifiers.org"
        staticpages = "http://www.ebi.ac.uk/compneur-srv/identifiers-org/dev/"

    }
    test {
        grails.serverURL = "http://localhost:8080"
        subdomain = "http://info.localhost:8080"
        staticpages = "http://www.ebi.ac.uk/compneur-srv/identifiers-org/dev/"
    }

}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}
     appenders {
/*         environments {
             development {
                 file name:'file', file:'/nfs/public/rw/webadmin/tomcat/bases/biomodels.net/tc-pst-identifiers_test/logs/identifiers.log'
                 rollingFile name: "stacktrace", maxFileSize: 1024, file: "/nfs/public/rw/webadmin/tomcat/bases/biomodels.net/tc-pst-identifiers_test/logs/stacktrace.log"
             }
             test{
                 String logDir = System.getenv('LOG_FOLDER')
                       file name: 'stacktrace',
                            file: "$logDir/stacktrace.log",
                            layout: pattern(conversionPattern: "'%d [%t] %-5p %c{2} %x -%m%n'")
             }

         }*/

/*         environments{
             development{
                     file name: "stacktrace", append: true,
                          file: System.getenv('LOG_FOLDER')+"stacktrace.log" //"/nfs/public/rw/webadmin/tomcat/bases/biomodels.net/tc-pst-identifiers_test/logs/ves-hx-4d/stacktrace.log"
             }
             test{
                     file name: "stacktrace", append: true,
                          file: "C:\\Users\\sarala.EBI\\Downloads\\stacktrace.log"
             }
         }*/
         appenders {
                 'null' name: "stacktrace"
             }
     }


    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
}

grails.app.context = "/"

