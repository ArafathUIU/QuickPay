package com.arafath.quickpay.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.arafath.quickpay.R;
import com.arafath.quickpay.domain.model.ApiState;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment {

    private LoginViewModel viewModel;
    private TextInputEditText phoneInput;
    private TextInputEditText passwordInput;
    private View progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        phoneInput = view.findViewById(R.id.phoneInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        progress = view.findViewById(R.id.loginProgress);

        view.findViewById(R.id.loginButton).setOnClickListener(v -> {
            viewModel.login(
                    phoneInput.getText().toString(),
                    passwordInput.getText().toString()
            );
        });

        view.findViewById(R.id.registerLink).setOnClickListener(v ->
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_login_to_register));

        viewModel.getLoginState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                return;
            }
            progress.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
            if (state.isSuccess()) {
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_login_to_home);
            } else if (state.isError()) {
                Snackbar.make(view, state.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}