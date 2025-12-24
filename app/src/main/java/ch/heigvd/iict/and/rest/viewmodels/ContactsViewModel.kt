package ch.heigvd.iict.and.rest.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ch.heigvd.iict.and.rest.ContactsRepository
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
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class ContactsViewModel(private val repository: ContactsRepository) : ViewModel() {

    val allContacts = repository.allContacts
    var sessionId: String? = null
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

    private suspend fun requestContacts(): List<Contact> {
        return sendSessionRequest<List<Contact>, Unit>(HttpMethod.Get, "/contacts")
    }

    private suspend inline fun <reified T, U>sendSessionRequest(method: HttpMethod, endpoint: String, payload: U? = null): T {
        if (sessionId == null) {
            sessionId = requestSessionId()
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

    fun test(): String {
        return "test"
    }

    // actions

    fun setContacts() {
        viewModelScope.launch(Dispatchers.IO) {
            val contacts = requestContacts()
            repository.setContacts(contacts)
        }
    }

    fun enroll() {
        viewModelScope.launch(Dispatchers.IO) {
            // TODO
        }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            // TODO
        }
    }

}

class ContactsViewModelFactory(private val repository: ContactsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}