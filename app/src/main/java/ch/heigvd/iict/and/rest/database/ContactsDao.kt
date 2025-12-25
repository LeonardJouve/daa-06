package ch.heigvd.iict.and.rest.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ch.heigvd.iict.and.rest.models.SyncContact
import ch.heigvd.iict.and.rest.models.SyncStatus

@Dao
interface ContactsDao {

    @Insert
    fun insert(contact: SyncContact) : Long

    @Insert()
    fun insertAll(contacts: List<SyncContact>)

    @Transaction
    fun setContacts(contacts: List<SyncContact>) {
        clearAllContacts()
        insertAll(contacts)
    }

    @Update
    fun update(contact: SyncContact)

    @Delete
    fun delete(contact: SyncContact)

    @Query("SELECT * FROM SyncContact")
    fun getAllContactsLiveData() : LiveData<List<SyncContact>>

    @Query("SELECT * FROM SyncContact")
    fun getAllContacts() : List<SyncContact>

    @Query("SELECT * FROM SyncContact WHERE syncId = :id")
    fun getContactById(id : Long) : LiveData<SyncContact?>

    @Query("SELECT COUNT(*) FROM SyncContact")
    fun getCount() : Int

    @Query("DELETE FROM SyncContact")
    fun clearAllContacts()

    @Query("SELECT * FROM SyncContact WHERE status != 'OK'")
    fun getUnsynced(): List<SyncContact>
}