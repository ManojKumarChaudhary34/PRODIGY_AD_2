package com.example.todoapp.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.EachRowBinding

class ToDoAdapter(private val todoList: MutableList<ToDoData>):
RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>(){

    private lateinit var myListener: OnItemClickListener

    fun setOnClickListener(listener: OnItemClickListener){
        this.myListener= listener
    }

    interface OnItemClickListener{
        fun onDeleteBtnClick(toDoData: ToDoData)
        fun onEditBtnClick(toDoData: ToDoData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding= EachRowBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ToDoViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return todoList.size
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        holder.tasks.text= todoList[position].todoTask

        holder.editButton.setOnClickListener {
            myListener.onEditBtnClick(todoList[position])
        }
        holder.deleteButton.setOnClickListener {
            myListener.onDeleteBtnClick(todoList[position])
        }
    }

    class ToDoViewHolder(binding: EachRowBinding): RecyclerView.ViewHolder(binding.root) {
        val tasks= binding.todoTask
        val editButton= binding.editBtn
        val deleteButton= binding.deleteBtn
    }


}
