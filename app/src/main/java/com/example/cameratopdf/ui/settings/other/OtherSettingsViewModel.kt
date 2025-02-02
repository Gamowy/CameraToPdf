package com.example.cameratopdf.ui.settings.other

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first

class OtherSettingsViewModel : ViewModel() {
    private val _serverAddress = MutableLiveData("")
    val serverAddress: MutableLiveData<String> = _serverAddress

    private val _theme = MutableLiveData(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    val theme: MutableLiveData<Int> = _theme

    suspend fun setServerAddress(context: Context, value: String) {
        _serverAddress.value = value
        context.otherSettings.edit { settings ->
            settings[SERVER_ADDRESS] = _serverAddress.value ?: ""
        }
    }

    suspend fun setTheme(context: Context, value: Int) {
        _theme.value = value
        context.otherSettings.edit { settings ->
            settings[THEME] = _theme.value ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    suspend fun loadSettings(context: Context) {
        val settings = context.otherSettings.data.first()
        _serverAddress.value = settings[SERVER_ADDRESS] ?: ""
        _theme.value = settings[THEME] ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    companion object {
        val Context.otherSettings: DataStore<Preferences> by preferencesDataStore(name = "otherSettings")
        val SERVER_ADDRESS = stringPreferencesKey("server_address")
        val THEME = intPreferencesKey("theme")
    }
}