package ch.heigvd.iict.and.rest.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ch.heigvd.iict.and.rest.ContactsRepository
import ch.heigvd.iict.and.rest.SessionManager
import ch.heigvd.iict.and.rest.models.Contact
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ContactsViewModel(private val repository: ContactsRepository, private val sessionManager: SessionManager) : ViewModel() {

    val allContacts = repository.allContacts
    var sessionId: String? = sessionManager.getSession()
    val baseURL = "https://daa.iict.ch"

    private val ktorClient = HttpClient(Android) {
        engine {
            connectTimeout = 5_000
            socketTimeout = 5_000
            dispatcher = Dispatchers.IO
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private suspend fun requestSessionId(): String {
        return ktorClient
            .get(baseURL + "/enroll")
            .body()
    }

    private suspend inline fun <reified T, U>sendSessionRequest(method: HttpMethod, endpoint: String, payload: U? = null): T {
        if (sessionId == null) {
            sessionId = requestSessionId()
            sessionManager.saveSession(sessionId!!)
        }

        return ktorClient.request(baseURL + endpoint) {
            this.method = method
            header("X-UUID", sessionId!!)
            if (payload != null) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }
        }.body<T>()
    }

    fun getContactById(id: Long): LiveData<Contact?> {
        return repository.getContactById(id)
    }

    // actions
    fun enroll(): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            sessionId = requestSessionId()
            sessionManager.saveSession(sessionId!!)
            val contacts = sendSessionRequest<List<Contact>, Unit>(HttpMethod.Get, "/contacts")
            repository.setContacts(contacts)
        }
    }

    fun refresh(): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            // TODO
        }
    }

    fun create(contact: Contact): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            val dbContact = sendSessionRequest<Contact, Contact>(HttpMethod.Post, "/contacts/", contact)
            repository.addContact(dbContact)
        }
    }

    fun update(contact: Contact): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            val dbContact = sendSessionRequest<Contact, Contact>(HttpMethod.Put, "/contacts/" + contact.id, contact)
            repository.updateContact(dbContact)
        }
    }

    fun delete(contact: Contact): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            sendSessionRequest<Unit, Unit>(HttpMethod.Delete, "/contacts/" + contact.id)
            repository.deleteContact(contact)
        }
    }
}

class ContactsViewModelFactory(private val repository: ContactsRepository, private val sessionManager: SessionManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactsViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}