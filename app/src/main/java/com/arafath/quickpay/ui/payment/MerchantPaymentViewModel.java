package com.arafath.quickpay.ui.payment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arafath.quickpay.QuickPayApplication;
import com.arafath.quickpay.data.repository.MerchantRepository;
import com.arafath.quickpay.data.repository.TransactionRepository;
import com.arafath.quickpay.data.repository.WalletRepository;
import com.arafath.quickpay.domain.model.ApiState;
import com.arafath.quickpay.domain.model.Merchant;
import com.arafath.quickpay.domain.model.Transaction;
import com.arafath.quickpay.domain.model.Wallet;
import com.arafath.quickpay.domain.usecase.ValidatePaymentUseCase;
import com.arafath.quickpay.util.PaymentResultHolder;

public class MerchantPaymentViewModel extends ViewModel {

    private final MerchantRepository merchantRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    private final MutableLiveData<ApiState<Merchant>> validationState = new MutableLiveData<>();
    private final MutableLiveData<ApiState<Transaction>> paymentState = new MutableLiveData<>();
    private final MutableLiveData<Double> balance = new MutableLiveData<>(0.0);

    public MerchantPaymentViewModel() {
        QuickPayApplication app = QuickPayApplication.getInstance();
        this.merchantRepository = app.getMerchantRepository();
        this.walletRepository = app.getWalletRepository();
        this.transactionRepository = app.getTransactionRepository();
        walletRepository.getCachedWalletAsync(new WalletRepository.Callback<Wallet>() {
            @Override
            public void onSuccess(Wallet data) {
                if (data != null) {
                    balance.setValue(data.getBalance());
                }
            }

            @Override
            public void onError(String message) {
            }
        });
    }

    public LiveData<ApiState<Merchant>> getValidationState() {
        return validationState;
    }

    public LiveData<ApiState<Transaction>> getPaymentState() {
        return paymentState;
    }

    public LiveData<Double> getBalance() {
        return balance;
    }

    public void validate(String merchantId) {
        validationState.setValue(ApiState.loading());
        merchantRepository.validateMerchant(merchantId, new MerchantRepository.Callback<Merchant>() {
            @Override
            public void onSuccess(Merchant data) {
                validationState.setValue(ApiState.success(data));
            }

            @Override
            public void onError(String message) {
                validationState.setValue(ApiState.error(message));
            }
        });
    }

    public void execute(Merchant merchant, double amount, String pin) {
        int amountCode = ValidatePaymentUseCase.validateAmount(amount);
        if (amountCode != ValidatePaymentUseCase.OK) {
            paymentState.setValue(ApiState.error(ValidatePaymentUseCase.messageFor(amountCode)));
            return;
        }
        Double current = balance.getValue();
        if (current != null) {
            int balanceCode = ValidatePaymentUseCase.validateBalance(amount, current);
            if (balanceCode != ValidatePaymentUseCase.OK) {
                paymentState.setValue(ApiState.error(ValidatePaymentUseCase.messageFor(balanceCode)));
                return;
            }
        }
        int pinCode = ValidatePaymentUseCase.validatePin(pin);
        if (pinCode != ValidatePaymentUseCase.OK) {
            paymentState.setValue(ApiState.error(ValidatePaymentUseCase.messageFor(pinCode)));
            return;
        }

        paymentState.setValue(ApiState.loading());
        merchantRepository.pay(amount, merchant.getMerchantId(), pin, new MerchantRepository.Callback<Transaction>() {
            @Override
            public void onSuccess(Transaction data) {
                transactionRepository.persist(data);
                PaymentResultHolder.set(data);
                paymentState.setValue(ApiState.success(data));
            }

            @Override
            public void onError(String message) {
                paymentState.setValue(ApiState.error(message));
            }
        });
    }
}