import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.labassignment03.R
import com.example.labassignment03.model.Note
import org.w3c.dom.Text

//Reference: https://www.geeksforgeeks.org/android-recyclerview-in-kotlin/
class NotesAdapter(private val notesList: List<Note>) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = notesList[position].title
        holder.body.text = notesList[position].body
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return notesList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        var title: TextView
        var body: TextView
        init{
             title = itemView.findViewById(R.id.ntitle) as TextView
             body = itemView.findViewById(R.id.nbody) as TextView
        }

    }
}
