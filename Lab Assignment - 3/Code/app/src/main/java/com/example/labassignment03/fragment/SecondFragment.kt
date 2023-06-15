package com.example.labassignment03.fragment

import android.graphics.ColorSpace.Model
import android.os.Bundle
import android.provider.ContactsContract.Data
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.findNavController
import com.example.labassignment03.R
import com.example.labassignment03.databinding.FragmentSecondBinding
import com.example.labassignment03.model.Note
import com.example.labassignment03.persistence.Database

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private lateinit var note : Note

    private var _binding: FragmentSecondBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

      _binding = FragmentSecondBinding.inflate(inflater, container, false)
      return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveBtn.setOnClickListener {

            val noteTitle = binding.root.findViewById<TextView>(R.id.noteTitle)
            val noteBody = binding.root.findViewById<TextView>(R.id.noteBody)

            if(noteTitle.text.toString().isEmpty() || noteBody.text.toString().isEmpty()){
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
            }
            else{
                val note = Note(noteTitle.text.toString(), noteBody.text.toString())
                Database.addNotesToList(note)
                noteTitle.text = ""
                noteBody.text = ""
            }
        }
    }
override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}