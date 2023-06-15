package com.example.group7project.fragments

import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.compose.ui.text.toLowerCase
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.group7project.R
import com.example.group7project.adapters.MatchesAdapter
import com.example.group7project.model.CurrentUser
import com.example.group7project.model.Match
import com.example.group7project.model.Matches
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText

// Matches fragment which shows the fragment
class MatchesFragment : Fragment() {

    private var matchesList: ArrayList<Match>? = ArrayList();
    private lateinit var matchesAdapter: MatchesAdapter;
    private lateinit var recylerView: RecyclerView;
    lateinit var progressIndicator: CircularProgressIndicator;
    private lateinit var searchMatchEditText: EditText;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_matches, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setting up the progress indicator
        progressIndicator = view.findViewById(R.id.progressIndicator);
        progressIndicator.visibility = View.VISIBLE;

        searchMatchEditText = view.findViewById<TextInputEditText>(R.id.searchMatchEditText);

        var matches: Matches = Matches();
        recylerView = view.findViewById(R.id.matchesRecyclerView);
        matchesList?.clear();
        matches.getAllMatches { res ->
            run {
                progressIndicator.visibility = View.GONE;
                matchesList?.addAll(res as ArrayList<Match>);
                //setting up the recycler view for the home fragment
                // Learnt from lab assignment 3 & https://www.youtube.com/watch?v=5mdV1hLbXzo
                recylerView.adapter = matchesList?.let { MatchesAdapter(it, this) };
                recylerView.layoutManager = LinearLayoutManager(context);
                recylerView.setHasFixedSize(true);
                setSearchMatchTextViewListener();
            }
        }
    }


    // setting the search bar text view listener
    fun setSearchMatchTextViewListener() {

        var currentFragment: Fragment = this;

        // adding the listener to the edit text.
        searchMatchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                var mList: ArrayList<Match> = ArrayList();
                mList.addAll(matchesList!!);
                if (matchesList != null) {
                    if (mList != null && s.length > 0) {
                        // filtering based on the text entered by the user.
                        recylerView.adapter = MatchesAdapter(mList.filter { match ->
                            match.name.toString().toLowerCase().contains(s.toString().toLowerCase())
                        } as ArrayList<Match>, currentFragment);
                    } else {
                        recylerView.adapter = MatchesAdapter(mList, currentFragment);
                    }

                    recylerView.layoutManager = LinearLayoutManager(context);
                    recylerView.setHasFixedSize(true);
                    progressIndicator.visibility = View.GONE;
                }
            }

        });
    }

}