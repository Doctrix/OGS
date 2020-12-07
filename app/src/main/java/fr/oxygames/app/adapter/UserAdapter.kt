package fr.oxygames.app.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import fr.oxygames.app.R
import fr.oxygames.app.activity.MessageChatActivity
import fr.oxygames.app.activity.VisitUserProfileActivity
import fr.oxygames.app.model.Chat
import fr.oxygames.app.model.Users

class UserAdapter(
    private val context: Context,
    private val users: List<Users>,
    private var isChatCheck: Boolean
    ) : RecyclerView.Adapter<UserAdapter.ViewHolder?>() {
    var lastMsg: String = ""

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_search_item_layout, viewGroup, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {

        val user: Users = users[i]
        holder.userNameTxt.text = user.getUsername()
        Picasso.get().load(user.getAvatar()).placeholder(R.drawable.ic_profile).into(holder.profileImageView)

        if (isChatCheck)
        {
            retrieveLastMessage(user.getUID(), holder.lastMessageTxt)
        }
        else
        {
            holder.lastMessageTxt.visibility = View.GONE
        }

        if (isChatCheck)
        {
            if (user.getStatus() == "online")
            {
                holder.onlineImageView.visibility = View.VISIBLE
                holder.offlineImageView.visibility = View.GONE
            }
            else
            {
                holder.onlineImageView.visibility = View.GONE
                holder.offlineImageView.visibility = View.VISIBLE
            }
        }
        else
        {
            holder.onlineImageView.visibility = View.GONE
            holder.offlineImageView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener{
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("What do you want?")
            builder.setItems(options, DialogInterface.OnClickListener{
                    dialog, position ->
                if (position == 0)
                {
                    val intent = Intent(context, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    context.startActivity(intent)
                }
                if (position == 1)
                {
                    val intent = Intent(context, VisitUserProfileActivity::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    context.startActivity(intent)
                }
            })
            builder.show()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val userNameTxt: TextView = itemView.findViewById(R.id.username)
        val profileImageView: CircleImageView = itemView.findViewById(R.id.image_profil)
        val onlineImageView: CircleImageView = itemView.findViewById(R.id.image_online)
        val offlineImageView: CircleImageView = itemView.findViewById(R.id.image_offline)
        val lastMessageTxt: TextView = itemView.findViewById(R.id.message_last)
    }

    private fun retrieveLastMessage(chatUserId: String?, lastMessageTxt: TextView)
    {
        lastMsg = "defaultMsg"

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val databaseInstance = FirebaseDatabase.getInstance()
        val reference = databaseInstance.reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children) {
                    val chat: Chat? = dataSnapshot.getValue(Chat::class.java)
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver() == firebaseUser.uid &&
                            chat.getSender() == chatUserId ||
                                chat.getReceiver() == chatUserId &&
                                chat.getSender() == firebaseUser.uid) {
                            lastMsg = chat.getMessage()!!
                        }
                    }
                }
                when (lastMsg) {
                    "defaultMsg" -> lastMessageTxt.text = "No Message"
                    "hello" -> lastMessageTxt.text = "image sent"
                    else -> lastMessageTxt.text = lastMsg
                }
                lastMsg = "defaultMsg"
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}