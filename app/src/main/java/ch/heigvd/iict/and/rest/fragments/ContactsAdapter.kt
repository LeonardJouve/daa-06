package ch.heigvd.iict.and.rest.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ch.heigvd.iict.and.rest.R
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.PhoneType
import ch.heigvd.iict.and.rest.models.SyncContact
import ch.heigvd.iict.and.rest.models.SyncStatus

class ContactsAdapter(contacts : List<SyncContact>, private val clickListener: OnItemClickListener) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    var contacts : List<SyncContact> = contacts
    set(value) {
        val diffCallBack = ContactsDiffCallBack(contacts, value)
        val diffItem = DiffUtil.calculateDiff(diffCallBack)
        field = value
        diffItem.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contactToDisplay = contacts[position]
        holder.bind(contactToDisplay, position)
    }

    override fun getItemCount() = contacts.size

    override fun getItemViewType(position: Int) = 0

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val image = view.findViewById<ImageView>(R.id.contact_image)
        private val name = view.findViewById<TextView>(R.id.contact_name)
        private val phonenumber = view.findViewById<TextView>(R.id.contact_phonenumber)
        private val type = view.findViewById<ImageView>(R.id.contact_phonenumber_type)

        fun bind(contact : SyncContact, position: Int) {
            view.setOnClickListener {
                clickListener.onItemClick(null, view, position, contact.syncId!!)
            }
            name.text = "${contact.contact.name} ${contact.contact.firstname}"
            phonenumber.text = "${contact.contact.phoneNumber}"

            val colRes = when (contact.status) {
                SyncStatus.OK -> android.R.color.black
                SyncStatus.CREATED -> android.R.color.holo_green_dark
                SyncStatus.DELETED -> android.R.color.holo_red_dark
                SyncStatus.MODIFIED -> android.R.color.holo_blue_dark
            }
            image.setColorFilter(ContextCompat.getColor(image.context, colRes))

            when(contact.contact.type) {
                PhoneType.HOME -> type.setImageResource(R.drawable.phone)
                PhoneType.OFFICE -> type.setImageResource(R.drawable.office)
                PhoneType.MOBILE -> type.setImageResource(R.drawable.cellphone)
                PhoneType.FAX -> type.setImageResource(R.drawable.fax)
                else -> type.setImageResource(android.R.color.transparent)
            }

        }
    }
}

class ContactsDiffCallBack(private val oldList: List<SyncContact>, private val newList : List<SyncContact>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].syncId == newList[newItemPosition].syncId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        val oldSyncContact = oldList[oldItemPosition]
        val oldContact = oldSyncContact.contact
        val newSyncContact = newList[newItemPosition]
        val newContact = newSyncContact.contact

        return  oldSyncContact.status == newSyncContact.status &&
                oldContact.name == newContact.name &&
                oldContact.firstname == newContact.firstname &&
                oldContact.birthday == newContact.birthday &&
                oldContact.email == newContact.email &&
                oldContact.address == newContact.address &&
                oldContact.zip == newContact.zip &&
                oldContact.city == newContact.city &&
                oldContact.type == newContact.type &&
                oldContact.phoneNumber == newContact.phoneNumber
    }

}