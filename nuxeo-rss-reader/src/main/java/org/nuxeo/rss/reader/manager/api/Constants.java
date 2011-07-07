package org.nuxeo.rss.reader.manager.api;

import static org.nuxeo.ecm.core.management.storage.DocumentStoreManager.MANAGEMENT_ROOT_PATH;

public class Constants {

    private Constants() {
        // Constants class
    }

    public static final String RSS_READER_MANAGEMENT_ROOT_TYPE = "RssFeedRoot";

    public static final String RSS_FEED_TYPE = "RssFeed";

    public static final String RSS_FEED = "RssFeed";

    public static final String RSS_READER_MANAGEMENT_ROOT_NAME = "rssFeeds";

    public static final String RSS_READER_MANAGEMENT_ROOT_PATH = MANAGEMENT_ROOT_PATH
            + "/" + RSS_READER_MANAGEMENT_ROOT_NAME;

    public static final String RSS_FEED_URL_PROPERTY = "rf:rss_address";

    public static final String RSS_FEED_IS_DEFAULT_PROPERTY = "rf:is_default_feed";

    public static final String RSS_GADGET_ARTICLE_COUNT = "rg:article_count";

    public static final String RSS_GADGET_MAX_FEED_COUNT = "rg:max_feed_count";
}
