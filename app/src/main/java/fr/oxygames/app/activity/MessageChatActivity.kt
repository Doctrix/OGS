package fr.oxygames.app.activity
/*
* Auteur : DOCtriX Bertrand PRIVAT
* Title : OxyMobile
* */
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import fr.oxygames.app.R
import fr.oxygames.app.adapter.ChatsAdapter
import fr.oxygames.app.databinding.ActivityMessageChatBinding
import fr.oxygames.app.fragment.APIService
import fr.oxygames.app.model.ChatModel
import fr.oxygames.app.model.Data
import fr.oxygames.app.model.UsersModel
import fr.oxygames.app.notifications.Client
import fr.oxygames.app.notifications.MyResponse
import fr.oxygames.app.notifications.Sender
import fr.oxygames.app.notifications.Token
import org.jetbrains.anko.longToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityMessageChatBinding
    var notify = false
    lateinit var userIdVisit: String
    var apiService: APIService? = null
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var chatsAdapter: ChatsAdapter? = null
    var mChatModelList: List<ChatModel>? = null
    lateinit var recyclerViewChats: RecyclerView
    private var user: UsersModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // toolbar
        val toolbar: Toolbar = binding.toolbarMessageChat
        binding.toolbarMessageChat.title = "Chat"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        val intent = Intent()
        val userIdVisit = intent.getStringExtra("visit_id")
        firebaseUser = FirebaseAuth.getInstance().currentUser

        recyclerViewChats = binding.recyclerViewChats
        recyclerViewChats.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerViewChats.layoutManager = linearLayoutManager

        reference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        longToast("visit_id")
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                user = snapshot.getValue(UsersModel::class.java)

                binding.usernameChat.text = user!!.getUsername()
                Picasso.get().load(user!!.getAvatar()).into(binding.profileImageChat)

                retrieveMessages(firebaseUser!!.uid, userIdVisit, user!!.getAvatar())
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })

        binding.sendMessageBtn.setOnClickListener {
            notify = true
            val message = binding.textMessageChat.text.toString()
            if (message == "")
            {
                longToast("Please write a message, first...")
            }
            else
            {
             sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
            }
            binding.textMessageChat.setText("")
        }

        binding.attachImageFileBtn.setOnClickListener {
            notify = true
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)
        }
        //seenMessage(userIdVisit)
    }

    // send message to chats
    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String)
    {
        reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference!!.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isSeen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey
        reference!!.child("Chats")
        .child(messageKey!!)
        .setValue(messageHashMap)
        .addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val chatsListReference = FirebaseDatabase.getInstance()
                    .reference
                    .child("ChatList")
                    .child(firebaseUser!!.uid)
                    .child(userIdVisit)

                chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists())
                        {
                            chatsListReference.child("id").setValue(userIdVisit)
                        }

                        val chatsListReceiverRef = FirebaseDatabase.getInstance()
                            .reference
                            .child("ChatList")
                            .child(userIdVisit)
                            .child(firebaseUser!!.uid)
                        chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                    }

                    override fun onCancelled(error: DatabaseError)
                    {
                    }
                })
            }
        }

        //implement the push notifications using fcm
        reference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        reference!!.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                val user = snapshot.getValue(UsersModel::class.java)
                if (notify)
                {
                    //sendNotification(receiverId, user!!.getUsername(), message)
                }
                notify = false
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    private fun sendNotification(receiverId: String?, username: String?, message: String)
    {
        reference = FirebaseDatabase.getInstance().reference.child("Tokens")

        val query = reference!!.orderByKey().equalTo(receiverId)

        query.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                for (dataSnapshot in snapshot.children)
                {
                    val token: Token? = dataSnapshot.getValue(Token::class.java)

                    val data = Data(firebaseUser!!.uid, R.mipmap.ic_launcher,"$username: $message","New Message", userIdVisit)
                    val sender = Sender(data, token!!.getToken().toString())

                apiService!!.sendNotification(sender).enqueue(object : Callback<MyResponse>
                    {
                        override fun onResponse(
                            call: Call<MyResponse>,
                            response: Response<MyResponse>
                        )
                        {
                            if (response.code() == 200)
                            {
                                if (response.body()!!.success != 1)
                                    longToast("Failed, Nothing happen.")
                            }
                        }
                        override fun onFailure(call: Call<MyResponse>, t: Throwable)
                        {

                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==438 && resultCode==RESULT_OK && data!=null && data.data!=null)
        {
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("image is uploading, please wait...")
            progressBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chats Images")
            reference = FirebaseDatabase.getInstance().reference
            val messageId = reference!!.push().key
            val filePath = storageReference.child("$messageId.jpg")

            val uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if (!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()
                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "sent you an image"
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["isSeen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    reference!!.child("Chats").child(messageId!!).setValue(messageHashMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful)
                        {
                            progressBar.dismiss()
                            //implement the push notifications using fcm
                            reference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
                            reference!!.addValueEventListener(object : ValueEventListener
                            {
                                override fun onDataChange(snapshot: DataSnapshot)
                                {
                                    val user = snapshot.getValue(UsersModel::class.java)
                                    if (notify)
                                    {
                                        //sendNotification(userIdVisit, user!!.getUsername(), "sent you an image.")
                                    }
                                    notify = false
                                }

                                override fun onCancelled(error: DatabaseError)
                                {

                                }
                            })
                        }
                    }
                }
            }
        }
    }

    private fun retrieveMessages(senderId: String, receiverId: String?, receiverImageUrl: String?) {
        mChatModelList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                (mChatModelList as ArrayList<ChatModel>).clear()
                for (snapShot in p0.children)
                {
                    val chat = snapShot.getValue(ChatModel::class.java)

                    if (chat!!.getReceiver().equals(senderId) && chat.getSender().equals(receiverId)
                        || chat.getReceiver().equals(receiverId) && chat.getSender().equals(senderId))
                    {
                        (mChatModelList as ArrayList<ChatModel>).add(chat)
                    }
                    chatsAdapter = ChatsAdapter(this@MessageChatActivity, (mChatModelList as ArrayList<ChatModel>), receiverImageUrl!!)
                    recyclerViewChats.adapter = chatsAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    var seenList: ValueEventListener? = null

    private fun seenMessage(userId: String) {
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        seenList = reference.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {
                for (dataSnapshot in p0.children)
                {
                    val chat = dataSnapshot.getValue(ChatModel::class.java)

                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && chat.getSender().equals(userId))
                    {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isSeen"] = true
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

    override fun onPause() {
        super.onPause()

        reference!!.removeEventListener(seenList!!)
    }
}
