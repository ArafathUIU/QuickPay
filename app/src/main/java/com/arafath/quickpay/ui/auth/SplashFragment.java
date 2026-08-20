package com.arafath.quickpay.ui.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.arafath.quickpay.QuickPayApplication;
import com.arafath.quickpay.R;
import com.arafath.quickpay.util.SessionManager;

public class SplashFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SessionManager session = QuickPayApplication.getInstance().getSessionManager();
        boolean loggedIn = session.isLoggedIn();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            NavController navController = NavHostFragment.findNavController(this);
            int destination = loggedIn ? R.id.action_splash_to_home : R.id.action_splash_to_login;
            navController.navigate(destination);
        }, 1500);
    }
}