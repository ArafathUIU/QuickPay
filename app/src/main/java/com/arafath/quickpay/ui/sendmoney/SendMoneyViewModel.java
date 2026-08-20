package com.arafath.quickpay.ui.sendmoney;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arafath.quickpay.QuickPayApplication;
import com.arafath.quickpay.data.repository.TransactionRepository;
import com.arafath.quickpay.data.repository.UserRepository;
import com.arafath.quickpay.domain.model.ApiState;
import com.arafath.quickpay.domain.model.Transaction;
import com.arafath.quickpay.domain.model.User;
import com.arafath.quickpay.domain.usecase.ValidatePaymentUseCase;
import com.arafath.quickpay.util.PaymentResultHolder;
import com.arafath.quickpay.util.SessionManager;
import com.arafath.quickpay.util.Validators;

public class SendMoneyViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final SessionManager sessionManager;

    private final MutableLiveData<ApiState<User>> searchState = new MutableLiveData<>();
    private final MutableLiveData<ApiState<Transaction>> sendState = new MutableLiveData<>();

    public SendMoneyViewModel() {
        QuickPayApplication app = QuickPayApplication.getInstance();
        this.userRepository = app.getUserRepository();
        this.transactionRepository = app.getTransactionRepository();
        this.sessionManager = app.getSessionManager();
    }

    public LiveData<ApiState<User>> getSearchState() {
        return searchState;
    }

    public LiveData<ApiState<Transaction>> getSendState() {
        return sendState;
    }

    public void search(String phone) {
        if (!Validators.isValidPhone(phone)) {
            searchState.setValue(ApiState.error("Enter a valid recipient phone number."));
            return;
        }
        if (phone.equals(sessionManager.getUserPhone())) {
            searchState.setValue(ApiState.error("You cannot send money to yourself."));
            return;
        }
        searchState.setValue(ApiState.loading());
        userRepository.searchByPhone(phone.trim(), new UserRepository.Callback<User>() {
            @Override
            public void onSuccess(User data) {
                searchState.setValue(ApiState.success(data));
            }

            @Override
            public void onError(String message) {
                searchState.setValue(ApiState.error(message));
            }
        });
    }

    public void execute(User recipient, double amount, String note, String pin) {
        int pinCode = ValidatePaymentUseCase.validatePin(pin);
        if (pinCode != ValidatePaymentUseCase.OK) {
            sendState.setValue(ApiState.error(ValidatePaymentUseCase.messageFor(pinCode)));
            return;
        }
        sendState.setValue(ApiState.loading());
        transactionRepository.sendMoney(
                recipient.getUserId(),
                recipient.getPhone(),
                amount,
                note,
                pin,
                new TransactionRepository.Callback<Transaction>() {
                    @Override
                    public void onSuccess(Transaction data) {
                        PaymentResultHolder.set(data);
                        sendState.setValue(ApiState.success(data));
                    }

                    @Override
                    public void onError(String message) {
                        sendState.setValue(ApiState.error(message));
                    }
                });
    }
}