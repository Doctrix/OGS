package fr.oxygames.app.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.oxygames.app.*
import fr.oxygames.app.adapter.UserAdapter
import fr.oxygames.app.model.UsersModel
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : Fragment() {
    var userAdapter: UserAdapter? = null
    var mUsers: List<UsersModel>? = null
    var recyclerView: RecyclerView? = null
    var searchEditText: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        searchEditText = view.findViewById(R.id.searchUsers)

        retrieveAllUsers()
        mUsers = ArrayList()


        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(cs.toString().toLowerCase(Locale.FRENCH))
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        return view
    }

    private fun retrieveAllUsers() {

        print("hello search")

        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")
        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<UsersModel>).clear()
                if (searchEditText!!.text.toString() == "") {
                    for (snapshot in p0.children) {
                        val user: UsersModel? = snapshot.getValue(UsersModel::class.java)
                        if (!(user!!.getUID()).equals(firebaseUserID)) {
                            (mUsers as ArrayList<UsersModel>).add(user)
                        }
                    }
                    userAdapter = UserAdapter(context!!, mUsers!!, false)
                    recyclerView!!.adapter = userAdapter
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun searchForUsers(str: String) {
        print("hello search")

        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers = FirebaseDatabase.getInstance().reference
            .child("Users").orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")
        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<UsersModel>).clear()
                for (snapshot in p0.children) {
                    val user: UsersModel? = snapshot.getValue(UsersModel::class.java)
                    if (!(user!!.getUID()).equals(firebaseUserID)) {
                        (mUsers as ArrayList<UsersModel>).add(user)
                    }
                }
                userAdapter = UserAdapter(context!!, mUsers!!, false)
                recyclerView!!.adapter = userAdapter
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

}