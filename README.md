# Nuxeo RSS reader

Nuxeo RSS related module.

# Building

## quick build:

    mvn clean install -Dmaven.test.skip=true

## build & test

    mvn clean install

## Run integration test suite

    mvn verify -f nuxeo-rss-reader-ftest/pom.xml

## Deploying

Install [the Nuxeo RSS Reader Marketplace Package](https://connect.nuxeo.com/nuxeo/site/marketplace/package/nuxeo-rss-reader/).
Or manually copy the built artifacts into `$NUXEO_HOME/templates/custom/bundles/` and activate the "custom" template.

## QA results

[![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=addons_nuxeo-rss-reader-master)](https://qa.nuxeo.org/jenkins/job/addons_nuxeo-rss-reader-master/)

# About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at www.nuxeo.com.
