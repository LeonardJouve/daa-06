package ch.heigvd.iict.and.rest

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ch.heigvd.iict.and.rest.fragments.ContactFormFragment

class UpsertContact : AppCompatActivity() {
    companion object {
        val CONTACT_ID_KEY = "contact_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_upsert_contact)

        val contactId = intent.getLongExtra(CONTACT_ID_KEY, -1)

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content_fragment, ContactFormFragment.newInstance(if (contactId == -1L) null else contactId))
            .commit()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.upsert_contact)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}