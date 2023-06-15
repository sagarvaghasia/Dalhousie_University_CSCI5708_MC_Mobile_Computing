package com.example.group7project.fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.example.group7project.R
import com.example.group7project.model.Chat
import com.example.group7project.model.CurrentUser
import com.example.group7project.model.Deal
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class DealFragment : Fragment() {

    private lateinit var termsAndConditions: EditText;
    private lateinit var amount: EditText;
    private lateinit var deal: Deal;
    private var matchedUserId: String = String();
    private var chatId: String = String();
    private lateinit var rejectedErrorTextView: TextView;
    private var isUpdate: Boolean = false;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_deal, container, false)

        termsAndConditions =
            view.findViewById<TextInputEditText>(R.id.terms_conditions_textInputEditText);
        amount = view.findViewById<TextInputEditText>(R.id.amount_textInputEditText);
        rejectedErrorTextView = view.findViewById<TextView>(R.id.dealRejectedError);

        deal = Deal();
        matchedUserId = requireArguments().getString("matchedUserId").toString();
        chatId = requireArguments().getString("chatId").toString();

        // loading the latest deal details and accordingly populating the terms and conitions , as well as amount
        deal.loadDetails(matchedUserId) { res ->
            run {
                termsAndConditions.setText(deal.termsAndConditions);

                amount.setText(deal.amount);
                if (deal.termsAndConditions.isNullOrEmpty())
                    isUpdate = false;
                else
                    isUpdate = true;

                if (!deal.dealRejected) {
                    rejectedErrorTextView.visibility = View.GONE;
                }
            }
        };

        addButtonClickListener(view);

        return view;
    }

    // it listens to add button and then call validate details and later create deal if
    fun addButtonClickListener(view: View) {
        var btn_createDeal: MaterialButton = view.findViewById<MaterialButton>(R.id.btn_createDeal);
        btn_createDeal.setOnClickListener {
            validateDealDetails { isValid ->
                run {
                    if (isValid) // if details are valid
                    {
                        deal.offeredBy = CurrentUser.userType;
                        deal.termsAndConditions = termsAndConditions.text.toString();
                        deal.amount = amount.text.toString();

                        // calling the createDeal method
                        deal.createDeal {
                            var chat: Chat = Chat();
                            var message: String = String();
                            if (isUpdate)
                                message = "Deal updated.";
                            else
                                message = "Deal created.";
                            // storing a deal created/updated message
                            chat.sendMessage(chatId, "system", message) {}
                            activity?.onBackPressed(); // traversing back to chat activity.
                        }
                    }
                }
            }
        }
    }

    // it validates the details entered by user while creating or updating the deal.
    fun validateDealDetails(callback: (Boolean) -> Unit) {
        termsAndConditions.setError(null);
        amount.setError(null);
        when {

            TextUtils.isEmpty(termsAndConditions.text.toString().trim()) -> {
                termsAndConditions.setError("Please enter terms & conditions.")
            }

            TextUtils.isEmpty(amount.text.toString().trim()) -> {
                amount.setError("Please correct amount here")
            }

            amount.text.toString().isNotEmpty() -> {

                if (amount.text.toString().matches(Regex("[0-9]+"))) {
                    callback(true);
                } else {
                    amount.setError("Invalid amount");
                }
            }
            (true) -> callback(false);
        }
    }

}