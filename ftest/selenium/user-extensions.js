// set absolute path to current folder here, used in tests with file upload
Selenium.prototype.doRetrieveTestFolderPath = function() {
  storedVars['testfolderpath'] = "/Users/nuxeo/Nuxeo/nuxeo-addons/nuxeo-rss-reader/ftest/selenium/data/";
};

// override default method to make sure privilege to type file path is enabled
Selenium.prototype.doType = function(locator, value) {
   /**
   * Sets the value of an input field, as though you typed it in.
   *
   * <p>Can also be used to set the value of combo boxes, check boxes, etc. In these cases,
   * value should be the value of the option selected, not the visible text.</p>
   *
   * @param locator an <a href="#locators">element locator</a>
   * @param value the value to type
   */
   // this is the added line here:
   netscape.security.PrivilegeManager.enablePrivilege("UniversalFileRead");
   if (this.browserbot.controlKeyDown || this.browserbot.altKeyDown || this.browserbot.metaKeyDown) {
        throw new SeleniumError("type not supported immediately after call to controlKeyDown() or altKeyDown() or metaKeyDown()");
   }
   // TODO fail if it can't be typed into.
   var element = this.browserbot.findElement(locator);
   if (this.browserbot.shiftKeyDown) {
       value = new String(value).toUpperCase();
   }
   this.browserbot.replaceText(element, value);
};


// ajax4jsf testing helper inspired from
// http://codelevy.com/articles/2007/11/05/selenium-and-ajax-requests
/**
 * Registers with the a4j library to record when an Ajax request
 * finishes.
 *
 * Call this after the most recent page load but before any Ajax requests.
 *
 * Once you've called this for a page, you should call waitForA4jRequest at
 * every opportunity, to make sure the A4jRequestFinished flag is consumed.
 */
Selenium.prototype.doWatchA4jRequests = function() {
  var testWindow = selenium.browserbot.getCurrentWindow();
  // workaround for Selenium IDE 1b2 bug, see
  // http://clearspace.openqa.org/message/46135
  if (testWindow.wrappedJSObject) {
      testWindow = testWindow.wrappedJSObject;
  }
  Selenium.A4jRequestFinished = false;
  Selenium.ActiveA4jRequestCount = 0;
  testWindow.A4J.AJAX.AddListener({
    onbeforeajax: function() {
      Selenium.ActiveA4jRequestCount++;
    }
  });
  testWindow.A4J.AJAX.AddListener({
    onafterajax: function() {
      Selenium.ActiveA4jRequestCount--;
      if (Selenium.ActiveA4jRequestCount == 0) {
        Selenium.A4jRequestFinished = true;
      }
    }
  });
}

/**
 * If you've set up with watchA4jRequests, this routine will wait until
 * an Ajax request has finished and then return.
 */
Selenium.prototype.doWaitForA4jRequest = function(timeout) {
  return Selenium.decorateFunctionWithTimeout(function() {
    if (Selenium.A4jRequestFinished) {
      Selenium.A4jRequestFinished = false;
      return true;
    }
    return false;
  }, timeout);
}

Selenium.A4jRequestFinished = false;
Selenium.ActiveA4jRequestCount = 0;


// wait for jquery and/or prototype calls
Selenium.prototype.doWaitForJSQueries = function(timeout) {
  return Selenium.decorateFunctionWithTimeout(function() {
    var testWindow = selenium.browserbot.getCurrentWindow();
    if (testWindow.wrappedJSObject) {
        testWindow = testWindow.wrappedJSObject;
    }
    if (testWindow.jQuery.active == 0 && testWindow.Ajax.activeRequestCount == 0) {
      return true;
    }
    return false;
  }, timeout);
}
