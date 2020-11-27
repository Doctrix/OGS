package fr.oxygames.app.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.oxygames.app.R
import fr.oxygames.app.adapter.UserAdapter
import fr.oxygames.app.model.ChatList
import fr.oxygames.app.model.Users

class ChatsFragment : Fragment() {
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var usersChatList: List<ChatList>? = null
    lateinit var recycler_view_chatList: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        recycler_view_chatList = view.findViewById(R.id.recycler_view_chats)
        recycler_view_chatList.setHasFixedSize(true)
        recycler_view_chatList.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                (usersChatList as ArrayList).clear()

                for (dataSnapshot in snapshot.children){
                    val chatList = dataSnapshot.getValue(ChatList::class.java)
                    (usersChatList as ArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return view
    }

    private fun retrieveChatList(){
        mUsers = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                (mUsers as ArrayList).clear()
                for (dataSnapshot in snapshot.children)
                {
                    val user = dataSnapshot.getValue(Users::class.java)
                    for (eachChatList in usersChatList!!)
                    {
                        if (user!!.getUID().equals(eachChatList.getId()))
                        {
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, (mUsers as ArrayList<Users>), true)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}
