package fr.oxygames.app.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import fr.oxygames.app.adapter.UserAdapter
import fr.oxygames.app.databinding.FragmentChatsBinding
import fr.oxygames.app.model.ChatListModel
import fr.oxygames.app.model.ChatModel
import fr.oxygames.app.model.UserModel
import fr.oxygames.app.notifications.Token
import java.util.*

class ChatsFragment : Fragment() {
    private lateinit var binding: FragmentChatsBinding

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<UserModel>? = null
    private var usersChatList: List<ChatListModel>? = null
    lateinit var recyclerViewChatList: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    private var usersReference: DatabaseReference? = null
    private var chatModel: ChatModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatsBinding.inflate(layoutInflater)

        recyclerViewChatList = binding.recyclerViewChatList
        recyclerViewChatList.setHasFixedSize(true)
        recyclerViewChatList.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                (usersChatList as ArrayList).clear()

                for (dataSnapshot in snapshot.children)
                {
                    val chatModel = dataSnapshot.getValue(ChatListModel::class.java)

                    (usersChatList as ArrayList).add(chatModel!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        updateToken(FirebaseInstanceId.getInstance().token)

        return binding.root
    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun retrieveChatList() {

        mUsers = ArrayList()

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                (mUsers as ArrayList).clear()
                for (dataSnapshot in snapshot.children)
                {
                    val user = dataSnapshot.getValue(UserModel::class.java)
                    for (eachChatList in usersChatList!!)
                    {
                        if (user!!.getUID().equals(eachChatList.getId()))
                        {
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, (mUsers as ArrayList<UserModel>), true)
                recyclerViewChatList.adapter = userAdapter
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
