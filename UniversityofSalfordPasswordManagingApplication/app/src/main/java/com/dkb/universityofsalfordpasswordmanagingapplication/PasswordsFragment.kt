package com.dkb.universityofsalfordpasswordmanagingapplication

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.dkb.universityofsalfordpasswordmanagingapplication.databinding.FragmentPasswordsBinding
import com.dkb.universityofsalfordpasswordmanagingapplication.databinding.PasswordItemViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PasswordsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PasswordsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentPasswordsBinding
    lateinit var passwordsViewModel: PasswordViewModel
    lateinit var userViewModel: UserViewModel
    lateinit var passwordAdapter: PasswordAdapter
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
        //return inflater.inflate(R.layout.fragment_passwords, container, false)
        binding = FragmentPasswordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        binding.buttonPasswordsCreatePassword.setOnClickListener {
            navController.navigate(R.id.action_passwordsFragment_to_passwordCreateFragment)
        }

        binding.buttonLogout.setOnClickListener {
            logout()
        }
        binding.buttonProfile.setOnClickListener {
            navController.navigate(R.id.action_passwordsFragment_to_profileFragment)
        }

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        val userModel = userViewModel.userLiveModel.value
        passwordsViewModel = ViewModelProvider(requireActivity()).get(PasswordViewModel::class.java)
        val passwordModel = passwordsViewModel.passwordsLiveModel.value

        binding.passwordRecyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)


        if (passwordModel != null && userModel != null)
        {
            val passwords = passwordModel.passwords
            passwords.clear()
            val retrofit = ServiceBuilder.buildService(API::class.java)

            retrofit.passwords("Bearer " + userModel.accessToken).enqueue(
                object: Callback<List<PasswordsResponseModelItem>> {
                    override fun onResponse(
                        call: Call<List<PasswordsResponseModelItem>>,
                        response: Response<List<PasswordsResponseModelItem>>
                    ) {
                        // Successful communicated to server
                        if (response.code() == 200) {
                            val responseBody = response.body()!!
                            passwordsViewModel = ViewModelProvider(requireActivity()).get(PasswordViewModel::class.java)
                            for (passwordResponseData in responseBody) {
                                passwords.add(PasswordItem(passwordResponseData.programName, passwordResponseData.programUsername, passwordResponseData.programPassword, passwordResponseData._id))
                            }
                            passwordAdapter = PasswordAdapter(passwords, {position -> deletePassword(position)}, {position -> editPassword(position)}, {password, title -> copyPassword(password, title)})
                            binding.passwordRecyclerView.adapter = passwordAdapter
                        } else {
                            Toast.makeText(activity, "Failed to load passwords", Toast.LENGTH_LONG).show()
                        }

                    }
                    override fun onFailure(call: Call<List<PasswordsResponseModelItem>>, t: Throwable) {
                        // Failed to connect to server
                        Toast.makeText(activity, "Failed to connect to database", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }

    private fun deletePassword(index: Int) {
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        passwordsViewModel = ViewModelProvider(requireActivity()).get(PasswordViewModel::class.java)
        val passwordModel = passwordsViewModel.passwordsLiveModel.value
        val userModel = userViewModel.userLiveModel.value

        if (passwordModel != null && userModel != null) {
            val passwords = passwordModel.passwords
            val passwordDeleteRequest = PasswordDeleteRequestModel(passwords[index]._id)

            val retrofit = ServiceBuilder.buildService(API::class.java)

            retrofit.passwordsDelete("Bearer " + userModel.accessToken, passwordDeleteRequest).enqueue(
                object: Callback<PasswordDeleteResponseModel> {
                    override fun onResponse(
                        call: Call<PasswordDeleteResponseModel>,
                        response: Response<PasswordDeleteResponseModel>
                    ) {
                        // Successful communicated to server
                        if (response.code() == 200) {
                            Toast.makeText(activity, passwords[index].programName + " Deleted", Toast.LENGTH_LONG).show()
                            passwords.removeAt(index)
                            passwordAdapter = PasswordAdapter(passwords, {position -> deletePassword(position)}, {position -> editPassword(position)}, {password, title -> copyPassword(password, title)})
                            binding.passwordRecyclerView.adapter = passwordAdapter
                        } else {
                            Toast.makeText(activity, "Failed to delete password", Toast.LENGTH_LONG).show()
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

    private fun editPassword(index: Int) {
        val passwordModel = passwordsViewModel.passwordsLiveModel.value
        var passwordEdit: PasswordEditModel? = passwordsViewModel.passwordEditLiveModel.value
        if (passwordModel != null) {
            val password = passwordModel.passwords[index]
            if (passwordEdit != null) {
                passwordEdit._id = password._id
                passwordEdit.programName = password.programName
                passwordEdit.programUsername = password.programUsername
                passwordEdit.programPassword = password.programPassword
                navController = findNavController()
                navController.navigate(R.id.action_passwordsFragment_to_passwordEditFragment)
            }
        }
    }

    private fun logout() {
        navController = findNavController()
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        var userModel = userViewModel.userLiveModel.value

        if (userModel != null) {
            val retrofit = ServiceBuilder.buildService(API::class.java)
            val logoutRequest = LogoutRequestModel(userModel.refreshToken)

            retrofit.userLogout("Bearer " + userModel.accessToken, logoutRequest).enqueue(
                object: Callback<LogoutResponseModel> {
                    override fun onResponse(
                        call: Call<LogoutResponseModel>,
                        response: Response<LogoutResponseModel>
                    ) {
                        // Successful communicated to server
                        if (response.code() == 200) {
                            Toast.makeText(activity, "Logged out", Toast.LENGTH_LONG).show()
                            navController.navigate(R.id.action_passwordsFragment_to_loginFragment)
                        } else {
                            Toast.makeText(activity, "Failed to log out", Toast.LENGTH_LONG).show()
                        }

                    }
                    override fun onFailure(call: Call<LogoutResponseModel>, t: Throwable) {
                        // Failed to connect to server
                        Toast.makeText(activity, "Failed to connect to database", Toast.LENGTH_LONG).show()
                    }
                }
            )
        } else {
            navController.navigate(R.id.action_passwordsFragment_to_loginFragment)
        }
    }

    private fun copyPassword(password: String, title: String) {
        val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Password", password)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(activity, "Password for $title copied to clipboard", Toast.LENGTH_LONG).show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PasswordsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PasswordsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}