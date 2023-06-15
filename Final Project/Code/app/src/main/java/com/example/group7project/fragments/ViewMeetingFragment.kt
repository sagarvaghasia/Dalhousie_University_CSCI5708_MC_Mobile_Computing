package com.example.group7project.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.group7project.R
import com.example.group7project.`interface`.Communicator
import com.google.android.material.button.MaterialButton

class ViewMeetingFragment : Fragment() {

    private var date: String? = String();
    private var time: String? = String();
    private var title: String? = String();
    private var status: String? = String();
    private lateinit var comm: Communicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = it.getString("scheduledDate")
            time = it.getString("scheduledTime")
            title = it.getString("title")
            status = it.getString("status")
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_view_meeting, container, false)

        var dateView = view.findViewById<TextView>(R.id.viewDateValueTextView)
        dateView.text = date.toString()

        var timeView = view.findViewById<TextView>(R.id.viewTimeValueTextView)
        timeView.text = time.toString()

        var titleView = view.findViewById<TextView>(R.id.viewTitleValueTextView)
        titleView.text = title.toString()

        var statusView = view.findViewById<TextView>(R.id.viewStatusValueTextView)
        statusView.text = status.toString()

        var btn = view.findViewById<MaterialButton>(R.id.btn_backToMeetingList)

        comm = activity as Communicator

        // this will go back to the meeting list screen
        btn.setOnClickListener {
            comm.goBackFun()
        }
        return view
    }
}