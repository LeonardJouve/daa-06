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
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModelFactory


/**
 * A simple [Fragment] subclass.
 * Use the [ContactForm.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactFormFragment : Fragment() {
    private val contactViewModel: ContactsViewModel by activityViewModels {
        ContactsViewModelFactory((requireActivity().application as ContactsApplication).repository)
    }

    private var contactId: Long = -1
    private lateinit var contact: LiveData<Contact?>

    private lateinit var binding : FragmentContactFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactId = arguments?.getLong(UpsertContact.CONTACT_ID_KEY, -1L)
            ?: -1L
    }

    fun setupView() {
        if (contact.value == null) {
            binding.deleteButton.visibility = View.GONE;
            binding.contactFormTitle.text = getString(R.string.fragment_detail_title_new)
            binding.upsertButton.text = getString(R.string.fragment_btn_create)
        } else {
            binding.deleteButton.visibility = View.VISIBLE;
            binding.contactFormTitle.text = getString(R.string.fragment_detail_title_edit)
            binding.upsertButton.text = getString(R.string.fragment_btn_save)
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

        contact = if (contactId == -1L)
            MutableLiveData<Contact?>(null)
            else contactViewModel.getContactById(contactId)

        contact.observe(viewLifecycleOwner, { contact ->
            setupView()
            Toast.makeText(requireActivity(), contact.toString(), Toast.LENGTH_LONG).show()
        })
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