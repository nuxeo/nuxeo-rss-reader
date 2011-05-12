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

import static org.junit.Assert.*;

import java.net.URL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationParameters;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * @author <a href="mailto:ei@nuxeo.com">Eugen Ionica</a>
 *
 */
@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
@RepositoryConfig(type = BackendType.H2, user = "Administrator", cleanup = Granularity.METHOD)
@Deploy({ "org.nuxeo.rss.reader", "org.nuxeo.ecm.automation.core",
        "org.nuxeo.ecm.automation.features", "org.nuxeo.ecm.platform.query.api" })
public class TestParseFeedOperation {

    @Inject
    CoreSession session;

    @Inject
    AutomationService service;

    @Test
    public void testParseFeedOperation() throws Exception {
        OperationContext ctx = new OperationContext(session);
        assertNotNull(ctx);

        URL url = this.getClass().getClassLoader().getResource("feed.rss");
        assertNotNull(url);

        OperationChain chain = new OperationChain("fakeChain");
        OperationParameters oparams = new OperationParameters(
                ParseFeedOperation.ID);
        oparams.set("feedUrl", url.toExternalForm());
        chain.add(oparams);

        Blob result = (Blob) service.run(ctx, chain);

        JSONObject object = JSONObject.fromObject(result.getString());
        assertEquals("Feed title",
                object.get(ParseFeedOperation.Field.FEED_TITLE.name()));
        JSONArray array = object.getJSONArray(ParseFeedOperation.Field.ENTRIES.name());
        assertEquals(3, array.size());

        // test limit
        oparams.set("limit", 1);
        result = (Blob) service.run(ctx, chain);

        object = JSONObject.fromObject(result.getString());
        array = object.getJSONArray(ParseFeedOperation.Field.ENTRIES.name());
        assertEquals(1, array.size());

    }

}
