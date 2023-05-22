package com.dkb.universityofsalfordpasswordmanagingapplication

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.dkb.universityofsalfordpasswordmanagingapplication.databinding.FragmentPasswordCreateBinding
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
 * Use the [passwordCreateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class passwordCreateFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentPasswordCreateBinding
    private lateinit var navController : NavController
    lateinit var userViewModel: UserViewModel
    lateinit var passwordCreateViewModel: PasswordCreateViewModel

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
        binding = FragmentPasswordCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        passwordCreateViewModel = ViewModelProvider(requireActivity()).get(PasswordCreateViewModel::class.java)
        val passwordCreateModel = passwordCreateViewModel.passwordCreateLiveModel.value
        if (passwordCreateModel != null) {
            binding.textProgramName.setText(passwordCreateModel.programName)
            binding.textProgramUsername.setText(passwordCreateModel.programUsername)
            binding.textProgramPassword.setText(passwordCreateModel.programPassword)
            passwordCreateModel.programName = ""
            passwordCreateModel.programUsername = ""
            passwordCreateModel.programPassword = ""
        }
        binding.buttonShowProgramPassword.setOnClickListener{
            if(binding.buttonShowProgramPassword.text.toString().equals("Show")){
                binding.textProgramPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.buttonShowProgramPassword.text = "Hide"
            } else{
                binding.textProgramPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.buttonShowProgramPassword.text = "Show"
            }
        }
        binding.buttonPasswordCreateSubmit.setOnClickListener{
            createPassword()
        }
        binding.buttonPasswordCreateCancel.setOnClickListener{
            navController.navigate(R.id.action_passwordCreateFragment_to_passwordsFragment)
        }
        binding.buttonCreatePasswordGeneratePassword.setOnClickListener{
            if (passwordCreateModel != null) {
                passwordCreateModel.programName = binding.textProgramName.text.toString()
                passwordCreateModel.programUsername = binding.textProgramUsername.text.toString()
                passwordCreateModel.programPassword = binding.textProgramPassword.text.toString()
            }
            navController.navigate(R.id.action_passwordCreateFragment_to_generatePasswordFragment)
        }
    }

    private fun createPassword() {
        val programName: String = binding.textProgramName.text.toString()
        val programUsername: String = binding.textProgramUsername.text.toString()
        val programPassword: String = binding.textProgramPassword.text.toString()

        if (programName.isEmpty()) {
            Toast.makeText(activity, "Program Title is required", Toast.LENGTH_LONG).show()
            return
        }
        if (programUsername.isEmpty()) {
            Toast.makeText(activity, "Program Username/Email is required", Toast.LENGTH_LONG).show()
            return
        }
        if (programPassword.isEmpty()) {
            Toast.makeText(activity, "Program Password is required", Toast.LENGTH_LONG).show()
            return
        }

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        var userModel = userViewModel.userLiveModel.value
        val retrofit = ServiceBuilder.buildService(API::class.java)
        val passwordCreateRequest = PasswordCreateRequestModel(programName, programUsername, programPassword)

        if (userModel != null) {
            retrofit.passwordsCreate("Bearer " + userModel.accessToken, passwordCreateRequest).enqueue(
                object : Callback<PasswordCreateResponseModel> {
                    override fun onResponse(
                        call: Call<PasswordCreateResponseModel>,
                        response: Response<PasswordCreateResponseModel>
                    ) {
                        if (response.code() == 201) {
                            Log.d("TAG", "${response.body()?._id.toString()}")
                            Toast.makeText(
                                activity,
                                "${response.body()?.programName.toString()} created",
                                Toast.LENGTH_LONG
                            ).show()
                            navController.navigate(R.id.action_passwordCreateFragment_to_passwordsFragment)
                        } else {
                            Toast.makeText(activity, "Failed to Create User", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<PasswordCreateResponseModel>, t: Throwable) {
                        Toast.makeText(activity, "Failed to connect to database", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            )
        } else {
            Toast.makeText(activity, "Not logged in", Toast.LENGTH_LONG)
                .show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment passwordCreateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            passwordCreateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}