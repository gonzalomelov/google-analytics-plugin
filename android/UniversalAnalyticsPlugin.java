package com.danielcwilson.plugins.analytics;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger.LogLevel;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.google.analytics.tracking.android.ExceptionReporter;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map.Entry;
import java.lang.Thread.UncaughtExceptionHandler;

public class UniversalAnalyticsPlugin extends CordovaPlugin {
    public static final String START_TRACKER = "startTrackerWithId";
    public static final String START_TRACKER_WITH_OPTIONS = "startTracker";
    public static final String ADD_DIMENSION = "addCustomDimension";
    public static final String TRACK_VIEW = "trackView";
    public static final String TRACK_EVENT = "trackEvent";
    public static final String TRACK_EXCEPTION = "trackException";

    public Boolean trackerStarted = false;
    public HashMap<String, String> customDimensions = new HashMap<String, String>();

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (START_TRACKER_WITH_OPTIONS.equals(action)) {
            String id = args.getString(0);
            Boolean trackUncaughtExceptions = args.isNull(1) ? null : args.getBoolean(1);
            Integer trackInterval = args.isNull(2) ? null : args.getInt(2);
            Boolean dryRun = args.isNull(3) ? null : args.getBoolean(3);
            this.startTracker(id, trackUncaughtExceptions, trackInterval, dryRun, callbackContext);
            return true;
        } else if (START_TRACKER.equals(action)) {
            String id = args.getString(0);
            this.startTracker(id, callbackContext);
            return true;
        } else if (ADD_DIMENSION.equals(action)) {
            String key = args.getString(0);
            String value = args.getString(1);
            this.addCustomDimension(key, value, callbackContext);
            return true;
        } else if (TRACK_VIEW.equals(action)) {
            String screen = args.getString(0);
            this.trackView(screen, callbackContext);
            return true;
        } else if (TRACK_EVENT.equals(action)) {
            int length = args.length();
            if (length > 0) {
                this.trackEvent(
                    args.getString(0), 
                    length > 1 ? args.getString(1) : "", 
                    length > 2 ? args.getString(2) : "", 
                    length > 3 ? args.getLong(3) : 0, 
                    callbackContext);
            }
            return true;
        } else if (TRACK_EXCEPTION.equals(action)) {
            String description = args.getString(0);
            boolean fatal = args.getBoolean(1);
            this.trackException(description, fatal, callbackContext);
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private void startTracker(String id, Boolean trackUncaughtExceptions, Integer trackInterval, Boolean dryRun, CallbackContext callbackContext) {
        if (null != id && id.length() > 0) {
            Tracker tracker = GoogleAnalytics.getInstance(this.cordova.getActivity()).getTracker(id);
            if (trackUncaughtExceptions) {
                UncaughtExceptionHandler exceptionHandler = new ExceptionReporter(
                    tracker,
                    GAServiceManager.getInstance(),
                    Thread.getDefaultUncaughtExceptionHandler(),
                    this.cordova.getActivity());
                Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
            }
            if (dryRun != null) {
                GoogleAnalytics.getInstance(this.cordova.getActivity()).setDryRun(dryRun);
            }
            if (trackInterval == null) {
                trackInterval = 30; //deprecated but whatcha gonna do? set dispatch period to 30 sec
            }
            GAServiceManager.getInstance().setLocalDispatchPeriod(trackInterval); //deprecated but whatcha gonna do? set dispatch period to 30 sec
            callbackContext.success("tracker started");
            trackerStarted = true;
         // Set the log level to verbose.
          /*GoogleAnalytics.getInstance(this.cordova.getActivity()).getLogger()
                .setLogLevel(LogLevel.VERBOSE);*/
        } else {
            callbackContext.error("tracker id is not valid");
        }
    }

    @SuppressWarnings("deprecation")
    private void startTracker(String id, CallbackContext callbackContext) {
        this.startTracker(id, null, null, null, callbackContext);
    }

    private void addCustomDimension(String key, String value, CallbackContext callbackContext) {
        if (null != key && key.length() > 0 && null != value && value.length() > 0) {
            customDimensions.put(key, value);
        } else {
            callbackContext.error("Expected non-empty string arguments.");
        }
    }
    
    private void addCustomDimensionsToTracker(Tracker tracker) {
        for (Entry<String, String> entry : customDimensions.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            //System.out.println("Setting tracker dimension slot " + key + ": <" + value+">");
            tracker.set(Fields.customDimension(Integer.parseInt(key)), value);
        }
    }

    private void trackView(String screenname, CallbackContext callbackContext) {
    if (! trackerStarted ) {
            callbackContext.error("Tracker not started");
        return;
    }

        Tracker tracker = GoogleAnalytics.getInstance(this.cordova.getActivity()).getDefaultTracker();
        addCustomDimensionsToTracker(tracker);
        
        if (null != screenname && screenname.length() > 0) {
            tracker.set(Fields.SCREEN_NAME, screenname);
            tracker.send(MapBuilder
              .createAppView()
              .build()
            );
            callbackContext.success("Track Screen: " + screenname);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void trackEvent(String category, String action, String label, long value, CallbackContext callbackContext) {
        if (! trackerStarted ) {
            callbackContext.error("Tracker not started");
            return;
        }

        Tracker tracker = GoogleAnalytics.getInstance(this.cordova.getActivity()).getDefaultTracker();
        addCustomDimensionsToTracker(tracker);

        if (null != category && category.length() > 0) {
            tracker.send(MapBuilder
                .createEvent(category, action, label, value)
                .build()
            );
            callbackContext.success("Track Event: " + category);
        } else {
            callbackContext.error("Expected non-empty string arguments.");
        }
    }

    private void trackException(String description, boolean fatal, CallbackContext callbackContext) {
        if (!trackerStarted) {
            callbackContext.error("Tracker not started");
            return;
        }

        if (description == null || description.length() <= 100) {
            Tracker tracker = GoogleAnalytics.getInstance(this.cordova.getActivity()).getDefaultTracker();
            tracker.send(MapBuilder
                    .createException(description, fatal)
                    .build()
            );
            callbackContext.success("Track Exception: " + description);
        } else {
            callbackContext.error("Expected description up to 100 characters.");
        }
    }
}

