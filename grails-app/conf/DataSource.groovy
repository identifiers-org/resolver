hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}

Properties databaseProperties = new Properties()
String pathToConfig=getConfigFilePath()
if (!pathToConfig) {
    throw new Exception("No config file available")
}

private String getConfigFilePath() {
    String path = System.getenv("IDORG_CONFIG");
    if (!path || !testPath(path)) {
        path=System.getProperty("user.home") + System.getProperty("file.separator") + ".idorg.properties"
        if (!testPath(path)) {
            path=null
        }
    }
    return path
}

private boolean testPath(String path) {
    File testFile=new File(path)
    return testFile.exists()
}

databaseProperties.load(new FileInputStream(pathToConfig))
def databaseConfig = new ConfigSlurper().parse(databaseProperties)

// environment specific settings
environments {
    development {
        dataSource {
            pooled = true

            //dbCreate = "validate"   // checks that the current schema matches the domain model, but doesn't modify the database in any way
            url = databaseConfig.idorg.dev.database.url
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect   //org.hibernate.dialect.MySQL5InnoDBDialect
            username = databaseConfig.idorg.dev.database.username
            password = databaseConfig.idorg.dev.database.password
            properties {
                // to solve the:
                //     Error 500: Executing action [resolve] of controller [uk.ac.ebi.miriam.resolver.UriRecordController] caused exception: Runtime error executing action
                //     Exception Message: Broken pipe
                //     Class: UriRecordController
                //     At Line: [114]   >>> first database query
                validationQuery = "select 1"
                testWhileIdle = true
                timeBetweenEvictionRunsMillis = 60000
            }
        }
    }

    //TODO: figure out how to use jndi with london datacenters
    production {
        dataSource {

            pooled = true
            //dbCreate = "validate"   // checks that the current schema matches the domain model, but doesn't modify the database in any way

            String datacenter = System.getenv("DATACENTRE");
            if(datacenter.equals("pg")){
                url = databaseConfig.idorg.prodpg.database.url
            }else if (datacenter.equals("oy")) {
                url = databaseConfig.idorg.prodoy.database.url
            }
            else{
                url = databaseConfig.idorg.prod.database.url
            }
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect   //org.hibernate.dialect.MySQLMyISAMDialect
            username = databaseConfig.idorg.prod.database.username
            password = databaseConfig.idorg.prod.database.password
            properties {
                // to solve the:
                //     Error 500: Executing action [resolve] of controller [uk.ac.ebi.miriam.resolver.UriRecordController] caused exception: Runtime error executing action
                //     Exception Message: Broken pipe
                //     Class: UriRecordController
                //     At Line: [114]   >>> first database query
                validationQuery = "select 1"
                testWhileIdle = true
                timeBetweenEvictionRunsMillis = 60000
            }
        }
    }

    test {
        dataSource {
            pooled = true
            //dbCreate = "validate"   // checks that the current schema matches the domain model, but doesn't modify the database in any way
            url = databaseConfig.idorg.test.database.url
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
            username = databaseConfig.idorg.test.database.username
            password = databaseConfig.idorg.test.database.password
            properties {
                // to solve the:
                //     Error 500: Executing action [resolve] of controller [uk.ac.ebi.miriam.resolver.UriRecordController] caused exception: Runtime error executing action
                //     Exception Message: Broken pipe
                //     Class: UriRecordController
                //     At Line: [114]   >>> first database query
                validationQuery = "select 1"
                testWhileIdle = true
                timeBetweenEvictionRunsMillis = 60000
            }
        }
    }
}

