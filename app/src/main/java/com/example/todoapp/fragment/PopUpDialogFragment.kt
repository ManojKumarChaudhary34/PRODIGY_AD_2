package com.example.todoapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.todoapp.databinding.FragmentPopUpDialogBinding
import com.example.todoapp.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText


class PopUpDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentPopUpDialogBinding
    private lateinit var listener: dialogAddBtnClickListener
    private var toDoData: ToDoData? = null


    fun setListener(listener: HomeFragment) {
        this.listener = listener
    }

    companion object {
        const val TAG = "PopUpDialogFragment"

        @JvmStatic
        fun newInstance(todoId: String, todoTask: String) = PopUpDialogFragment().apply {
            arguments = Bundle().apply {
                putString("todoId", todoId)
                putString("todoTask", todoTask)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPopUpDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            toDoData = ToDoData(
                arguments?.getString("todoId").toString(),
                arguments?.getString("todoTask").toString()
            )
            binding.inputTaskEt.setText(toDoData?.todoTask)
        }
        registerEvents()
    }

    private fun registerEvents() {
        binding.btnAdd.setOnClickListener {
            val todoTask = binding.inputTaskEt.text.toString()
            if (todoTask.isNotEmpty()) {
                if (toDoData == null){
                    listener.onSaveTask(todoTask, binding.inputTaskEt)
                }else{
                    toDoData?.todoTask = todoTask
                    listener.onUpdateTask(toDoData!!, binding.inputTaskEt)
                }

            } else {
                Toast.makeText(context, "Please type some task", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    interface dialogAddBtnClickListener {
        fun onSaveTask(todo: String, todoEt: TextInputEditText)
        fun onUpdateTask(toDoData: ToDoData, todoEt: TextInputEditText)
    }

}