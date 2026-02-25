package com.example.playlistMaker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.appbar.MaterialToolbar
import java.net.URL

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        enableEdgeToEdge()

        setUpToolbar()
        setUpShareButton()
        setUpSupportButton()
        setUpUserAgreementButton()



    }

    private fun setUpToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.settings_screen_toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setUpShareButton() {
        val btnShareApp = findViewById<LinearLayout>(R.id.btnShareApp)
        val linkText = "https://practicum.yandex.ru/profile/android-developer/"
        btnShareApp.setOnClickListener {
            openUrlInBrowser(linkText)
        }
    }

    private fun setUpSupportButton() {
        val btnSupport = findViewById<LinearLayout>(R.id.btnWriteToSupport)
        val recipient = "fh279@yandex.ru"
        val subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
        val body = "Спасибо разработчикам и разработчицам за крутое приложение!"
        val encodedSubject = Uri.encode(subject)
        val encodedBody = Uri.encode(body)
        val uriString = "mailto:$recipient?subject=$encodedSubject&body=$encodedBody"
        val uri = uriString.toUri()

        btnSupport.setOnClickListener {
            sendEmail(uri)
        }
    }

    private fun setUpUserAgreementButton() {
        val btnUserAgreement = findViewById<LinearLayout>(R.id.btnUserAgreement)
        val link = "https://yandex.ru/legal/practicum_offer/ru/"
        btnUserAgreement.setOnClickListener {
            openUserAgreement(link.toUri())
        }


    }

    private fun openUrlInBrowser(url: String) {
        val uri = url.toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(packageManager) != null) {
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                showErrorMessage("Can't open the link")
            }
        } else {
            showErrorMessage("No app to open the link")
        }
    }

    private fun sendEmail(uri: Uri) {
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        if (intent.resolveActivity(packageManager) != null) {
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                showErrorMessage("Activity not found")
            }
        } else {
            showErrorMessage("There is no app to send email")
        }
    }


    private fun openUserAgreement(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(packageManager) != null) {
            try { startActivity(intent) } catch (e: ActivityNotFoundException) {
                showErrorMessage("Activity not found")
            }
        } else {
            showErrorMessage("There is no app to open page")
        }
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(
            /* context = */ this,
            /* text = */ message,
            /* duration = */ Toast.LENGTH_SHORT
        ).show()
    }
}
