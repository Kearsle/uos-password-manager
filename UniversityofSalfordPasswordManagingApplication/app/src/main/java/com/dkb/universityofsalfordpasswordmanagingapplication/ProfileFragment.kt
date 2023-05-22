package com.dkb.universityofsalfordpasswordmanagingapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.dkb.universityofsalfordpasswordmanagingapplication.databinding.FragmentLoginBinding
import com.dkb.universityofsalfordpasswordmanagingapplication.databinding.FragmentProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentProfileBinding
    private lateinit var navController : NavController
    lateinit var userViewModel: UserViewModel
    private var infoShow = false
    private lateinit var username: String
    private lateinit var email: String


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
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        binding.buttonShowUserData.setOnClickListener {
            if (infoShow) {
                // hide info
                binding.buttonShowUserData.text = "Show"
                var hidUsername = ""
                for (i in 0..username.length-1) {
                    if (i == 0) {
                        hidUsername += username[i]
                    } else {
                        hidUsername += "*"
                    }
                }
                binding.textUsername.text = hidUsername
                var hidEmail = ""
                var lastChar: String = ""
                for (i in 0..email.length-1) {
                    if (i == 0 || email[i].toString() == "@" || email[i].toString() == "." || lastChar == "@") {
                        hidEmail += email[i]
                    } else {
                        hidEmail += "*"
                    }
                    lastChar = email[i].toString()
                }
                binding.textEmail.text = hidEmail
                infoShow = false
            } else {
                // show info
                binding.buttonShowUserData.text = "Hide"
                binding.textUsername.text = username
                binding.textEmail.text = email
                infoShow = true
            }
        }

        binding.buttonDeleteAccount.setOnClickListener {
            deleteUser()
        }

        binding.buttonDeleteProgramPasswords.setOnClickListener {
            deleteAllPasswords()
        }

        binding.buttonResetPassword.setOnClickListener{
            navController.navigate(R.id.action_profileFragment_to_passwordResetFragment)
        }

        binding.buttonLogoutAll.setOnClickListener {
            logoutAll()
        }

        binding.buttonProfileBack.setOnClickListener{
            navController.navigate(R.id.action_profileFragment_to_passwordsFragment)
        }

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        val userModel = userViewModel.userLiveModel.value

        if (userModel != null) {
            val retrofit = ServiceBuilder.buildService(API::class.java)

            retrofit.getUser("Bearer " + userModel.accessToken).enqueue(
                object : Callback<UserGetResponseModel> {
                    override fun onResponse(
                        call: Call<UserGetResponseModel>,
                        response: Response<UserGetResponseModel>
                    ) {
                        // Successful communicated to server
                        if (response.code() == 200) {
                            username = response.body()?.username.toString()
                            email = response.body()?.email.toString()
                            binding.buttonShowUserData.text = "Show"
                            var hidUsername = ""
                            for (i in 0..username.length-1) {
                                if (i == 0) {
                                    hidUsername += username[i]
                                } else {
                                    hidUsername += "*"
                                }
                            }
                            binding.textUsername.text = hidUsername
                            var hidEmail = ""
                            var lastChar: String = ""
                            for (i in 0..email.length-1) {
                                if (i == 0 || email[i].toString() == "@" || email[i].toString() == "." || lastChar == "@") {
                                    hidEmail += email[i]
                                } else {
                                    hidEmail += "*"
                                }
                                lastChar = email[i].toString()
                            }
                            binding.textEmail.text = hidEmail
                            infoShow = false
                        } else {
                            Toast.makeText(activity, "Failed to get user info", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<UserGetResponseModel>, t: Throwable) {
                        // Failed to connect to server
                        Toast.makeText(activity, "Failed to connect to database", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            )
        }
    }

    private fun deleteUser() {
        navController = findNavController()
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        var userModel = userViewModel.userLiveModel.value

        if (userModel != null) {
            val retrofit = ServiceBuilder.buildService(API::class.java)

            retrofit.userDelete("Bearer " + userModel.accessToken).enqueue(
                object: Callback<UserDeleteResponseModel> {
                    override fun onResponse(
                        call: Call<UserDeleteResponseModel>,
                        response: Response<UserDeleteResponseModel>
                    ) {
                        // Successful communicated to server
                        if (response.code() == 200) {
                            Toast.makeText(activity, "Account Deleted", Toast.LENGTH_LONG).show()
                            navController.navigate(R.id.action_profileFragment_to_loginFragment)
                        } else {
                            Toast.makeText(activity, "Failed to delete your account", Toast.LENGTH_LONG).show()
                        }

                    }
                    override fun onFailure(call: Call<UserDeleteResponseModel>, t: Throwable) {
                        // Failed to connect to server
                        Toast.makeText(activity, "Failed to connect to database", Toast.LENGTH_LONG).show()
                    }
                }
            )
        } else {
            navController.navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }

    private fun deleteAllPasswords() {
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        val userModel = userViewModel.userLiveModel.value

        if (userModel != null) {
            val retrofit = ServiceBuilder.buildService(API::class.java)

            retrofit.passwordsDeleteAll("Bearer " + userModel.accessToken).enqueue(
                object: Callback<PasswordDeleteResponseModel> {
                    override fun onResponse(
                        call: Call<PasswordDeleteResponseModel>,
                        response: Response<PasswordDeleteResponseModel>
                    ) {
                        // Successful communicated to server
                        if (response.code() == 200) {
                            Toast.makeText(activity, "All Stored Passwords Deleted", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(activity, "Failed to delete all passwords", Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure(call: Call<PasswordDeleteResponseModel>, t: Throwable) {
                        // Failed to connect to server
                        Toast.makeText(activity, "Failed to connect to database", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }

    private fun logoutAll() {
        navController = findNavController()
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        var userModel = userViewModel.userLiveModel.value

        if (userModel != null) {
            val retrofit = ServiceBuilder.buildService(API::class.java)

            retrofit.userLogoutAll("Bearer " + userModel.accessToken).enqueue(
                object: Callback<LogoutAllResponseModel> {
                    override fun onResponse(
                        call: Call<LogoutAllResponseModel>,
                        response: Response<LogoutAllResponseModel>
                    ) {
                        // Successful communicated to server
                        if (response.code() == 200) {
                            Toast.makeText(activity, "Logged out on all devices", Toast.LENGTH_LONG).show()
                            navController.navigate(R.id.action_profileFragment_to_loginFragment)
                        } else {
                            Toast.makeText(activity, "Failed to log out on all devices", Toast.LENGTH_LONG).show()
                        }

                    }
                    override fun onFailure(call: Call<LogoutAllResponseModel>, t: Throwable) {
                        // Failed to connect to server
                        Toast.makeText(activity, "Failed to connect to database", Toast.LENGTH_LONG).show()
                    }
                }
            )
        } else {
            navController.navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}