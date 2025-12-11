package ch.heigvd.iict.and.rest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ch.heigvd.iict.and.rest.fragments.ContactFormFragment
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModelFactory
import kotlin.getValue

class UpsertContact : AppCompatActivity() {
    private val contactsViewModel: ContactsViewModel by viewModels {
        ContactsViewModelFactory((application as ContactsApplication).repository)
    }

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