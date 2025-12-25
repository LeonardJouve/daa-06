package ch.heigvd.iict.and.rest.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.R
import ch.heigvd.iict.and.rest.UpsertContact
import ch.heigvd.iict.and.rest.databinding.FragmentContactFormBinding
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.PhoneType
import ch.heigvd.iict.and.rest.models.SyncContact
import ch.heigvd.iict.and.rest.models.SyncStatus
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [ContactForm.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactFormFragment : Fragment() {
    private val contactViewModel: ContactsViewModel by activityViewModels {
        ContactsViewModelFactory((requireActivity().application as ContactsApplication).repository, (requireActivity().application as ContactsApplication).sessionManager)
    }

    private var contactId: Long = -1
    private lateinit var contact: LiveData<SyncContact?>

    private lateinit var binding : FragmentContactFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactId = arguments?.getLong(UpsertContact.CONTACT_ID_KEY, -1L)
            ?: -1L
    }

    fun setupView() {
        if (contact.value == null) {
            binding.deleteButton.visibility = View.GONE
            binding.contactFormTitle.text = getString(R.string.fragment_detail_title_new)
            binding.upsertButton.text = getString(R.string.fragment_btn_create)
        } else {
            val contact = contact.value!!.contact

            binding.deleteButton.visibility = View.VISIBLE
            binding.contactFormTitle.text = getString(R.string.fragment_detail_title_edit)
            binding.upsertButton.text = getString(R.string.fragment_btn_save)

            binding.nameInput.setText(contact.name)
            binding.firstnameInput.setText(contact.firstname)
            binding.emailInput.setText(contact.email)
            contact.birthday?.time.let {
                binding.birthdayInput.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it))
            }
            binding.addressInput.setText(contact.address)
            binding.zipInput.setText(contact.zip)
            binding.cityInput.setText(contact.city)
            when (contact.type) {
                PhoneType.HOME -> binding.homeRadio.isChecked = true
                PhoneType.FAX -> binding.faxRadio.isChecked = true
                PhoneType.MOBILE -> binding.mobileRadio.isChecked = true
                PhoneType.OFFICE -> binding.officeRadio.isChecked = true
                else -> {}
            }
            binding.phoneInput.setText(contact.phoneNumber)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactFormBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButton.setOnClickListener {
            requireActivity().finish()
        }

        binding.deleteButton.setOnClickListener {
            contact.value?.let {
                contactViewModel.delete(it).invokeOnCompletion {
                    requireActivity().finish()
                }
            }
        }

        binding.upsertButton.setOnClickListener {
            val newContact = Contact(
                id = contact.value?.contact?.id,
                name = binding.nameInput.text.toString(),
                firstname = binding.firstnameInput.text.toString(),
                birthday = contact.value?.contact?.birthday ?: Calendar.getInstance(),
                email = binding.emailInput.text.toString(),
                address = binding.addressInput.text.toString(),
                zip = binding.zipInput.text.toString(),
                city = binding.cityInput.text.toString(),
                type = when {
                    binding.homeRadio.isChecked -> PhoneType.HOME
                    binding.faxRadio.isChecked -> PhoneType.FAX
                    binding.mobileRadio.isChecked -> PhoneType.MOBILE
                    binding.officeRadio.isChecked -> PhoneType.OFFICE
                    else -> null
                },
                phoneNumber = binding.phoneInput.text.toString(),
            )

            val job = if (contact.value == null)
                contactViewModel.create(newContact)
                else contactViewModel.update(SyncContact(contact.value!!.syncId, SyncStatus.MODIFIED, newContact))

            job.invokeOnCompletion {
                requireActivity().finish()
            }
        }

        contact = if (contactId == -1L)
            MutableLiveData<SyncContact?>(null)
            else contactViewModel.getContactById(contactId)

        contact.observe(viewLifecycleOwner, { setupView() })
    }

        companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ContactForm.
         */
        @JvmStatic
        fun newInstance(contactId: Long?) =
            ContactFormFragment().apply {
                arguments = Bundle().apply {
                    if (contactId != null)
                        putLong(UpsertContact.CONTACT_ID_KEY, contactId)
                }
            }
    }
}