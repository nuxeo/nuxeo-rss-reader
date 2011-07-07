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
import java.io.UnsupportedEncodingException;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.util.StringList;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.impl.blob.InputStreamBlob;
import org.nuxeo.rss.reader.service.RSSFeedService;

import net.sf.json.JSONObject;

/**
 * @author <a href="mailto:ei@nuxeo.com">Eugen Ionica</a>
 *
 *         Operation that will retrieve one or more feeds from the url(s)
 *         provided, merge the entries if necessary and return the result in a
 *         JSON object
 *
 */

@Operation(id = FeedProviderOperation.ID, category = Constants.CAT_EXECUTION, label = "Parse Feed Operation", description = "Parse a feed and return a JSON array with feed entries.")
public class FeedProviderOperation {



    public static final String ID = "Feed.Provider";

    public static final JSONObject EMPTY_FEED = new JSONObject();

    @Param(name = "domain", required = false)
    protected String domain = "/default-domain";

    @Param(name = "urls", required = false)
    protected StringList urls = null;

    @Context
    protected CoreSession session;

    @Context
    protected RSSFeedService rssFeedService;

    @OperationMethod
    public Blob run() throws Exception {
        if (urls == null) {
            urls = new StringList(rssFeedService.getCurrentUserRssFeedAddresses(session));
        }
        if (urls.size() == 0) {
            return buildBlob(EMPTY_FEED.toString());
        }
        int limit = rssFeedService.getDisplayedArticleCount(session);
        if (urls.size() == 1) {
            JSONObject feed = FeedHelper.getFeed(urls.get(0), limit);
            return buildBlob(feed.toString());
        }
        JSONObject feed = FeedHelper.mergeFeeds(
                urls.toArray(new String[urls.size()]), limit);
        return buildBlob(feed.toString());
    }

    protected Blob buildBlob(String text) throws UnsupportedEncodingException {
        return new InputStreamBlob(new ByteArrayInputStream(
                text.getBytes("UTF-8")), "application/json");
    }

}
