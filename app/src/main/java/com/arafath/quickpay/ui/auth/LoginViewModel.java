package com.arafath.quickpay.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arafath.quickpay.QuickPayApplication;
import com.arafath.quickpay.data.repository.AuthRepository;
import com.arafath.quickpay.domain.model.ApiState;
import com.arafath.quickpay.util.Validators;

public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<ApiState<Void>> loginState = new MutableLiveData<>();

    public LoginViewModel() {
        this.authRepository = QuickPayApplication.getInstance().getAuthRepository();
    }

    public LiveData<ApiState<Void>> getLoginState() {
        return loginState;
    }

    public void login(String phone, String password) {
        if (phone == null || phone.trim().isEmpty() || !Validators.isValidPhone(phone)) {
            loginState.setValue(ApiState.error("Enter a valid phone number."));
            return;
        }
        if (password == null || password.isEmpty()) {
            loginState.setValue(ApiState.error("Enter your password."));
            return;
        }

        loginState.setValue(ApiState.loading());
        authRepository.login(phone.trim(), password, new AuthRepository.Callback<AuthRepository.LoginResult>() {
            @Override
            public void onSuccess(AuthRepository.LoginResult data) {
                loginState.setValue(ApiState.success(null));
            }

            @Override
            public void onError(String message) {
                loginState.setValue(ApiState.error(message));
            }
        });
    }
}