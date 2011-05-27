## Nuxeo RSS reader

Nuxeo RSS related module. 

## How to build

# quick build:
  ant build 

# build & test
  ant build-with-tests 
  #or 
  mvn clean install

# deploy in a nuxeo tomcat instance
  ant deploy-tomcat 

Note: the tomcat.dir should be defined in a build.properties

# Run integration test suite
  ant integration-test


## About Nuxeo

Nuxeo provides a modular, extensible Java-based [open source software platform for enterprise content management](http://www.nuxeo.com/en/products/ep) and packaged applications for [document management](http://www.nuxeo.com/en/products/document-management), [digital asset management](http://www.nuxeo.com/en/products/dam) and [case management](http://www.nuxeo.com/en/products/case-management). Designed by developers for developers, the Nuxeo platform offers a modern architecture, a powerful plug-in model and extensive packaging capabilities for building content applications.

More information on: <http://www.nuxeo.com/ 

