package com.arafath.quickpay.ui.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.arafath.quickpay.R;
import com.arafath.quickpay.domain.model.ApiState;
import com.arafath.quickpay.ui.pin.PinDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class AddMoneyFragment extends Fragment {

    private AddMoneyViewModel viewModel;
    private TextInputEditText amountInput;
    private View progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_money, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddMoneyViewModel.class);

        amountInput = view.findViewById(R.id.amountInput);
        progress = view.findViewById(R.id.progress);

        view.findViewById(R.id.quick500).setOnClickListener(v -> amountInput.setText("500"));
        view.findViewById(R.id.quick1000).setOnClickListener(v -> amountInput.setText("1000"));
        view.findViewById(R.id.quick5000).setOnClickListener(v -> amountInput.setText("5000"));

        view.findViewById(R.id.confirmButton).setOnClickListener(v -> {
            double amount = parseAmount();
            int code = viewModel.validateAmount(amount);
            if (code != com.arafath.quickpay.domain.usecase.ValidatePaymentUseCase.OK) {
                Snackbar.make(view,
                        com.arafath.quickpay.domain.usecase.ValidatePaymentUseCase.messageFor(code),
                        Snackbar.LENGTH_LONG).show();
                return;
            }
            showPinDialog(amount);
        });

        viewModel.getState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                return;
            }
            progress.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
            if (state.isSuccess()) {
                NavHostFragment.findNavController(AddMoneyFragment.this)
                        .navigate(R.id.action_addMoney_to_result);
            } else if (state.isError()) {
                Snackbar.make(view, state.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private double parseAmount() {
        try {
            return Double.parseDouble(amountInput.getText().toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private void showPinDialog(double amount) {
        PinDialogFragment dialog = PinDialogFragment.newInstance(getString(R.string.pin_title));
        dialog.setCallback(new PinDialogFragment.PinCallback() {
            @Override
            public void onPinEntered(String pin) {
                viewModel.execute(amount, pin);
            }

            @Override
            public void onCancelled() {
            }
        });
        dialog.show(getParentFragmentManager(), "pin");
    }
}