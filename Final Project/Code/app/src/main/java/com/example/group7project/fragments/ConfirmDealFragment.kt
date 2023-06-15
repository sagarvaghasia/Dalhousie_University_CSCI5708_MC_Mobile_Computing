package com.example.group7project.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.group7project.R
import com.example.group7project.model.Chat
import com.example.group7project.model.Deal
import com.google.android.material.button.MaterialButton

class ConfirmDealFragment : Fragment() {

    private var matchedUserId: String = String();
    private lateinit var termsAndConditions: TextView;
    private lateinit var amount: TextView;
    private lateinit var deal: Deal;
    private var chatId: String = String();

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_confirm_deal, container, false);
        matchedUserId = requireArguments().getString("matchedUserId").toString();
        chatId = requireArguments().getString("chatId").toString();


        termsAndConditions = view.findViewById(R.id.termsAndConditionsValueTextView);
        termsAndConditions.setMovementMethod(ScrollingMovementMethod())
        amount = view.findViewById(R.id.amountValueTextView);

        deal = Deal();
        deal.loadDetails(matchedUserId) { res ->
            run {
                termsAndConditions.setText(deal.termsAndConditions)
                amount.setText(deal.amount);
            }
        };

        // calling the set listener functions
        addAcceptButtonListener(view);
        addRejectButtonListener(view);

        return view;
    }

    // this method listens to accept button and call accept function to accept the deal and goes back to chat activity
    fun addAcceptButtonListener(view: View) {
        var btn: MaterialButton = view.findViewById<MaterialButton>(R.id.btn_acceptDeal);
        btn.setOnClickListener {
            deal.acceptDeal { res ->
                run {
                    var chat: Chat = Chat();
                    var message: String = String();
                    message = "Deal accepted.";
                    // storing a deal accepted message as system message.
                    chat.sendMessage(chatId, "system", message) {}
                    activity?.onBackPressed();
                }
            }
        }
    }

    // this method listens to reject button and call reject function to reject the deal and goes back to chat activity
    fun addRejectButtonListener(view: View) {
        var btn: MaterialButton = view.findViewById<MaterialButton>(R.id.btn_rejectDeal);
        btn.setOnClickListener {
            // will call deal reject here and later navigate to chat activity
            deal.rejectDeal { res ->
                run {
                    var chat: Chat = Chat();
                    var message: String = String();
                    message = "Deal rejected.";
                    chat.sendMessage(chatId, "system", message) {}
                    // storing a deal rejected message as system message.
                    activity?.onBackPressed();
                }
            }
        }
    }

}