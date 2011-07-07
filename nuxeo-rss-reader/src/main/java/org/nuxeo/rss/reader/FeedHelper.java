/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     eugen
 */
package org.nuxeo.rss.reader;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.Base64;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * @author <a href="mailto:ei@nuxeo.com">Eugen Ionica</a> various static method
 *         to retrieve a feed, put feed content in a JSON object, merge two or
 *         more feeds, etc.
 *
 */
public class FeedHelper {

    private static final Log log = LogFactory.getLog(FeedHelper.class);

    public static final int NO_LIMIT = -1;

    public static enum Field {
        FEED_TITLE, FEED_LINK, FEED_DESCRIPTION, FEED_PUBDATE, TITLE, DESCRIPTION, LINK, PUBDATE, ENTRIES, FEEDS
    };

    /**
     * return a complete feed as JSON object
     *
     * @param feedUrl - URL of the feed
     * @return - feed as JSON object
     * @throws Exception
     */
    public static JSONObject getFeed(String feedUrl) throws Exception {
        return getFeed(feedUrl, NO_LIMIT);
    }

    /**
     * return partial feed as JSON object
     *
     * @param feedUrl - URL of the feed
     * @param limit -
     * @return - feed as JSON object
     * @throws Exception
     */
    public static JSONObject getFeed(String feedUrl, int limit)
            throws Exception {
        SyndFeed feed = parseFeed(feedUrl);
        return buildJson(feed, limit);
    }

    /**
     * merge two or more feeds and return all the entries
     *
     * @param urls - URLs of the feeds that will be merged
     * @return
     * @throws UnsupportedEncodingException
     */
    public static JSONObject mergeFeeds(String[] urls) throws UnsupportedEncodingException {
        return mergeFeeds(urls, NO_LIMIT);
    }

    /**
     * merge two or more feeds and return first "limit" entries
     *
     * @param urls - URLs of the feeds that will be merged
     * @param limit -
     * @return
     * @throws UnsupportedEncodingException
     */
    public static JSONObject mergeFeeds(String[] urls, int limit) throws UnsupportedEncodingException {
        MergedEntries mergedEntries = new MergedEntries(urls, limit);

        // build the JSON with feed entries
        JSONObject object = new JSONObject();
        addProperty(object, Field.FEEDS, buildFeedsInfoArray(mergedEntries.getFeeds()));
        addProperty(object, Field.ENTRIES, buildEntriesArray(mergedEntries.getEntries(), true));
        return object;
    }

    public static Map<String, Object> searchFeedEntry(String[] urls, String link) throws URISyntaxException, UnsupportedEncodingException {
        MergedEntries mergedEntries = new MergedEntries(urls, NO_LIMIT);

        List<SyndEntry> entries = mergedEntries.getEntries();
        Map<String, Object> foundEntry = new HashMap<String, Object>();
        for (int i = 0; i < entries.size(); i++) {
            SyndEntry entry = entries.get(i);
            if (link.equals(entry.getLink())) {
                foundEntry.put("entry", entry);
                if (i > 0) {
                    foundEntry.put("previous", URLEncoder.encode(entries.get(i - 1).getLink(), "UTF-8"));
                }
                if (i < entries.size() - 1) {
                    foundEntry.put("next", URLEncoder.encode(entries.get(i + 1).getLink(), "UTF-8"));
                }
                break;
            }
        }
        return foundEntry;
    }

    public static SyndFeed parseFeed(String feedUrl) throws Exception {
        URL url = new URL(feedUrl);
        SyndFeedInput input = new SyndFeedInput();
        URLConnection urlConnection;
        if (FeedUrlConfig.useProxy()) {
            SocketAddress addr = new InetSocketAddress(FeedUrlConfig.getProxyHost(), FeedUrlConfig.getProxyPort());
            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
            urlConnection = url.openConnection(proxy);
            if (FeedUrlConfig.isProxyAuthenticated()) {
                String encoded = Base64.encodeBytes(new String(
                        FeedUrlConfig.getProxyLogin() + ":"
                                + FeedUrlConfig.getProxyPassword()).getBytes());
                urlConnection.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
                urlConnection.connect();
            }
        } else {
            urlConnection = url.openConnection();
        }
        SyndFeed feed = input.build(new XmlReader(urlConnection));
        return feed;
    }

    /**
     * @param feed object
     * @return build the JSON object that represents the feed
     * @throws UnsupportedEncodingException
     *
     */
    private static JSONObject buildJson(SyndFeed feed, int limit) throws UnsupportedEncodingException {
        JSONObject object = new JSONObject();
        addFeedInfo(object, feed);

        @SuppressWarnings("unchecked")
        List<SyndEntry> entries = feed.getEntries();
        if (limit != NO_LIMIT && entries.size() > limit) {
            entries = entries.subList(0, limit);
        }
        addProperty(object, Field.ENTRIES, buildEntriesArray(entries, false));
        return object;
    }

