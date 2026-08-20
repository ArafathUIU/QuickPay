package com.arafath.quickpay.ui.payment;

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
import com.arafath.quickpay.domain.model.Merchant;
import com.arafath.quickpay.domain.usecase.QrParser;
import com.arafath.quickpay.ui.pin.PinDialogFragment;
import com.arafath.quickpay.util.Constants;
import com.arafath.quickpay.util.FormatUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class MerchantPaymentFragment extends Fragment {

    private MerchantPaymentViewModel viewModel;
    private MaterialCardView merchantCard;
    private MaterialCardView loadingCard;
    private TextView merchantName;
    private TextView merchantCategory;
    private TextInputEditText amountInput;
    private TextView balanceValue;
    private TextView totalValue;
    private View progress;
    private Merchant merchant;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_merchant_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MerchantPaymentViewModel.class);

        merchantCard = view.findViewById(R.id.merchantCard);
        loadingCard = view.findViewById(R.id.loadingCard);
        merchantName = view.findViewById(R.id.merchantName);
        merchantCategory = view.findViewById(R.id.merchantCategory);
        amountInput = view.findViewById(R.id.amountInput);
        balanceValue = view.findViewById(R.id.balanceValue);
        totalValue = view.findViewById(R.id.totalValue);
        progress = view.findViewById(R.id.progress);

        view.findViewById(R.id.confirmButton).setOnClickListener(v -> {
            if (merchant == null) {
                Snackbar.make(view, "Merchant is not validated.", Snackbar.LENGTH_LONG).show();
                return;
            }
            double amount = parseAmount();
            if (amount <= 0) {
                Snackbar.make(view, R.string.error_invalid_amount, Snackbar.LENGTH_LONG).show();
                return;
            }
            showPinDialog(amount);
        });

        viewModel.getBalance().observe(getViewLifecycleOwner(), balance ->
                balanceValue.setText(FormatUtils.currency(balance)));

        viewModel.getValidationState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                return;
            }
            if (state.isLoading()) {
                loadingCard.setVisibility(View.VISIBLE);
            } else {
                loadingCard.setVisibility(View.GONE);
            }
            if (state.isSuccess()) {
                merchant = state.getData();
                merchantName.setText(merchant.getName());
                merchantCategory.setText(getString(R.string.merchant_category,
                        merchant.getCategory() != null ? merchant.getCategory() : "Merchant"));
                merchantCard.setVisibility(View.VISIBLE);
            } else if (state.isError()) {
                merchant = null;
                merchantCard.setVisibility(View.GONE);
                Snackbar.make(view, state.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getPaymentState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                return;
            }
            progress.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
            if (state.isSuccess()) {
                NavHostFragment.findNavController(MerchantPaymentFragment.this)
                        .navigate(R.id.action_merchantPayment_to_result);
            } else if (state.isError()) {
                Snackbar.make(view, state.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        handleQrArgs();
    }

    private void handleQrArgs() {
        if (getArguments() == null || !getArguments().containsKey(Constants.KEY_QR_DATA)) {
            Snackbar.make(requireView(), R.string.error_invalid_qr, Snackbar.LENGTH_LONG).show();
            return;
        }
        String raw = getArguments().getString(Constants.KEY_QR_DATA);
        QrParser.QrData data = QrParser.parse(raw);
        if (data == null || !data.isMerchant()) {
            Snackbar.make(requireView(), R.string.error_invalid_qr, Snackbar.LENGTH_LONG).show();
            return;
        }
        viewModel.validate(data.id);
    }

    private double parseAmount() {
        try {
            return Double.parseDouble(amountInput.getText().toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private void showPinDialog(double amount) {
        totalValue.setText(FormatUtils.currency(amount));
        PinDialogFragment dialog = PinDialogFragment.newInstance(getString(R.string.pin_title));
        dialog.setCallback(new PinDialogFragment.PinCallback() {
            @Override
            public void onPinEntered(String pin) {
                viewModel.execute(merchant, amount, pin);
            }

            @Override
            public void onCancelled() {
            }
        });
        dialog.show(getParentFragmentManager(), "pin");
    }
}