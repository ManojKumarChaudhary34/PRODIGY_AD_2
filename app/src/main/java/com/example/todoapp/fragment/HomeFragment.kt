package com.example.todoapp.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.utils.ToDoAdapter
import com.example.todoapp.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment(), PopUpDialogFragment.dialogAddBtnClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentHomeBinding
    private lateinit var navController: NavController
    private lateinit var databaseReference: DatabaseReference
    private var popupFragment: PopUpDialogFragment? = null
    private lateinit var adapter: ToDoAdapter
    private lateinit var todoList: MutableList<ToDoData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()
        registerEvents()
    }

    private fun getDataFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                todoList.clear()
                for (taskSnapshot in snapshot.children) {
                    val todoTask =
                        taskSnapshot.key?.let { ToDoData(it, taskSnapshot.value.toString()) }


                    if (todoTask != null) {
                        todoList.add(todoTask)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun registerEvents() {
        binding.addBtnHome.setOnClickListener {
            if (popupFragment != null)
                childFragmentManager.beginTransaction().remove(popupFragment!!)
                    .commit() //this will prevent from creating multiple popups in one time
            popupFragment = PopUpDialogFragment()
            popupFragment!!.setListener(this)
            popupFragment!!.show(
                childFragmentManager, TAG
            )
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Tasks")
            .child(auth.currentUser?.uid.toString())

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.setItemViewCacheSize(13)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        todoList = ArrayList()
        adapter = ToDoAdapter(todoList)
        adapter.setOnClickListener(object : ToDoAdapter.OnItemClickListener {
            override fun onDeleteBtnClick(toDoData: ToDoData) {
                databaseReference.child(toDoData.todoID).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()

                    }
                }
            }

            override fun onEditBtnClick(toDoData: ToDoData) {
                if (popupFragment != null)
                    childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
                popupFragment= PopUpDialogFragment.newInstance(toDoData.todoID, toDoData.todoTask)
                popupFragment!!.setListener(this@HomeFragment)
                popupFragment!!.show(childFragmentManager,TAG)
            }

        })
        binding.recyclerView.adapter = adapter

    }

    override fun onSaveTask(todo: String, todoEt: TextInputEditText) {
        databaseReference.push().setValue(todo).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Todo Saved Successfully!!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            todoEt.text = null
            popupFragment!!.dismiss()
        }
    }

    override fun onUpdateTask(toDoData: ToDoData, todoEt: TextInputEditText) {
        val map= HashMap<String, Any>()
        map[toDoData.todoID]= toDoData.todoTask
        databaseReference.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()

            }
            todoEt.text = null
            popupFragment!!.dismiss()
        }
    }

}