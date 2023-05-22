package com.dkb.universityofsalfordpasswordmanagingapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.dkb.universityofsalfordpasswordmanagingapplication.databinding.PasswordItemViewBinding


class PasswordAdapter (val passwords : ArrayList<PasswordItem>, val onClickDelete: (Int) -> Unit, val onClickEdit: (Int) -> Unit, val onClickCopy: (String, String) -> Unit) : RecyclerView.Adapter<PasswordAdapter.ViewHolder>(){

    inner class ViewHolder(var itemBinding: PasswordItemViewBinding) : RecyclerView.ViewHolder(itemBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordAdapter.ViewHolder {
        val itemBinding = PasswordItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PasswordAdapter.ViewHolder, position: Int) {
        val passwordItem = passwords[position]
        holder.itemBinding.nameTextView.text = passwordItem.programName
        holder.itemBinding.usernameTextView.text = passwordItem.programUsername
        holder.itemBinding.passwordTextView.text = passwordItem.programPassword

        holder.itemBinding.buttonDeletePassword.setOnClickListener {
            onClickDelete(position)
        }

        holder.itemBinding.buttonEditPassword.setOnClickListener {
            onClickEdit(position)
        }

        holder.itemBinding.nameTextView.setOnClickListener {
            onClickCopy(holder.itemBinding.passwordTextView.text.toString(), holder.itemBinding.nameTextView.text.toString())
        }

        holder.itemBinding.usernameTextView.setOnClickListener {
            onClickCopy(holder.itemBinding.passwordTextView.text.toString(), holder.itemBinding.nameTextView.text.toString())
        }

        holder.itemBinding.passwordTextView.setOnClickListener {
            onClickCopy(holder.itemBinding.passwordTextView.text.toString(), holder.itemBinding.nameTextView.text.toString())
        }

        // Show/Hide Password

        holder.itemBinding.passwordTextView.isVisible = false
        holder.itemBinding.buttonShowPassword.text = "Show Password"

        holder.itemBinding.buttonShowPassword.setOnClickListener {
            if (holder.itemBinding.passwordTextView.isVisible) {
                // if shown then hide
                holder.itemBinding.buttonShowPassword.text = "Show Password"
                holder.itemBinding.passwordTextView.isVisible = false
            } else {
                // if hid then show
                holder.itemBinding.buttonShowPassword.text = "Hide"
                holder.itemBinding.passwordTextView.isVisible = true
            }
        }
    }

    override fun getItemCount(): Int {
        return passwords.size
    }
}