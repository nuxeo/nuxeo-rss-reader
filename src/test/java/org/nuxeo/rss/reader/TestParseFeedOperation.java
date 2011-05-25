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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationParameters;
import org.nuxeo.ecm.automation.core.util.StringList;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author <a href="mailto:ei@nuxeo.com">Eugen Ionica</a>
 *
 */
@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
@RepositoryConfig(type = BackendType.H2, user = "Administrator", cleanup = Granularity.METHOD)
@Deploy({ "org.nuxeo.rss.reader", "org.nuxeo.ecm.automation.core", "org.nuxeo.ecm.platform.userworkspace.types",
        "org.nuxeo.ecm.automation.features", "org.nuxeo.ecm.platform.query.api",
"org.nuxeo.ecm.platform.userworkspace.api", "org.nuxeo.ecm.platform.userworkspace.core" })
public class TestParseFeedOperation extends AbstractRSSFeedTestCase {

    @Inject
    AutomationService service;

    @Test
    public void testFeedProvider() throws Exception {
        OperationContext ctx = new OperationContext(session);
        assertNotNull(ctx);

        changeGadgetPreferencesValues(100, 100);

        URL url = this.getClass().getClassLoader().getResource("feed.rss");
        assertNotNull(url);

        OperationChain chain = new OperationChain("fakeChain");
        OperationParameters oparams = new OperationParameters(
                FeedProviderOperation.ID);
        oparams.set("urls", url.toExternalForm());
        chain.add(oparams);

        Blob result = (Blob) service.run(ctx, chain);

        JSONObject object = JSONObject.fromObject(result.getString());
        assertEquals("Feed title",
                object.get(FeedHelper.Field.FEED_TITLE.name()));
        JSONArray array = object.getJSONArray(FeedHelper.Field.ENTRIES.name());
        assertEquals(3, array.size());

        // test limit
        changeGadgetPreferencesValues(-1, 1);
        result = (Blob) service.run(ctx, chain);

        object = JSONObject.fromObject(result.getString());
        array = object.getJSONArray(FeedHelper.Field.ENTRIES.name());
        assertEquals(1, array.size());
    }

    @Test
    public void testMerge() throws Exception {
        OperationContext ctx = new OperationContext(session);
        assertNotNull(ctx);

        changeGadgetPreferencesValues(100, 100);

        URL url1 = this.getClass().getClassLoader().getResource("feed_1.rss");
        assertNotNull(url1);
        URL url2 = this.getClass().getClassLoader().getResource("feed_2.rss");
        assertNotNull(url2);

        StringList urls = new StringList();
        urls.add(url2.toExternalForm());
        urls.add(url1.toExternalForm());

        OperationChain chain = new OperationChain("fakeChain");
        OperationParameters oparams = new OperationParameters(
                FeedProviderOperation.ID);
        oparams.set("urls", urls);
        chain.add(oparams);

        Blob result = (Blob) service.run(ctx, chain);

        JSONObject object = JSONObject.fromObject(result.getString());
        JSONArray array = object.getJSONArray(FeedHelper.Field.ENTRIES.name());

        // check the merge
        assertEquals(6, array.size());

        // check the order
        for (int i = 0; i < 6; i++) {
            JSONObject entry = (JSONObject) array.get(i);
            assertEquals("title " + (i + 1),
                    entry.get(FeedHelper.Field.TITLE.name()));
        }

    }

}
