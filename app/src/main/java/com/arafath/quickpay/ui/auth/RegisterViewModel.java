package com.arafath.quickpay.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arafath.quickpay.QuickPayApplication;
import com.arafath.quickpay.data.repository.AuthRepository;
import com.arafath.quickpay.data.remote.dto.RegisterRequest;
import com.arafath.quickpay.domain.model.ApiState;
import com.arafath.quickpay.domain.model.User;
import com.arafath.quickpay.util.Validators;

public class RegisterViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<ApiState<User>> registerState = new MutableLiveData<>();

    public RegisterViewModel() {
        this.authRepository = QuickPayApplication.getInstance().getAuthRepository();
    }

    public LiveData<ApiState<User>> getRegisterState() {
        return registerState;
    }

    public void register(String name, String phone, String email, String password,
                         String confirmPassword, String pin) {
        if (!Validators.isValidName(name)) {
            registerState.setValue(ApiState.error("Enter your full name (at least 3 characters)."));
            return;
        }
        if (!Validators.isValidPhone(phone)) {
            registerState.setValue(ApiState.error("Enter a valid 11-digit phone number (e.g. 01XXXXXXXXX)."));
            return;
        }
        if (!Validators.isValidEmail(email)) {
            registerState.setValue(ApiState.error("Enter a valid email address."));
            return;
        }
        if (!Validators.isValidPassword(password)) {
            registerState.setValue(ApiState.error("Password must be at least 6 characters."));
            return;
        }
        if (!password.equals(confirmPassword)) {
            registerState.setValue(ApiState.error("Passwords do not match."));
            return;
        }
        if (!Validators.isValidPin(pin)) {
            registerState.setValue(ApiState.error("Transaction PIN must be 4 digits."));
            return;
        }

        registerState.setValue(ApiState.loading());
        RegisterRequest request = new RegisterRequest(name.trim(), phone.trim(), email.trim(), password, pin);
        authRepository.register(request, new AuthRepository.Callback<User>() {
            @Override
            public void onSuccess(User data) {
                registerState.setValue(ApiState.success(data));
            }

            @Override
            public void onError(String message) {
                registerState.setValue(ApiState.error(message));
            }
        });
    }
}