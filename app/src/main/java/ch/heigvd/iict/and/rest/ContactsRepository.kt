package ch.heigvd.iict.and.rest

import androidx.lifecycle.LiveData
import ch.heigvd.iict.and.rest.database.ContactsDao
import ch.heigvd.iict.and.rest.models.Contact

class ContactsRepository(private val contactsDao: ContactsDao) {

    val allContacts = contactsDao.getAllContactsLiveData()

    fun getContactById(id: Long): LiveData<Contact?> {
        return contactsDao.getContactById(id)
    }

    fun setContacts(contacts: List<Contact>) {
        contactsDao.setContacts(contacts)
    }

    fun addContact(contact: Contact) {
        contactsDao.insert(contact)
    }

    fun updateContact(contact: Contact) {
        contactsDao.update(contact)
    }

    fun deleteContact(contact: Contact) {
        contactsDao.delete(contact)
    }

    companion object {
        private val TAG = "ContactsRepository"
    }

}