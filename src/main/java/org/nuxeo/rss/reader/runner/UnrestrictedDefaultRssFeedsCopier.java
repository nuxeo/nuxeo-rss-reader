/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.rss.reader.runner;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

/**
 * Unrestricted runner to copy defaults feed to user feeds container
 *
 * @author <a href="mailto:akervern@nuxeo.com">Arnaud Kervern</a>
 * @since 5.4.2
 */
public class UnrestrictedDefaultRssFeedsCopier extends
        UnrestrictedSessionRunner {

    protected static final String QUERY_USER_RSS_FEED = "SELECT * FROM RssFeed "
            + "WHERE rf:is_default_feed = 1 "
            + "AND ecm:currentLifeCycleState != 'deleted' "
            + "AND ecm:path STARTSWITH '%s'";

    private static Log log = LogFactory.getLog(UnrestrictedDefaultRssFeedsCopier.class);

    protected String userFeedsContainerPath;

    protected String feedsContainerPath;

    public UnrestrictedDefaultRssFeedsCopier(CoreSession session,
            String userFeedsContainerPath, String feedsContainerPath) {
        super(session);
        this.userFeedsContainerPath = userFeedsContainerPath;
        this.feedsContainerPath = feedsContainerPath;
    }

    @Override
    public void run() throws ClientException {
        if (session.getChildren(new PathRef(userFeedsContainerPath)).isEmpty()) {
            List<DocumentModel> userFeeds = session.copy(
                    getDefaultRssFeedModels(), new PathRef(
                            userFeedsContainerPath));
            for (DocumentModel doc : userFeeds) {
                doc.getACP().getOrCreateACL().clear();
            }
            session.save();
        } else {
            log.warn("Trying to copy user feeds to an non empty container");
        }
    }

    protected List<DocumentRef> getDefaultRssFeedModels()
            throws ClientException {
        List<DocumentRef> defaultFeeds = new ArrayList<DocumentRef>();
        DocumentModelList docs = session.query(String.format(
                QUERY_USER_RSS_FEED, feedsContainerPath));
        for (DocumentModel doc : docs) {
            defaultFeeds.add(doc.getRef());
        }
        return defaultFeeds;
    }
}
