function UniversalAnalyticsPlugin() {};

UniversalAnalyticsPlugin.prototype.startTrackerWithId = function(id) {
	cordova.exec(function() {}, function() {}, 'UniversalAnalytics', 'startTrackerWithId', [id]);
};

UniversalAnalyticsPlugin.prototype.trackView = function(screen) {
	cordova.exec(function() {}, function() {}, 'UniversalAnalytics', 'trackView', [screen]);
};

UniversalAnalyticsPlugin.prototype.addCustomDimension = function(key, value) {
	cordova.exec(function() {}, function() {}, 'UniversalAnalytics', 'addCustomDimension', [key, value]);
};

UniversalAnalyticsPlugin.prototype.trackEvent = function(category, action, label, value) {
	cordova.exec(function() {}, function() {}, 'UniversalAnalytics', 'trackEvent', [category, action, label, value]);
};

UniversalAnalyticsPlugin.prototype.trackException = function(description, fatal) {
  cordova.exec(function() {}, function() {}, 'UniversalAnalytics', 'trackException', [description, fatal]);
};

module.exports = new UniversalAnalyticsPlugin();

