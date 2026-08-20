package com.arafath.quickpay.ui.wallet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arafath.quickpay.QuickPayApplication;
import com.arafath.quickpay.data.repository.TransactionRepository;
import com.arafath.quickpay.data.repository.WalletRepository;
import com.arafath.quickpay.domain.model.ApiState;
import com.arafath.quickpay.domain.model.Transaction;
import com.arafath.quickpay.domain.usecase.ValidatePaymentUseCase;
import com.arafath.quickpay.util.PaymentResultHolder;

public class AddMoneyViewModel extends ViewModel {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final MutableLiveData<ApiState<Transaction>> state = new MutableLiveData<>();

    public AddMoneyViewModel() {
        QuickPayApplication app = QuickPayApplication.getInstance();
        this.walletRepository = app.getWalletRepository();
        this.transactionRepository = app.getTransactionRepository();
    }

    public LiveData<ApiState<Transaction>> getState() {
        return state;
    }

    public int validateAmount(double amount) {
        return ValidatePaymentUseCase.validateAmount(amount);
    }

    public void execute(double amount, String pin) {
        int pinCode = ValidatePaymentUseCase.validatePin(pin);
        if (pinCode != ValidatePaymentUseCase.OK) {
            state.setValue(ApiState.error(ValidatePaymentUseCase.messageFor(pinCode)));
            return;
        }
        state.setValue(ApiState.loading());
        walletRepository.addMoney(amount, pin, new WalletRepository.Callback<Transaction>() {
            @Override
            public void onSuccess(Transaction data) {
                transactionRepository.persist(data);
                PaymentResultHolder.set(data);
                state.setValue(ApiState.success(data));
            }

            @Override
            public void onError(String message) {
                state.setValue(ApiState.error(message));
            }
        });
    }
}