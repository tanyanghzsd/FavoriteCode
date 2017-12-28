package utils.preference;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by tanyang on 17-12-28.
 */

public class PreferenceUtil implements SharedPreferences, SharedPreferences.Editor{
    private static final String DEFAULT_PREF = "default_pref";
    private static final byte[] MUTEX = new byte[0];
    private static HashMap<String, PreferenceUtil> sPrefMap = new HashMap<String, PreferenceUtil>();
    private Context mContext;

    private XSharedPreferences mSp;
    private Editor mEditor;
    private String mName;

    private PreferenceUtil(Context context,String name) {
        mContext = context;
        mName = name;
        mSp = XSharedPreferences.getSharedPreferences(mContext, name, Context.MODE_PRIVATE);
        mEditor = mSp.edit();
    }


    /**
     * 获取Preference实例
     * @param name
     * @return
     */
    public static synchronized PreferenceUtil getPreference(Context context, String name) {
        PreferenceUtil pref = sPrefMap.get(name);
        if (pref == null) {
            pref = new PreferenceUtil(context,name);
            sPrefMap.put(name, pref);
        }
        return pref;
    }

    /**
     * 获取默认的Preference实例
     * @return
     */
    public static PreferenceUtil getPreference(Context context) {
        return getPreference(context,DEFAULT_PREF);
    }


    @Override
    public Map<String, ?> getAll() {
        return mSp.getAll();
    }

    @Override
    public String getString(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return mSp.getStringSet(key, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return mSp.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return mSp.getFloat(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return mSp.contains(key);
    }

    @Override
    public Editor edit() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        mSp.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        mSp.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public Editor putString(String key, String value) {
        return mEditor.putString(key, value);
    }

    @Override
    public Editor putStringSet(String key, Set<String> values) {
        return mEditor.putStringSet(key, values);
    }

    @Override
    public Editor putInt(String key, int value) {
        return mEditor.putInt(key, value);
    }

    @Override
    public Editor putLong(String key, long value) {
        return mEditor.putLong(key, value);
    }

    @Override
    public Editor putFloat(String key, float value) {
        return mEditor.putFloat(key, value);
    }

    @Override
    public Editor putBoolean(String key, boolean value) {
        return mEditor.putBoolean(key, value);
    }

    @Override
    public Editor remove(String key) {
        return mEditor.remove(key);
    }

    @Override
    public Editor clear() {
        return mEditor.clear();
    }

    @Override
    public boolean commit() {
        if (android.os.Build.VERSION.SDK_INT >= 9) {
            mEditor.apply();
            return true;
        } else {
            return mEditor.commit();
        }
    }

    public boolean commit(boolean async) {
        if (async) {
            return commit();
        } else {
            return mEditor.commit();
        }
    }

    @Override
    public void apply() {
        mEditor.apply();
    }
}
