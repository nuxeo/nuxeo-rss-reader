package org.nuxeo.rss.reader.manager.api;

import static org.nuxeo.ecm.core.management.storage.DocumentStoreManager.MANAGEMENT_ROOT_PATH;

public class Constants {

    private Constants() {
        // Constants class
    }

    public static final String RSS_FEED_ROOT_TYPE = "RssFeedRoot";

    public static final String RSS_FEED_TYPE = "RssFeed";

    public static final String RSS_FEED = "RssFeed";

    public static final String RSS_FEEDS_FOLDER = "rssFeeds";

    public static final String RSS_FEED_CONTAINER_PATH = MANAGEMENT_ROOT_PATH
            + "/" + RSS_FEEDS_FOLDER;
}
