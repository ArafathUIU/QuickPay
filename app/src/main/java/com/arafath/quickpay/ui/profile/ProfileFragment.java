package com.arafath.quickpay.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.arafath.quickpay.QuickPayApplication;
import com.arafath.quickpay.R;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        QuickPayApplication app = QuickPayApplication.getInstance();
        String name = app.getSessionManager().getUserName();
        String phone = app.getSessionManager().getUserPhone();
        String walletId = app.getSessionManager().getWalletId();

        TextView initial = view.findViewById(R.id.profileInitial);
        if (name != null && !name.isEmpty()) {
            initial.setText(name.substring(0, 1).toUpperCase());
        }
        TextView nameText = view.findViewById(R.id.profileName);
        nameText.setText(name != null ? name : "-");
        TextView phoneText = view.findViewById(R.id.profilePhone);
        phoneText.setText(phone != null ? phone : "-");
        TextView walletText = view.findViewById(R.id.profileWalletId);
        walletText.setText(walletId != null ? walletId : "-");

        view.findViewById(R.id.logoutButton).setOnClickListener(v -> {
            app.getSessionManager().clearSession();
            NavHostFragment.findNavController(ProfileFragment.this)
                    .navigate(R.id.action_profile_to_login);
        });
    }
}