    /**
     * build and JSON array with infos about each feed
     *
     * @param feeds
     * @return
     */
    private static JSONArray buildFeedsInfoArray(List<SyndFeed> feeds) {
        JSONArray array = new JSONArray();
        for (SyndFeed feed : feeds) {
            JSONObject o = new JSONObject();
            addFeedInfo(o, feed);
            array.add(o);
        }
        return array;
    }

    private static JSONArray buildEntriesArray(List<SyndEntry> entries,
            boolean addFeedInfo) throws UnsupportedEncodingException {
        JSONArray array = new JSONArray();
        for (SyndEntry entry : entries) {
            JSONObject o = new JSONObject();
            addProperty(o, Field.TITLE, entry.getTitle());
            addProperty(o, Field.LINK, URLEncoder.encode(entry.getLink(), "UTF-8"));

            SyndContent description = entry.getDescription();
            if (description != null) {
                String text = description.getValue();
                if ("text/html".equals(description.getType())) {
                    // drop html tags from description
                    text = extractText(text);
                }
                addProperty(o, Field.DESCRIPTION, text);
            }
            if (entry.getPublishedDate() != null) {
                addProperty(o, Field.PUBDATE, entry.getPublishedDate());
            }

            // added feed info in case entries comes from more than one feed
            if (addFeedInfo) {
                addFeedInfo(o, entry.getSource());
            }

            array.add(o);
        }
        return array;
    }

    /**
     * add info about feed in JSON object parameter
     *
     * @param object
     * @param feed
     * @return
     */
    private static JSONObject addFeedInfo(JSONObject object, SyndFeed feed) {
        addProperty(object, Field.FEED_TITLE, feed.getTitle());
        addProperty(object, Field.FEED_LINK, feed.getLink());
        addProperty(object, Field.FEED_DESCRIPTION, feed.getDescription());
        if (feed.getPublishedDate() != null) {
            addProperty(object, Field.FEED_PUBDATE, feed.getPublishedDate());
        }
        return object;
    }

    private static void addProperty(JSONObject object, Field field, Object value) {
        object.element(field.name(), value);
    }

    /**
     * drop xml/html tags
     *
     * @param html - original text
     * @return pure text
     */
    private static String extractText(String html) {
        char[] characters = html.toCharArray();
        int level = 0;
        StringBuilder sb = new StringBuilder();
        for (char c : characters) {
            switch (c) {
            case '<':
                level++;
                break;
            case '>':
                level--;
                break;
            default:
                if (level == 0) {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }


    public static class MergedEntries {

        protected List<SyndFeed> feeds = new ArrayList<SyndFeed>();

        protected List<SyndEntry> entries = new ArrayList<SyndEntry>();

        public MergedEntries(String[] urls, int limit) {
            // collect entries from feeds
            for (String url : urls) {
                try {
                    SyndFeed feed = parseFeed(url);
                    feeds.add(feed);

                    @SuppressWarnings("unchecked")
                    List<SyndEntry> list = feed.getEntries();
                    if (limit != NO_LIMIT && limit < list.size()) {
                        list = list.subList(0, limit);
                    }
                    for (SyndEntry entry : list) {
                        if (entry.getSource() == null) { // source needed when merged entries are rendered
                            entry.setSource(feed);
                        }
                    }
                    entries.addAll(list);
                } catch (Exception e) {
                    log.warn("failed to retrieve feed " + url, e);
                }
            }

            // sort entries
            Collections.sort(entries, new Comparator<SyndEntry>() {
                public int compare(SyndEntry o1, SyndEntry o2) {
                    Date publishedDate1 = o1.getPublishedDate();
                    Date publishedDate2 = o2.getPublishedDate();
                    if (publishedDate1 == null || publishedDate2 == null) {
                        return 0;
                    }
                    Calendar c1 = Calendar.getInstance();
                    Calendar c2 = Calendar.getInstance();
                    c1.setTime(publishedDate1);
                    c2.setTime(publishedDate2);
                    return -c1.compareTo(c2);
                }
            });

            // drop some elements if it's necessary
            if (limit != NO_LIMIT && limit < entries.size()) {
                entries = entries.subList(0, limit);
            }
        }

        public List<SyndFeed> getFeeds() {
            return feeds;
        }

        public List<SyndEntry> getEntries() {
            return entries;
        }

    }

}
