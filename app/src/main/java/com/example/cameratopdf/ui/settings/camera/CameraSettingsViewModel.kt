package com.example.cameratopdf.ui.settings.camera

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.first

class CameraSettingsViewModel: ViewModel() {
    private val _photosPerDocument = MutableLiveData(5)
    val photosPerDocument: LiveData<Int> = _photosPerDocument

    private val _timeBetweenPhotos = MutableLiveData(5)
    val timeBetweenPhotos: LiveData<Int> = _timeBetweenPhotos

    private val _makeSoundBeforePhoto = MutableLiveData(true)
    val makeSoundBeforePhoto: LiveData<Boolean> = _makeSoundBeforePhoto

    private val _makeSoundAfterPhoto = MutableLiveData(true)
    val makeSoundAfterPhoto: LiveData<Boolean> = _makeSoundAfterPhoto

    private val _makeSoundAfterAllPhotos = MutableLiveData(true)
    val makeSoundAfterAllPhotos: LiveData<Boolean> = _makeSoundAfterAllPhotos

    suspend fun setPhotosPerDocument(context: Context, value: Int) {
        _photosPerDocument.value = value
        context.cameraSettings.edit { settings ->
            settings[PHOTOS_PER_DOCUMENT] = _photosPerDocument.value ?: 5
        }
    }

    suspend fun setTimeBetweenPhotos(context: Context, value: Int) {
        _timeBetweenPhotos.value = value
        context.cameraSettings.edit { settings ->
            settings[TIME_BETWEEN_PHOTOS] = _timeBetweenPhotos.value ?: 5
        }
    }

    suspend fun setMakeSoundBeforePhoto(context: Context, value: Boolean) {
        _makeSoundBeforePhoto.value = value
        context.cameraSettings.edit { settings ->
            settings[MAKE_SOUND_BEFORE_PHOTO] = _makeSoundBeforePhoto.value ?: true
        }
    }

    suspend fun setMakeSoundAfterPhoto(context: Context, value: Boolean) {
        _makeSoundAfterPhoto.value = value
        context.cameraSettings.edit { settings ->
            settings[MAKE_SOUND_AFTER_PHOTO] = _makeSoundAfterPhoto.value ?: true
        }
    }

    suspend fun setMakeSoundAfterAllPhotos(context: Context, value: Boolean) {
        _makeSoundAfterAllPhotos.value = value
        context.cameraSettings.edit { settings ->
            settings[MAKE_SOUND_AFTER_ALL_PHOTOS] = _makeSoundAfterAllPhotos.value ?: true
        }
    }

    suspend fun loadSettings(context: Context) {
        val settings = context.cameraSettings.data.first()
        _photosPerDocument.value = settings[PHOTOS_PER_DOCUMENT] ?: 5
        _timeBetweenPhotos.value = settings[TIME_BETWEEN_PHOTOS] ?: 5
        _makeSoundBeforePhoto.value = settings[MAKE_SOUND_BEFORE_PHOTO] ?: true
        _makeSoundAfterPhoto.value = settings[MAKE_SOUND_AFTER_PHOTO] ?: true
        _makeSoundAfterAllPhotos.value = settings[MAKE_SOUND_AFTER_ALL_PHOTOS] ?: true
    }

    companion object {
        val Context.cameraSettings: DataStore<Preferences> by preferencesDataStore(name = "cameraSettings")
        val PHOTOS_PER_DOCUMENT = intPreferencesKey("photos_per_document")
        val TIME_BETWEEN_PHOTOS = intPreferencesKey("time_between_photos")
        val MAKE_SOUND_BEFORE_PHOTO = booleanPreferencesKey("make_sound_before_photo")
        val MAKE_SOUND_AFTER_PHOTO = booleanPreferencesKey("make_sound_after_photo")
        val MAKE_SOUND_AFTER_ALL_PHOTOS = booleanPreferencesKey("make_sound_after_all_photos")
    }
}