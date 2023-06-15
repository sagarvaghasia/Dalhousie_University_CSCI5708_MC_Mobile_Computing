package com.example.labassignment02

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

class FragmentOne : Fragment() {

    private lateinit var communicator: Communicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_one, container, false)

        val sendBtn : Button = view.findViewById(R.id.sendBtn)
        val editText : EditText = view.findViewById(R.id.editTextTextPassword2)

        communicator = activity as  Communicator

        sendBtn.setOnClickListener {
            communicator.passDataCom(editText.text.toString())
        }
        return view
    }
}