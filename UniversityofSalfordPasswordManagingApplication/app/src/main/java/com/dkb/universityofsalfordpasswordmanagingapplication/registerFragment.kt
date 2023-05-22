package com.dkb.universityofsalfordpasswordmanagingapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.dkb.universityofsalfordpasswordmanagingapplication.databinding.FragmentLoginBinding
import com.dkb.universityofsalfordpasswordmanagingapplication.databinding.FragmentRegisterBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [registerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class registerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentRegisterBinding
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        binding.buttonRegister.setOnClickListener{
            createUser()
        }
        binding.buttonRegisterCancel.setOnClickListener{
            navController.navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun createUser() {
        val username: String = binding.textRegisterUsername.text.toString()
        val password: String = binding.textRegisterPassword.text.toString()
        val passwordRepeat: String = binding.textRegisterPasswordRepeat.text.toString()
        val email: String = binding.textRegisterEmail.text.toString()

        if (username.length < 3) {
            Toast.makeText(activity, "Username must be at least 3 characters", Toast.LENGTH_LONG).show()
            return
        }
        if (password.length < 5) {
            Toast.makeText(activity, "Password must be at least 5 characters", Toast.LENGTH_LONG).show()
            return
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex())) {
            Toast.makeText(activity, "Email is invalid", Toast.LENGTH_LONG).show()
            return
        }

        if (password != passwordRepeat)
        {
            Toast.makeText(activity, "Passwords are not the same", Toast.LENGTH_LONG).show()
            return
        }

        val retrofit = ServiceBuilder.buildService(API::class.java)
        val registerRequest = RegisterRequestModel(username, password, email)

        retrofit.createUser(registerRequest).enqueue(
            object: Callback<RegisterResponseModel> {
                override fun onResponse(
                    call: Call<RegisterResponseModel>,
                    response: Response<RegisterResponseModel>
                ) {
                    if (response.code() == 201) {
                        Toast.makeText(activity, response.body()?.success.toString(), Toast.LENGTH_LONG).show()
                        navController.navigate(R.id.action_registerFragment_to_loginFragment)
                    } else if (response.code() == 429) {
                        Toast.makeText(activity, "Too many login attempts, please try again later.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(activity, "Failed to Create User", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponseModel>, t: Throwable) {
                    Toast.makeText(activity, "Failed to connect to database", Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment registerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            registerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}