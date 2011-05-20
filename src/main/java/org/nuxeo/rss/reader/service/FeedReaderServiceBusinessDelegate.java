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
@Name("feedReader")
@Scope(CONVERSATION)
public class FeedReaderServiceBusinessDelegate implements Serializable {
    private static final long serialVersionUID = -5326113474071108997L;

    private static final Log log = LogFactory.getLog(FeedReaderServiceBusinessDelegate.class);

    protected FeedReaderService feedReader;

    // @Create
    public void initialize() {
        log.info("Seam component initialized...");
    }

    /**
     * Acquires a new {@link FeedReaderService} reference.
     */
    @Unwrap
    public FeedReaderService getFeedReaderService() throws ClientException {
        if (null == feedReader) {
            try {
                feedReader = Framework.getService(FeedReaderService.class);
            } catch (Exception e) {
                final String errMsg = "Error connecting to FeedReaderService. "
                        + e.getMessage();
                throw new ClientException(errMsg, e);
            }

            if (null == feedReader) {
                throw new ClientException("FeedReaderService service not bound");
            }
        }

        return feedReader;
    }

    @Destroy
    @PermitAll
    public void destroy() {
        if (null != feedReader) {
            // typeManager.remove();
            feedReader = null;
        }
    }
}
