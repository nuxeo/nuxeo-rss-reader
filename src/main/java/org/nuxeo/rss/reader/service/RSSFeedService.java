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

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

/**
 * Service Interface that provides Rss Reader features
 *
 * @author <a href="mailto:akervern@nuxeo.com">Arnaud Kervern</a>
 * @author <a href="mailto:bjalon@nuxeo.com">Benjamin JALON</a>
 * @since 5.4.2
 */
public interface RSSFeedService {

    /**
     * This Rss Reader Management container store administration values and
     * store feeds proposed by the administrator for user. If this container
     * doesn't exists, this method create it.
     *
     * @param session
     * @return
     * @throws ClientException
     */
    DocumentModel getRssReaderManagementContainer(CoreSession session)
            throws ClientException;

    /**
     * Return the max number of articles proposed into the rss gadget.
     *
     * @param session
     * @return
     * @throws ClientException
     */
    int getDisplayedArticleCount(CoreSession session) throws ClientException;

    /**
     * Return the number of maximum feeds the user can merge into the Rss Reader
     * gadget
     *
     * @param session
     * @return
     * @throws ClientException
     */
    int getMaximumFeedsCount(CoreSession session) throws ClientException;

    List<String> getCurrentUserRssFeedAddresses(CoreSession session)
            throws ClientException;

    /**
     * return user feeds that will be displayed into the Rss Reader Gadget
     *
     * @param session
     * @return
     * @throws ClientException
     */
    DocumentModelList getCurrentUserRssFeedDocumentModelList(CoreSession session)
            throws ClientException;

    /**
     * return feed defined by administrator
     *
     * @param session
     * @return
     * @throws ClientException
     */
    DocumentModelList getGlobalFeedsDocumentModelList(CoreSession session)
            throws ClientException;

    /**
     * return rss feed container of the current user and if not exists create it
     * and copy in it the default rss feed marked as default and proposed by the
     * Administration view.
     *
     * @param session
     * @return
     * @throws ClientException
     */
    DocumentModel getCurrentUserRssFeedsContainer(CoreSession session)
            throws ClientException;
}
