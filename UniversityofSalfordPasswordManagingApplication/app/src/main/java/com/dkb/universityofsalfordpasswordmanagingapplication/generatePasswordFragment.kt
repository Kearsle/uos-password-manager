package com.dkb.universityofsalfordpasswordmanagingapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dkb.universityofsalfordpasswordmanagingapplication.databinding.FragmentGeneratePasswordBinding
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
 * Use the [generatePasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class generatePasswordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var navController : NavController
    private lateinit var binding : FragmentGeneratePasswordBinding
    lateinit var passwordCreateViewModel: PasswordCreateViewModel
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
        binding = FragmentGeneratePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        passwordCreateViewModel = ViewModelProvider(requireActivity()).get(PasswordCreateViewModel::class.java)
        val passwordCreateModel = passwordCreateViewModel.passwordCreateLiveModel.value

        binding.buttonGeneratorBack.setOnClickListener {
            navController.navigate(R.id.action_generatePasswordFragment_to_passwordCreateFragment)
        }

        binding.buttonGeneratorAdd.setOnClickListener {
            if (passwordCreateModel != null) {
                passwordCreateModel.programPassword = binding.textGenerator.text.toString()
            }
            navController.navigate(R.id.action_generatePasswordFragment_to_passwordCreateFragment)
        }

        binding.textGeneratorLengthText.text = "length: ${binding.seekBar.progress}"

        binding.checkBoxNumbers.setOnCheckedChangeListener{ view, isChecked ->
            generateRandom(binding.seekBar.progress)
        }

        binding.checkBoxSymbols.setOnCheckedChangeListener{ view, isChecked ->
            generateRandom(binding.seekBar.progress)
        }

        binding.checkBoxUppercase.setOnCheckedChangeListener{ view, isChecked ->
            generateRandom(binding.seekBar.progress)
        }

        binding.checkBoxLowercase.setOnCheckedChangeListener{ view, isChecked ->
            generateRandom(binding.seekBar.progress)
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, length: Int, fromUser: Boolean) {
                generateRandom(length)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        binding.buttonGeneratorTRW.setOnClickListener{
            userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
            val userModel = userViewModel.userLiveModel.value
            if (userModel != null)
            {
                val retrofit = ServiceBuilder.buildService(API::class.java)

                retrofit.generateThreeRandomWords("Bearer " + userModel.accessToken).enqueue(
                    object: Callback<GenerateThreeRandomWordsResponseModelItem> {
                        override fun onResponse(
                            call: Call<GenerateThreeRandomWordsResponseModelItem>,
                            response: Response<GenerateThreeRandomWordsResponseModelItem>
                        ) {
                            // Successful communicated to server
                            if (response.code() == 200) {
                                val generatedWords = response.body()?.randomWords.toString()
                                binding.textGenerator.setText(generatedWords)
                            } else {
                                Toast.makeText(activity, "Failed to generate three random words", Toast.LENGTH_LONG).show()
                            }
                        }
                        override fun onFailure(call: Call<GenerateThreeRandomWordsResponseModelItem>, t: Throwable) {
                            // Failed to connect to server
                            Toast.makeText(activity, "Failed to connect to database", Toast.LENGTH_LONG).show()
                        }
                    }
                )
            }
        }
    }

    fun generateRandom(length: Int) {
        binding.textGeneratorLengthText.text = "length: $length"
        binding.textGenerator.setText(PasswordGenerator.random(length, binding.checkBoxNumbers.isChecked, binding.checkBoxSymbols.isChecked, binding.checkBoxUppercase.isChecked, binding.checkBoxLowercase.isChecked))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment generatePasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            generatePasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}