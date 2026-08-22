package com.arafath.quickpay.ui.sendmoney;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.arafath.quickpay.R;
import com.arafath.quickpay.domain.model.ApiState;
import com.arafath.quickpay.domain.model.User;
import com.arafath.quickpay.domain.usecase.QrParser;
import com.arafath.quickpay.ui.pin.PinDialogFragment;
import com.arafath.quickpay.util.Constants;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class SendMoneyFragment extends Fragment {

    private SendMoneyViewModel viewModel;
    private TextInputEditText phoneInput;
    private TextInputEditText amountInput;
    private TextInputEditText noteInput;
    private MaterialCardView recipientCard;
    private TextView recipientName;
    private TextView recipientPhone;
    private View progress;
    private User recipient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_money, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SendMoneyViewModel.class);

        phoneInput = view.findViewById(R.id.phoneInput);
        amountInput = view.findViewById(R.id.amountInput);
        noteInput = view.findViewById(R.id.noteInput);
        recipientCard = view.findViewById(R.id.recipientCard);
        recipientName = view.findViewById(R.id.recipientName);
        recipientPhone = view.findViewById(R.id.recipientPhone);
        progress = view.findViewById(R.id.progress);

        view.findViewById(R.id.searchButton).setOnClickListener(v ->
                viewModel.search(phoneInput.getText().toString()));

        view.findViewById(R.id.confirmButton).setOnClickListener(v -> {
            if (recipient == null) {
                Snackbar.make(view, "Search and select a recipient first.", Snackbar.LENGTH_LONG).show();
                return;
            }
            double amount = parseAmount();
            if (amount <= 0) {
                Snackbar.make(view, R.string.error_invalid_amount, Snackbar.LENGTH_LONG).show();
                return;
            }
            showPinDialog(amount);
        });

        viewModel.getSearchState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                return;
            }
            if (state.isSuccess()) {
                recipient = state.getData();
                recipientName.setText(recipient.getName());
                recipientPhone.setText(recipient.getPhone());
                TextView initial = view.findViewById(R.id.recipientInitial);
                String name = recipient.getName();
                if (name != null && !name.isEmpty()) {
                    initial.setText(name.substring(0, 1).toUpperCase());
                }
                recipientCard.setVisibility(View.VISIBLE);
            } else if (state.isError()) {
                recipient = null;
                recipientCard.setVisibility(View.GONE);
                Snackbar.make(view, state.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getSendState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                return;
            }
            progress.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
            if (state.isSuccess()) {
                NavHostFragment.findNavController(SendMoneyFragment.this)
                        .navigate(R.id.action_sendMoney_to_result);
            } else if (state.isError()) {
                Snackbar.make(view, state.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        handleQrArgs();
    }

    private void handleQrArgs() {
        if (getArguments() != null && getArguments().containsKey(Constants.KEY_QR_DATA)) {
            String raw = getArguments().getString(Constants.KEY_QR_DATA);
            QrParser.QrData data = QrParser.parse(raw);
            if (data != null && data.isUser()) {
                phoneInput.setText(data.id);
                viewModel.search(data.id);
            }
        }
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
                String note = noteInput.getText() != null ? noteInput.getText().toString() : "";
                viewModel.execute(recipient, amount, note, pin);
            }

            @Override
            public void onCancelled() {
            }
        });
        dialog.show(getParentFragmentManager(), "pin");
    }
}