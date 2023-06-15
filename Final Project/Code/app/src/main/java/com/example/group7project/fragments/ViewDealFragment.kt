package com.example.group7project.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.group7project.R
import com.example.group7project.model.Deal


class ViewDealFragment : Fragment() {

    private var matchedUserId: String = String();
    private lateinit var termsAndConditions: TextView;
    private lateinit var amount: TextView;
    private lateinit var deal: Deal;
    private var chatId: String = String();

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_view_deal, container, false);
        matchedUserId = requireArguments().getString("matchedUserId").toString();
        chatId = requireArguments().getString("chatId").toString();

        termsAndConditions = view.findViewById(R.id.termsAndConditionsValueTextView);
        termsAndConditions.setMovementMethod(ScrollingMovementMethod())
        amount = view.findViewById(R.id.amountValueTextView);

        deal = Deal();
        // loading the details of the deal
        deal.loadDetails(matchedUserId) { res ->
            run {
                termsAndConditions.setText(deal.termsAndConditions.toString());
                amount.setText(deal.amount.toString());
            }
        }

        // calling for setting button click listener
        setButtonClickListener(view);
        return view;
    }

    // listening to back button and taking back to chat activity
    fun setButtonClickListener(view: View) {
        var btn = view.findViewById<Button>(R.id.returnButton);
        btn.setOnClickListener {
            activity?.onBackPressed();
        }
    }

}