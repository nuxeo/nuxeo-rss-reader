# Nuxeo RSS reader

Nuxeo RSS related module. 

## How to build

### quick build:

    mvn clean install -Dmaven.test.skip=true

### build & test

    mvn clean install

### Run integration test suite

    mvn verify -f nuxeo-rss-reader-ftest/pom.xml

### deploy in an existing nuxeo tomcat instance

    ant deploy-tomcat 

Note: the `tomcat.dir` should be defined in a `build.properties` file.


## About Nuxeo

Nuxeo provides a modular, extensible Java-based [open source software platform for enterprise content management](http://www.nuxeo.com/en/products/ep) and packaged applications for [document management](http://www.nuxeo.com/en/products/document-management), [digital asset management](http://www.nuxeo.com/en/products/dam) and [case management](http://www.nuxeo.com/en/products/case-management). Designed by developers for developers, the Nuxeo platform offers a modern architecture, a powerful plug-in model and extensive packaging capabilities for building content applications.

More information on: <http://www.nuxeo.com/>

