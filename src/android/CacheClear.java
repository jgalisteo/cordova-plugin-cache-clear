package com.anrip.cordova;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.util.Log;

@TargetApi(19)
public class CacheClear extends CordovaPlugin {
    private static final String LOG_TAG = "CacheClear";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("task")) {
            Log.v(LOG_TAG, "Cordova Android CacheClear() called.");
            task(callbackContext);
            return true;
        }
        return false;
    }

    public void task(CallbackContext callbackContext) {
        final CacheClear self = this;
        final CallbackContext callback = callbackContext;

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    // clear the cache
                    self.webView.clearCache(true);
                    self.clearApplicationData();
                    // send success result to cordova
                    PluginResult result = new PluginResult(PluginResult.Status.OK);
                    result.setKeepCallback(false);
                    callback.sendPluginResult(result);
                } catch (Exception e) {
                    String msg = "Error while clearing webview cache.";
                    Log.e(LOG_TAG, msg);
                    // return error answer to cordova
                    PluginResult result = new PluginResult(PluginResult.Status.ERROR, msg);
                    result.setKeepCallback(false);
                    callback.sendPluginResult(result);
                }
            }
        });
    }

    // http://www.hrupin.com/2011/11/how-to-clear-user-data-in-your-android-application-programmatically
    private void clearApplicationData() {
      File cache = this.cordova.getActivity().getCacheDir();
      File appDir = new File(cache.getParent());
      Log.i(LOG_TAG, "Absolute path: " + appDir.getAbsolutePath());
      if (appDir.exists()) {
        String[] children = appDir.list();
        for (String s : children) {
          if (!s.equals("lib")) {
            deleteDir(new File(appDir, s));
            Log.i(LOG_TAG, "File /data/data/APP_PACKAGE/" + s + " DELETED");
          }
        }
      }
    }

    private static boolean deleteDir(File dir) {
      Log.i(LOG_TAG, "Deleting: " + dir.getAbsolutePath());
      if (dir != null && dir.isDirectory()) {
        String[] children = dir.list();
        for (int i = 0; i < children.length; i++) {
          boolean success = deleteDir(new File(dir, children[i]));
          if (!success) {
            return false;
          }
        }
      }

      return dir.delete();
    }

}
