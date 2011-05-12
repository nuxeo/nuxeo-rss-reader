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

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.InputStreamBlob;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * @author <a href="mailto:ei@nuxeo.com">Eugen Ionica</a>
 * Operation  parses the feed and put the result in a JSON object
 */

@Operation(id = ParseFeedOperation.ID, category = Constants.CAT_EXECUTION, label = "Parse Feed Operation", description = "Parse a feed and return a JSON array with feed entries.")
public class ParseFeedOperation {

    public static final String ID = "Feed.Parser";

    public static enum Field {
        FEED_TITLE, FEED_LINK, FEED_DESCRIPTION, FEED_PUBDATE, TITLE, DESCRIPTION, LINK, PUBDATE, ENTRIES
    };

    @Param(name = "feedUrl")
    protected String feedUrl;

    @Param(name = "limit", required = false)
    protected int limit = 5;

    @Param(name = "dateFormat", required = false)
    protected String dateFormat = "dd/MM/yyyy";

    @OperationMethod
    public Blob run() throws Exception {
        URL url = new URL(feedUrl);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(url));
        JSONObject object = buildJson(feed);
        return new InputStreamBlob(new ByteArrayInputStream(
                object.toString().getBytes("UTF-8")), "application/json");
    }

    /**
     * @param feed object
     * @return build the JSON object that will be the output of the operation
     */
    protected JSONObject buildJson(SyndFeed feed) {
        JSONObject object = new JSONObject();
        addProperty(object, Field.FEED_TITLE, feed.getTitle());
        addProperty(object, Field.FEED_LINK, feed.getLink());
        addProperty(object, Field.FEED_DESCRIPTION, feed.getDescription());
        if (feed.getPublishedDate() != null) {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            addProperty(object, Field.FEED_PUBDATE,
                    format.format(feed.getPublishedDate()));
        }
        @SuppressWarnings("unchecked")
        List<SyndEntry> entries = feed.getEntries();
        if (entries.size() > limit) {
            entries = entries.subList(0, limit);
        }
        addProperty(object, Field.ENTRIES, buildEntriesArray(entries));
        return object;
    }

    protected JSONArray buildEntriesArray(List<SyndEntry> entries) {
        JSONArray array = new JSONArray();
        for (SyndEntry entry : entries) {
            JSONObject o = new JSONObject();
            addProperty(o, Field.TITLE, entry.getTitle());
            addProperty(o, Field.LINK, entry.getLink());

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
                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                addProperty(o, Field.PUBDATE,
                        format.format(entry.getPublishedDate()));
            }
            array.add(o);
        }
        return array;
    }

    protected void addProperty(JSONObject object, Field field, Object value) {
        object.element(field.name(), value);
    }

    /**
     * drop xml/html tags
     *
     * @param html - original text
     * @return pure text
     */
    protected String extractText(String html) {
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

}
