package com.glink.inspect.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

public class SpConfig {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public SpConfig(Context context) {
        this.preferences = this.getSharedPreferences(context);
        this.editor = this.preferences.edit();
        this.editor.commit();
    }

    protected SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean putString(String entry, String value, boolean commit) {
        if (this.preferences == null) {
            return false;
        } else {
            this.editor.putString(entry, value);
            return commit ? this.editor.commit() : true;
        }
    }

    public boolean putString(String entry, String value) {
        return this.putString(entry, value, true);
    }

    public boolean putInt(String entry, int value, boolean commit) {
        if (this.preferences == null) {
            return false;
        } else {
            this.editor.putInt(entry, value);
            return commit ? this.editor.commit() : true;
        }
    }

    public boolean putInt(String entry, int value) {
        return this.putInt(entry, value, true);
    }

    public boolean putFloat(String entry, float value, boolean commit) {
        if (this.preferences == null) {
            return false;
        } else {
            this.editor.putFloat(entry, value);
            return commit ? this.editor.commit() : true;
        }
    }

    public boolean putFloat(String entry, float value) {
        return this.putFloat(entry, value, true);
    }

    public boolean putLong(String entry, long value, boolean commit) {
        if (this.preferences == null) {
            return false;
        } else {
            this.editor.putLong(entry, value);
            return commit ? this.editor.commit() : true;
        }
    }

    public boolean putLong(String entry, long value) {
        return this.putLong(entry, value, true);
    }

    public boolean putBoolean(String entry, boolean value, boolean commit) {
        if (this.preferences == null) {
            return false;
        } else {
            this.editor.putBoolean(entry, value);
            return commit ? this.editor.commit() : true;
        }
    }

    public boolean putBoolean(String entry, boolean value) {
        return this.putBoolean(entry, value, true);
    }

    public Map<String, ?> getAll() {
        if (this.preferences == null) {
            return null;
        } else {
            try {
                return this.preferences.getAll();
            } catch (Exception var2) {
                var2.printStackTrace();
                return null;
            }
        }
    }

    public String getString(String entry, String defaultValue) {
        if (this.preferences == null) {
            return defaultValue;
        } else {
            try {
                return this.preferences.getString(entry, defaultValue);
            } catch (Exception var4) {
                var4.printStackTrace();
                return defaultValue;
            }
        }
    }

    public int getInt(String entry, int defaultValue) {
        if (this.preferences == null) {
            return defaultValue;
        } else {
            try {
                return this.preferences.getInt(entry, defaultValue);
            } catch (Exception var4) {
                var4.printStackTrace();
                return defaultValue;
            }
        }
    }

    public float getFloat(String entry, float defaultValue) {
        if (this.preferences == null) {
            return defaultValue;
        } else {
            try {
                return this.preferences.getFloat(entry, defaultValue);
            } catch (Exception var4) {
                var4.printStackTrace();
                return defaultValue;
            }
        }
    }

    public long getLong(String entry, long defaultValue) {
        if (this.preferences == null) {
            return defaultValue;
        } else {
            try {
                return this.preferences.getLong(entry, defaultValue);
            } catch (Exception var5) {
                var5.printStackTrace();
                return defaultValue;
            }
        }
    }

    public boolean getBoolean(String entry, boolean defaultValue) {
        if (this.preferences == null) {
            return defaultValue;
        } else {
            try {
                return this.preferences.getBoolean(entry, defaultValue);
            } catch (Exception var4) {
                var4.printStackTrace();
                return defaultValue;
            }
        }
    }

    public boolean commit() {
        return this.preferences == null ? false : this.editor.commit();
    }

    public boolean contains(String entry) {
        return this.preferences == null ? false : this.preferences.contains(entry);
    }

    public boolean clear(String entry) {
        if (this.preferences == null) {
            return false;
        } else {
            this.editor.remove(entry);
            return this.editor.commit();
        }
    }

    public boolean clearAll() {
        if (this.preferences == null) {
            return false;
        } else {
            this.editor.clear();
            return this.editor.commit();
        }
    }
}
