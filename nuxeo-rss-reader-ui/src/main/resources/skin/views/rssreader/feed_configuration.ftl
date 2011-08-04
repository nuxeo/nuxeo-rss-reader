<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="${skinPath}/css/feed_configuration.css" />
<title>Nuxeo RSS Reader Configuration</title>
<script type="text/javascript" >
function submitForm(formName) {
	document.forms[formName].submit();
}
function closePopUp() {
	parent.jQuery.fancybox.close();
}

</script>
</head>
<body>

<div class="feedBlock">
Current Feeds:
<ul>

<#list userFeeds as feed>
<li>
	<a href="removeFeed?id=${feed.id}" class="removeFeed"><img src="${skinPath}/icons/remove.png" alt=""/></a>
	${feed["dc:title"]}- <span>${feed["rf:rss_address"]}</span>
</li>
</#list>
</ul>

<#if ableToCreateNew>
  <#if globalFeeds?has_content>
  <form id="addGlobaFeedForm" method="GET" action="addGlobalFeed">
  Add a defined feed:
  <div class="fieldRow">
  <select name="feedId" size="0">
  <#list globalFeeds as feed>
  <option value="${feed.id}">${feed["dc:title"]}</option>
  </#list>
  </select>
  <a href="javascript: submitForm('addGlobaFeedForm')" class="addGlobalFeed"><img src="${skinPath}/icons/add.png" alt="" /></a>
  </div>
  </form>
  </#if>

  Add a new feed:
  <form id="addNewFeedForm" method="GET" action="addNewFeed">
  <div class="fieldRow">
  <input class="field" type="text" name="feedName" value="Feed Title" />
  <input class="field feedlink" type="text" name="feedLink" value="Feed link" />
  <a href="javascript: submitForm('addNewFeedForm')" class="addNewFeed"><img src="${skinPath}/icons/add.png" alt="" /></a>
  </div>
  </div>
  </form>
</#if>


<div class="submitBar">
<input type="button" value="Done" onclick="javascript: closePopUp()"/>
</div>

</body>
</html>
