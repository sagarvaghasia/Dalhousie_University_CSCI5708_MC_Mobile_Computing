package com.example.group7project.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.group7project.R
import com.example.group7project.fragments.ConfirmDealFragment
import com.example.group7project.fragments.DealFragment
import com.example.group7project.fragments.ViewDealFragment
import com.example.group7project.model.CurrentUser
import com.example.group7project.model.Deal

class DealActivity : AppCompatActivity() {

    private var secondaryUserEmail: String? = String();
    private var chatId: String? = String();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal);

        var bundle = intent.extras;
        if (bundle != null) {
            secondaryUserEmail = bundle.getString("secondaryUserEmail");
            chatId = bundle.getString("chatId");
        }
    }

    override fun onStart() {
        super.onStart();

        var deal = Deal();

        secondaryUserEmail?.let {
            deal.loadDetails(it) { res ->
                run {
                    if (res == true)
                        navigateToCorrectFragment(deal);
                }
            }
        }
    }

    // navigating to the correct fragment based on the the state of the deal between the users
    fun navigateToCorrectFragment(deal: Deal) {
        var bundle: Bundle = Bundle();
        bundle.putString("matchedUserId", secondaryUserEmail);
        bundle.putString("chatId", chatId);
        if (deal.dealOffered == true) // that means deal is already offered once
        {
            if (deal.dealCompleted == false) // that means deal is offered, but not accepted till now
            {
                if (deal.offeredBy.equals(CurrentUser.userType)) {
                    // navigating to new deal page
                    var dealFragment: DealFragment = DealFragment();
                    dealFragment.arguments = bundle;
                    setFragment(dealFragment);
                } else {

                    // navigating to confirm deal page
                    var confirmDealFragment: ConfirmDealFragment = ConfirmDealFragment();
                    confirmDealFragment.arguments = bundle;
                    setFragment(confirmDealFragment);
                }
            } else // this means that deal is offered as well as accepted
            {
                // navigating to confirm deal page
                var viewDealFragment: ViewDealFragment = ViewDealFragment();
                viewDealFragment.arguments = bundle;
                setFragment(viewDealFragment);
            }
        } else // that means deal is never offered so simply a new deal is required
        {
            // navigating to new deal page
            var dealFragment: DealFragment = DealFragment();
            dealFragment.arguments = bundle;
            setFragment(dealFragment);
        }

    }

    // this function replaces the deal container view with the received fragment
    fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.dealFragmentContainerView, fragment)
            .commit();
    }

}