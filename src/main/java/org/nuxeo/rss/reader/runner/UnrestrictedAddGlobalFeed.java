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
package org.nuxeo.rss.reader.runner;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

/**
 * @author <a href="mailto:ei@nuxeo.com">Eugen Ionica</a>
 *
 */
public class UnrestrictedAddGlobalFeed extends UnrestrictedSessionRunner{
    String feedId;
    String targetPath;

    /**
     * @param session
     * @param feedId
     * @param targetPath
     */
    public UnrestrictedAddGlobalFeed(CoreSession session, String feedId,
            String targetPath) {
        super(session);
        this.feedId = feedId;
        this.targetPath = targetPath;
    }

    public void run() throws ClientException {
        DocumentModel feed = session.copy(new IdRef(feedId), new PathRef(targetPath), null);
        feed.getACP().getOrCreateACL().clear();
    }

}
