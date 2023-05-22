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
import com.dkb.universityofsalfordpasswordmanagingapplication.databinding.FragmentPasswordResetBinding
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
 * Use the [passwordResetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class passwordResetFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentPasswordResetBinding
    private lateinit var navController : NavController
    lateinit var userViewModel: UserViewModel


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
        binding = FragmentPasswordResetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        binding.buttonReset.setOnClickListener {
            resetPassword()
        }

        binding.buttonPasswordResetCancel.setOnClickListener {
            navController.navigate(R.id.action_passwordResetFragment_to_profileFragment)
        }
    }

    private fun resetPassword() {
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        val userModel = userViewModel.userLiveModel.value

        if (binding.textOldPassword.text.toString() == "") {
            Toast.makeText(activity, "Current Password is required", Toast.LENGTH_LONG).show()
            return
        }

        if (binding.textPassword.text.toString().length < 5) {
            Toast.makeText(activity, "Password must be at least 5 characters", Toast.LENGTH_LONG).show()
            return
        }

        if (binding.textPassword.text.toString() != binding.textPasswordRepeat.text.toString()) {
            Toast.makeText(activity, "Passwords are not the same", Toast.LENGTH_LONG).show()
            return
        }

        if (userModel != null) {
            val retrofit = ServiceBuilder.buildService(API::class.java)
            val passwordResetRequest = PasswordResetRequestModel(binding.textPassword.text.toString(), binding.textOldPassword.text.toString())

            retrofit.userPasswordReset("Bearer " + userModel.accessToken, passwordResetRequest).enqueue(
                object : Callback<PasswordResetResponseModel> {
                    override fun onResponse(
                        call: Call<PasswordResetResponseModel>,
                        response: Response<PasswordResetResponseModel>
                    ) {
                        // Successful communicated to server
                        if (response.code() == 201) {
                            Toast.makeText(activity, "Password Reset", Toast.LENGTH_LONG).show()
                            navController.navigate(R.id.action_passwordResetFragment_to_profileFragment)
                        } else if (response.code() == 401) {
                            Toast.makeText(activity, "Current Password is incorrect", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(activity, "Failed to reset password", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<PasswordResetResponseModel>,
                        t: Throwable
                    ) {
                        // Failed to connect to server
                        Toast.makeText(
                            activity,
                            "Failed to connect to database",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment passwordResetFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            passwordResetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}