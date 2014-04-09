function UniversalAnalyticsPlugin() {};

UniversalAnalyticsPlugin.prototype.startTrackerWithId = function(id, callbackContext) {
  callbackContext = callbackContext || {};
  cordova.exec(callbackContext.success || null, callbackContext.error || null, 'UniversalAnalytics', 'startTrackerWithId', [id]);
};

UniversalAnalyticsPlugin.prototype.trackView = function(screen, callbackContext) {
  callbackContext = callbackContext || {};
  cordova.exec(callbackContext.success || null, callbackContext.error || null, 'UniversalAnalytics', 'trackView', [screen]);
};

UniversalAnalyticsPlugin.prototype.addCustomDimension = function(key, value, callbackContext) {
  callbackContext = callbackContext || {};
  cordova.exec(callbackContext.success || null, callbackContext.error || null, 'UniversalAnalytics', 'addCustomDimension', [key, value]);
};

UniversalAnalyticsPlugin.prototype.trackEvent = function(category, action, label, value, callbackContext) {
  callbackContext = callbackContext || {};
  cordova.exec(callbackContext.success || null, callbackContext.error || null, 'UniversalAnalytics', 'trackEvent', [category, action, label, value]);
};

UniversalAnalyticsPlugin.prototype.trackException = function(description, fatal, callbackContext) {
  callbackContext = callbackContext || {};
  cordova.exec(callbackContext.success || null, callbackContext.error || null, 'UniversalAnalytics', 'trackException', [description, fatal]);
};

module.exports = new UniversalAnalyticsPlugin();

