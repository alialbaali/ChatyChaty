package com.apps.chatychaty.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.apps.chatychaty.DURATION
import com.apps.chatychaty.R
import com.apps.chatychaty.databinding.FragmentSignInBinding
import com.apps.chatychaty.network.Repos
import com.apps.chatychaty.util.getPref
import com.apps.chatychaty.util.setPref
import com.apps.chatychaty.util.snackbar
import com.apps.chatychaty.viewModel.Error
import com.apps.chatychaty.viewModel.Sign
import com.apps.chatychaty.viewModel.SignSharedViewModel
import com.apps.chatychaty.viewModel.SignSharedViewModelFactory
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

/**
 * A simple [Fragment] subclass.
 */
class SignInFragment : Fragment(), Sign, Error {
    private lateinit var binding: FragmentSignInBinding

    private val viewModel by viewModels<SignSharedViewModel> {
        SignSharedViewModelFactory(Repos.userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        enterTransition = MaterialFadeThrough.create(requireContext()).apply {
            duration = DURATION
        }

        exitTransition = MaterialFadeThrough.create(requireContext()).apply {
            duration = DURATION
        }
        // Binding
        binding.let {

            it.lifecycleOwner = this

            it.viewModel = viewModel

        }


        binding.tvSignUp.setOnClickListener {
            this.findNavController()
                .navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
        }

        exitTransition =
            MaterialSharedAxis.create(requireContext(), MaterialSharedAxis.Z, true).apply {
                duration = DURATION
            }

        binding.btnSignIn.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            if (username.isBlank()) {
                binding.tilUsername.error = resources.getString(R.string.username_error)
            }
            if (password.isBlank()) {
                binding.tilPassword.error = resources.getString(R.string.password_error_empty)
            }

            if (username.isNotBlank() and password.isNotBlank()) {
                viewModel.signIn()
            }
        }

        // ViewModel
        viewModel.let { viewModel ->

            viewModel.error = this
            viewModel.sign = this

        }

        return binding.root
    }

    override fun snackbar(value: String) {
        binding.cool.snackbar(value)
    }

    override fun putPreferences(token: String, name: String, username: String, imgUrl: String?) {
        this.findNavController().navigate(SignInFragmentDirections.actionGlobalListFragment())

        activity?.setPref("token", token)
        activity?.setPref("name", name)
        activity?.setPref("username", username)
        activity?.setPref("img_url", imgUrl)

        com.apps.chatychaty.token = activity?.getPref("token")
    }
}
