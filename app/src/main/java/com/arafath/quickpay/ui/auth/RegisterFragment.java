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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterFragment extends Fragment {

    private RegisterViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        TextInputEditText nameInput = view.findViewById(R.id.nameInput);
        TextInputEditText phoneInput = view.findViewById(R.id.phoneInput);
        TextInputEditText emailInput = view.findViewById(R.id.emailInput);
        TextInputEditText passwordInput = view.findViewById(R.id.passwordInput);
        TextInputEditText confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput);
        TextInputEditText pinInput = view.findViewById(R.id.pinInput);
        View progress = view.findViewById(R.id.registerProgress);

        view.findViewById(R.id.registerButton).setOnClickListener(v -> viewModel.register(
                nameInput.getText().toString(),
                phoneInput.getText().toString(),
                emailInput.getText().toString(),
                passwordInput.getText().toString(),
                confirmPasswordInput.getText().toString(),
                pinInput.getText().toString()
        ));

        viewModel.getRegisterState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                return;
            }
            progress.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
            if (state.isSuccess()) {
                Snackbar.make(view, "Account created. Please log in.", Snackbar.LENGTH_LONG).show();
                NavController navController = NavHostFragment.findNavController(RegisterFragment.this);
                navController.navigateUp();
            } else if (state.isError()) {
                Snackbar.make(view, state.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}