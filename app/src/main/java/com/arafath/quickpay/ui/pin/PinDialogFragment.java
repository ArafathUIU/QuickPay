package com.arafath.quickpay.ui.pin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.arafath.quickpay.R;
import com.arafath.quickpay.util.Validators;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class PinDialogFragment extends DialogFragment {

    public interface PinCallback {
        void onPinEntered(String pin);

        void onCancelled();
    }

    private PinCallback callback;

    public static PinDialogFragment newInstance(String title) {
        PinDialogFragment fragment = new PinDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    public void setCallback(PinCallback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_pin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null && getArguments().getString("title") != null) {
            ((android.widget.TextView) view.findViewById(R.id.pinTitle)).setText(
                    getArguments().getString("title"));
        }

        TextInputEditText pinInput = view.findViewById(R.id.pinInput);

        view.findViewById(R.id.confirmButton).setOnClickListener(v -> {
            String pin = pinInput.getText() != null ? pinInput.getText().toString() : "";
            if (!Validators.isValidPin(pin)) {
                Snackbar.make(view, "Enter a valid 4-digit PIN.", Snackbar.LENGTH_LONG).show();
                return;
            }
            if (callback != null) {
                callback.onPinEntered(pin);
            }
            dismiss();
        });

        view.findViewById(R.id.cancelButton).setOnClickListener(v -> {
            if (callback != null) {
                callback.onCancelled();
            }
            dismiss();
        });
    }
}