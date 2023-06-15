package com.example.labassignment02

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import org.w3c.dom.Text

class FragmentTwo : Fragment() {

    var output : String ?= ""
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
        val view = inflater.inflate(R.layout.fragment_two, container, false)

        val backBtn : Button = view.findViewById(R.id.backBtn)
        val textview : TextView = view.findViewById(R.id.displayMessage)
        output = arguments?.getString("message")

        if(output == ""){
            textview.text = "The secret was: no secret"
        }
        else{
            textview.text = "The secret was: " + output
        }


        communicator = activity as  Communicator

        backBtn.setOnClickListener {
            val fragmentOne = FragmentOne()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentContainerView, fragmentOne)?.commit()
        }
        return view
    }
}











