package com.dkb.universityofsalfordpasswordmanagingapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.dkb.universityofsalfordpasswordmanagingapplication.databinding.FragmentLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentLoginBinding
    private lateinit var navController : NavController
    lateinit var userViewModel: UserViewModel

    private lateinit var username: EditText
    private lateinit var password: EditText

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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        var userModel = userViewModel.userLiveModel.value

        navController = findNavController()
        binding.buttonLogin.setOnClickListener{
            val username: String = binding.textLoginUsername.text.toString()
            val password: String = binding.textLoginPassword.text.toString()

            val retrofit = ServiceBuilder.buildService(API::class.java)
            val loginRequest = LoginRequestModel(username, password)

            retrofit.userLogin(loginRequest).enqueue(
                object: Callback<LoginResponseModel> {
                    override fun onResponse(
                        call: Call<LoginResponseModel>,
                        response: Response<LoginResponseModel>
                    ) {
                        if (response.code() == 200) {
                            if (userModel != null) {
                                userModel.userID = response.body()?.userID.toString()
                                userModel.accessToken = response.body()?.accessToken.toString()
                                userModel.refreshToken = response.body()?.refreshToken.toString()
                            }
                            navController.navigate(R.id.action_loginFragment_to_passwordsFragment)
                        } else if(response.code() == 429) {
                            Toast.makeText(activity, "Too many login attempts, please try again later.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(activity, "Login Failed! Check your Credentials", Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                        Log.d("TAG", t.toString())
                        Toast.makeText(activity, "Failed to connect to database", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
        binding.buttonLoginRegister.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}