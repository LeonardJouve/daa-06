package ch.heigvd.iict.and.rest

import androidx.lifecycle.LiveData
import ch.heigvd.iict.and.rest.database.ContactsDao
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.SyncContact
import ch.heigvd.iict.and.rest.models.SyncStatus

class ContactsRepository(private val contactsDao: ContactsDao) {

    val allContacts = contactsDao.getAllContactsLiveData()

    fun getContactById(id: Long): LiveData<SyncContact?> {
        return contactsDao.getContactById(id)
    }

    fun setContacts(contacts: List<Contact>) {
        contactsDao.setContacts(contacts.map { SyncContact(null, SyncStatus.OK, it) })
    }

    fun softInsertContact(contact: Contact): Long {
        return contactsDao.insert(SyncContact(null, SyncStatus.CREATED, contact))
    }

    fun softUpdateContact(contact: SyncContact) {
        if (contact.status != SyncStatus.CREATED) {
            contact.status = SyncStatus.MODIFIED
        }
        contactsDao.update(contact)
    }

    fun softDeleteContact(contact: SyncContact) {
        contact.status = SyncStatus.DELETED
        contactsDao.update(contact)
    }

    fun syncContact(contact: SyncContact) {
        contact.status = SyncStatus.OK
        contactsDao.update(contact)
    }

    fun hardDeleteContact(contact: SyncContact) {
        contactsDao.delete(contact)
    }

    fun unsynced(): List<SyncContact> {
        return contactsDao.getUnsynced()
    }
}