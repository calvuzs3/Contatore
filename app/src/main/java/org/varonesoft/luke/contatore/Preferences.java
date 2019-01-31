package org.varonesoft.luke.contatore;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.varonesoft.luke.countdown.Counter;

/**
 * Manage preferences
 * <p>
 * Also the feature to save the last counter mCount
 * At the beginning it fetcehs the value
 */
final class Preferences {

    private static final String KEY_LAST_COUNTER_USED = "preference_last_counter";
    private static final long VALUE_LAST_COUNTER_USED = Counter.ONEMINUTEMILLIS;

    private static final String KEY_PLAYSOUNDS = "preference_play_sounds";
    private static final boolean VALUE_PLAYSOUNDS = true;

    // Returns LAST VALUE
    static long getLastCounterUsed(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(KEY_LAST_COUNTER_USED, VALUE_LAST_COUNTER_USED);
    }

    // Returns PLAYSOUND
    static boolean getPlaySoundPreference(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(KEY_PLAYSOUNDS, VALUE_PLAYSOUNDS);
    }

    // Save LAST VALUE
    static void setLastCounterUsed(Context context, long value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(KEY_LAST_COUNTER_USED, value)
                .apply();
    }

    // Save PLAYSOUND
    static void setPlaySoundPreference(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_PLAYSOUNDS, value)
                .apply();
    }
}
