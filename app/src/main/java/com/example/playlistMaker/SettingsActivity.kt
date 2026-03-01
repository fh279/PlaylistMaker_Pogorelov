package com.example.playlistMaker

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.appbar.MaterialToolbar

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
        val recipient = this.getString(R.string.email_recipient_text)
        val subject = this.getString(R.string.email_subject_text)
        val body = this.getString(R.string.email_body_text)
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
        val link = this.getString(R.string.user_agreement_link)
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
                showErrorMessage(this.getString(R.string.cant_open_the_page_toast_text))
            }
        } else {
            showErrorMessage(this.getString(R.string.no_app_to_open_page_toast_text))
        }
    }

    private fun sendEmail(uri: Uri) {
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        if (intent.resolveActivity(packageManager) != null) {
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                showErrorMessage(this.getString(R.string.activity_not_found_toast_text))
            }
        } else {
            showErrorMessage(this.getString(R.string.no_app_to_sent_email_toast_text))
        }
    }


    private fun openUserAgreement(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(packageManager) != null) {
            try { startActivity(intent) } catch (e: ActivityNotFoundException) {
                showErrorMessage(this.getString(R.string.activity_not_found_toast_text))
            }
        } else {
            showErrorMessage(this.getString(R.string.no_app_to_open_page_toast_text))
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
