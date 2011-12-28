<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
  "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
  <title>Nuxeo RSS Reader Configuration</title>

  <link rel="stylesheet" type="text/css"
    href="${skinPath}/css/feed_configuration.css"/>

  <script type="text/javascript"
    src="${skinPath}/script/jquery/jquery.js"></script>

  <script type="text/javascript">
    function submitForm() {
      var selectedOption = $("#feedId option:selected");
      var formName = "addGlobalFeedForm";
      if (selectedOption.length == 0 || selectedOption.val() === "none") {
        formName = "addNewFeedForm";
      }
      document.forms[formName].submit();
    }

    function closePopUp() {
      parent.jQuery.fancybox.close();
    }

    $(document).ready(function () {
      $(".field").focus(function () {
        $("#feedId option:first").attr("selected", true);
      });
      $(".field").click(function () {
        $(this).focus();
        $(this).select();
      });
    });

    function confirmDeleteFeed() {
      return confirm("${Context.getMessage('label.rss.feed.configuration.feed.remove.confirm')}");
    }
  </script>
</head>
<body>

<div class="feedBlock">
  <h3>${Context.getMessage('label.rss.feed.configuration.title')}</h3>

  <div class="popupContent">
    <div class="addFeedBlock">
      <h4>${Context.getMessage('label.rss.feed.configuration.add.feed.title')}</h4>
    <#if globalFeeds?has_content>
      <form id="addGlobalFeedForm" method="POST" action="addGlobalFeed">
        <select id="feedId" name="feedId" size="0">
          <option
            value="none">${Context.getMessage('label.rss.feed.configuration.select.defined.feed')}</option>
          <#list globalFeeds as feed>
            <option value="${feed.id}">${feed["dc:title"]}</option>
          </#list>
        </select>
      </form>
      <button class="button smallButton"
        <#if ableToCreateNew>onclick="submitForm()"
        <#else>disabled="disabled" </#if> >${Context.getMessage('label.rss.feed.configuration.feed.add')}</button>
      <span>${Context.getMessage('label.rss.feed.configuration.add.custom.feed')}</span>
    </#if>

      <form id="addNewFeedForm" method="POST" action="addNewFeed">
        <input class="field" type="text" name="feedName"
          value="${Context.getMessage('label.rss.feed.configuration.custom.feed.title')}"/>
        <input class="field feedlink" type="text" name="feedLink"
          value="${Context.getMessage('label.rss.feed.configuration.custom.feed.link')}"/>
      </form>
      <button class="button smallButton"
      <#if ableToCreateNew>onclick="submitForm()"
      <#else>disabled="disabled" </#if> >${Context.getMessage('label.rss.feed.configuration.feed.add')}</button>
    </div>

    <div class="listFeedBlock">
      <h4>${Context.getMessage('label.rss.feed.configuration.receiving.feeds.title1')} ${userFeeds?size} ${Context.getMessage('label.rss.feed.configuration.receiving.feeds.title2')}</h4>

      <p
        class="detail">${Context.getMessage('label.rss.feed.configuration.feeds.limit1')} ${maxFeedsCount}${Context.getMessage('label.rss.feed.configuration.feeds.limit2')}</p>

      <table class="dataList">
        <tbody>
        <#list userFeeds as feed>
        <tr>
          <td class="iconColumn"><img src="${skinPath}/icons/rss.png"
            alt="RSS"/></td>
          <td class="feedName">${feed["dc:title"]}</td>
          <td>${feed["rf:rss_address"]}</td>
          <td class="actionColumn">
            <form method="POST" action="removeFeed">
              <input type="hidden" name="id" value="${feed.id}"/>
              <input class="button  smallButton" type="submit"
                onclick="if( !confirmDeleteFeed() ) return false;"
                value="${Context.getMessage('label.rss.feed.configuration.feed.remove')}"/>
            </form>
          </td>
        </tr>
        </#list>
        </tbody>
      </table>
    </div>
  </div>

  <div class="buttonsGadget">
    <button class="button"
      onclick="javascript:closePopUp()">${Context.getMessage('label.rss.feed.configuration.close')}</button>
  </div>

</body>
</html>
