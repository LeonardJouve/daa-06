package ch.heigvd.iict.and.rest.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ch.heigvd.iict.and.rest.ContactsRepository
import ch.heigvd.iict.and.rest.models.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsViewModel(private val repository: ContactsRepository) : ViewModel() {

    val allContacts = repository.allContacts

    fun getContactById(id: Long): LiveData<Contact?> {
        return repository.getContactById(id)
    }

    fun test(): String {
        return "test"
    }

    // actions
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