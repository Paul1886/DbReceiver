package com.example.dbreceiver

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED

class MainActivity : AppCompatActivity() {

    private val authority = "com.yourpackagename"
    private val tableName = "user_table"
    private val uri: Uri = Uri.Builder()
        .scheme("content")
        .authority(authority)
        .appendPath(tableName)
        .build()

    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textViewNames = findViewById<TextView>(R.id.textViewNames)

        // Check if permission is already granted
        if (checkPermission()) {
            loadData(textViewNames)
        } else {
            // Request the necessary permissions
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean {
        // Check if you have the necessary permissions to access the Content Provider
        // Implement your permission check logic here
        val readPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return readPermission == PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                loadData(findViewById(R.id.textViewNames))
            } else {
                // Permission denied
                // Handle the denial case
            }
        }
    }

    private fun loadData(textView: TextView) {
        val projection = arrayOf("companyName")

        Log.d("MainActivity", "URI used for data query: $uri")

        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use { cursor ->
            val names = mutableListOf<String>()
            val nameIndex = cursor.getColumnIndexOrThrow("companyName")
            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                names.add(name)
            }

            val concatenatedNames = names.joinToString(", ")
            textView.text = concatenatedNames
            Log.d("MainActivity", "Data retrieved: $concatenatedNames")
        } ?: Log.e("MainActivity", "Cursor is null")
    }
}
