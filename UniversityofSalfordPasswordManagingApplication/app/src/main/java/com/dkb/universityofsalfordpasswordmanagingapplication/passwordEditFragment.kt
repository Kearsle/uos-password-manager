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
import com.dkb.universityofsalfordpasswordmanagingapplication.databinding.FragmentPasswordEditBinding
import com.dkb.universityofsalfordpasswordmanagingapplication.databinding.FragmentRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [passwordEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class passwordEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentPasswordEditBinding
    private lateinit var navController : NavController
    lateinit var passwordsViewModel: PasswordViewModel
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
        binding = FragmentPasswordEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        val userModel = userViewModel.userLiveModel.value
        passwordsViewModel = ViewModelProvider(requireActivity()).get(PasswordViewModel::class.java)
        val passwordEditModel = passwordsViewModel.passwordEditLiveModel.value

        if (userModel != null && passwordEditModel != null) {
            binding.textProgramNameEdit.setText(passwordEditModel.programName)
            binding.textProgramUsernameEdit.setText(passwordEditModel.programUsername)
            binding.textProgramPasswordEdit.setText(passwordEditModel.programPassword)
        }

        navController = findNavController()
        binding.buttonPasswordEditSubmit.setOnClickListener{
            if (userModel != null && passwordEditModel != null) {
                val retrofit = ServiceBuilder.buildService(API::class.java)
                val passwordEditRequest = PasswordEditRequestModel(
                    passwordEditModel._id,
                    binding.textProgramNameEdit.text.toString(),
                    binding.textProgramUsernameEdit.text.toString(),
                    binding.textProgramPasswordEdit.text.toString()
                )

                retrofit.passwordsEdit("Bearer " + userModel.accessToken, passwordEditRequest).enqueue(
                    object : Callback<PasswordEditResponseModel> {
                        override fun onResponse(
                            call: Call<PasswordEditResponseModel>,
                            response: Response<PasswordEditResponseModel>
                        ) {
                            // Successful communicated to server
                            if (response.code() == 201) {
                                Toast.makeText(activity, "Edited Password", Toast.LENGTH_LONG).show()
                                navController.navigate(R.id.action_passwordEditFragment_to_passwordsFragment)
                            } else {
                                Toast.makeText(activity, "Failed to edit password", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(
                            call: Call<PasswordEditResponseModel>,
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
        binding.buttonPasswordEditCancel.setOnClickListener{
            navController.navigate(R.id.action_passwordEditFragment_to_passwordsFragment)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment passwordEditFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            passwordEditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}