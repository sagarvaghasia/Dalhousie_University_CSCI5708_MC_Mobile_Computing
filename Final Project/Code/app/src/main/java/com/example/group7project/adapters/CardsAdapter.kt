package com.example.group7project.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.group7project.R
import com.example.group7project.activities.UserDetailsActivity
import com.example.group7project.model.SwipeDeckUser
import java.util.*
import java.util.concurrent.TimeUnit

/*
    References:
    1. https://developer.android.com/reference/kotlin/androidx/recyclerview/widget/RecyclerView
    2. https://developer.android.com/reference/kotlin/androidx/recyclerview/widget/RecyclerView.Adapter
    3. https://bumptech.github.io/glide/
    4. https://www.youtube.com/watch?v=eKYBUuxM3aA&ab_channel=OumSaokosal
    5. https://www.udemy.com/course/buildtinder/
 */

class CardsAdapter(context: Context, resourceId: Int, users: List<SwipeDeckUser>) :
    ArrayAdapter<SwipeDeckUser>(context, resourceId, users) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var user = getItem(position)
        var finalView =
            convertView ?: LayoutInflater.from(context).inflate(R.layout.swipe_item, parent, false)

        // References to layout items
        var name = finalView.findViewById<TextView>(R.id.name)
        var description = finalView.findViewById<TextView>(R.id.description)
        var image = finalView.findViewById<ImageView>(R.id.cardImage)
        var userDetails = finalView.findViewById<ConstraintLayout>(R.id.profileClickLayout)
        var lastActive = finalView.findViewById<TextView>(R.id.lastActive)

        val currentDateTime = Date()
        val userLastActive = if (user?.lastActive != null) user?.lastActive else Date()

        name.text = "${user?.name}"
        description.text = "${user?.description}"

        // Define and calculate activity status of user
        var lastActivityMessage = ""
        val msDiff =
            Math.abs(currentDateTime.getTime() - (userLastActive?.getTime() ?: Date().time))
        val daysDiff = TimeUnit.DAYS.convert(msDiff, TimeUnit.MILLISECONDS)

        if (daysDiff >= 0 && daysDiff <= 1)
            lastActivityMessage = "Active Today"
        else if (daysDiff > 1 && daysDiff <= 7)
            lastActivityMessage = "Active This Week"
        else if (daysDiff > 7 && daysDiff <= 14)
            lastActivityMessage = "Active Last Week"
        else if (daysDiff > 14 && daysDiff <= 30)
            lastActivityMessage = "Active Last Month"
        else
            lastActivityMessage = "Inactive"

        lastActive.text = lastActivityMessage

        Glide.with(context)
            .load(user?.imageUrl)
            .into(image)

        userDetails.setOnClickListener {
            finalView.context.startActivity(
                UserDetailsActivity.newIntent(
                    finalView.context,
                    user?.email
                )
            )
        }
        return finalView
    }
}