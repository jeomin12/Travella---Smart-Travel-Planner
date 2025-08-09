package com.travelassistant.travella.utils


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionManager(private val context: Context) {

    companion object {
        const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        const val STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
        const val NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS
    }

    fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            LOCATION_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            STORAGE_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isNotificationPermissionGranted(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                NOTIFICATION_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Notifications are automatically granted on older versions
        }
    }

    fun getPermissionStatus(): Map<String, Boolean> {
        return mapOf(
            "Location" to isLocationPermissionGranted(),
            "Camera" to isCameraPermissionGranted(),
            "Storage" to isStoragePermissionGranted(),
            "Notifications" to isNotificationPermissionGranted()
        )
    }

    fun getRequiredPermissions(): Array<String> {
        val permissions = mutableListOf(
            LOCATION_PERMISSION,
            CAMERA_PERMISSION,
            STORAGE_PERMISSION
        )

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions.add(NOTIFICATION_PERMISSION)
        }

        return permissions.toTypedArray()
    }
}