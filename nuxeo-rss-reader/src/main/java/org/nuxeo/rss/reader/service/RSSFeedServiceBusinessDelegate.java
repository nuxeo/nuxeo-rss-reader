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

package org.nuxeo.rss.reader.service;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;

import javax.annotation.security.PermitAll;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.runtime.api.Framework;

/**
 * @author <a href="mailto:akervern@nuxeo.com">Arnaud Kervern</a>
 */
@Name("rssFeed")
@Scope(CONVERSATION)
public class RSSFeedServiceBusinessDelegate implements Serializable {
    private static final long serialVersionUID = -5326113474071108997L;

    private static final Log log = LogFactory.getLog(RSSFeedServiceBusinessDelegate.class);

    protected RSSFeedService RSSFeed;

    // @Create
    public void initialize() {
        log.info("Seam component initialized...");
    }

    /**
     * Acquires a new {@link RSSFeedService} reference.
     */
    @Unwrap
    public RSSFeedService getFeedReaderService() throws ClientException {
        if (null == RSSFeed) {
            try {
                RSSFeed = Framework.getService(RSSFeedService.class);
            } catch (Exception e) {
                final String errMsg = "Error connecting to RSSFeedService. " + e.getMessage();
                throw new ClientException(errMsg, e);
            }

            if (null == RSSFeed) {
                throw new ClientException("RSSFeedService service not bound");
            }
        }

        return RSSFeed;
    }

    @Destroy
    @PermitAll
    public void destroy() {
        if (null != RSSFeed) {
            // typeManager.remove();
            RSSFeed = null;
        }
    }
}
