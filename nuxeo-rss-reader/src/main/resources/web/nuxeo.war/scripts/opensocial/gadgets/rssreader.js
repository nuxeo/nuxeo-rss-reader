var prefs = new gadgets.Prefs();

var opCallParameters = {
    operationId : 'Feed.Provider',
    operationParams : {
     limit : 4
    },
    entityType : 'blob',
    operationContext : {},
    operationCallback : renderFeed
};

// main method ; called after operation is executed
function renderFeed(response, opCallParameters) {
    _gel("feedEntries").innerHTML = buildFeed(response);
    gadgets.window.adjustHeight();
    window.parent.nuxeo.rssReader.addFancyBoxTo(jQuery(".feedItemPopup"));
    window.parent.nuxeo.rssReader.addFancyBoxTo(jQuery(".feedConfigPopup"), loadFeedItems);
}

function buildFeed(response) {
    var feed_title = response.data["FEED_TITLE"];
    var feed_link = response.data["FEED_LINK"];
    var entries = response.data["ENTRIES"];
    if ( typeof entries == 'undefined' ) {
        return '<p>No entries</p>';
    }
    var feed = '';

    for (var i = 0; i < entries.length; i++) {
        var entry = entries[i];

        // handle the case when more feed are merged
        var entry_feed_title = entry["FEED_TITLE"];
        var entry_feed_link = entry["FEED_LINK"];
        if ( typeof entry_feed_title == 'undefined' ) {
            entry_feed_title = feed_title;
        }
        if ( typeof entry_feed_link == 'undefined' ) {
            entry_feed_link = feed_link;
        }
        // end -

        feed += buildEntry(entries[i], entry_feed_title, entry_feed_link);
    }
    return feed;
}

function buildEntry(entryData, feed_title, feed_link) {
    var entry = '';
    entry += '<div class="entry">';

    entry += '<div class="title">';

    var entry_id = entryData["URI"] != null ? entryData["URI"] : entryData["LINK"];
    entry += '<a class="feedItemPopup" href="' + NXGadgetContext.clientSideBaseUrl + 'site/rssreader/item?i='+ entry_id + '&language=' + prefs.getLang() + '">';
    entry += entryData["TITLE"];
    entry += '</a>'
    entry += '</div>';


    entry += '<div class="entryInfo">';
    if (entryData["PUBDATE"] == null) {
      entry += buildDate(entryData["PUBDATE"]) + ' - ';
    }
    entry += '<a class="source" href="'+ feed_link + '" target="_blank">';
    entry += feed_title;
    entry += '</a>';
    entry += '</div>';

    entry += '<div class="description">';
    entry += entryData["DESCRIPTION"];
    entry += '</div>';

    entry += '</div>';
    return entry;
}

function buildDate(dataInfo) {
    var d = new Date(dataInfo["time"]);
    return d.getDate() + '/' + ( d.getMonth() + 1 ) + '/' + d.getFullYear();
}

gadgets.util.registerOnLoadHandler(loadFeedItems);

function loadFeedItems() {
    buildSettingsButton();
    doAutomationRequest(opCallParameters);
}

function buildSettingsButton() {
    var link = '<a class="feedConfigPopup" href="' + NXGadgetContext.clientSideBaseUrl + 'site/rssreader/config?language=' + prefs.getLang() + '">' + prefs.getMsg('label.context.settings') + '</a>';
    _gel("actions").innerHTML = link;
}
