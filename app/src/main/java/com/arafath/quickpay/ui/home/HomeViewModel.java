package com.arafath.quickpay.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arafath.quickpay.QuickPayApplication;
import com.arafath.quickpay.data.repository.TransactionRepository;
import com.arafath.quickpay.data.repository.WalletRepository;
import com.arafath.quickpay.domain.model.Transaction;
import com.arafath.quickpay.domain.model.User;
import com.arafath.quickpay.domain.model.Wallet;
import com.arafath.quickpay.util.SessionManager;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final SessionManager sessionManager;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    private final MutableLiveData<String> greeting = new MutableLiveData<>();
    private final MutableLiveData<Double> balance = new MutableLiveData<>(0.0);
    private final MutableLiveData<Boolean> refreshDone = new MutableLiveData<>(false);

    public HomeViewModel() {
        QuickPayApplication app = QuickPayApplication.getInstance();
        sessionManager = app.getSessionManager();
        walletRepository = app.getWalletRepository();
        transactionRepository = app.getTransactionRepository();

        greeting.setValue("Hello, " + (sessionManager.getUserName() != null ? sessionManager.getUserName() : "there") + " 👋");
        transactionRepository.loadLocalTransactions();
    }

    public LiveData<String> getGreeting() {
        return greeting;
    }

    public LiveData<Double> getBalance() {
        return balance;
    }

    public LiveData<List<Transaction>> getRecentTransactions() {
        return transactionRepository.observeLocalTransactions();
    }

    public LiveData<Boolean> getRefreshDone() {
        return refreshDone;
    }

    public User getCachedUser() {
        return QuickPayApplication.getInstance().getAuthRepository().getCachedUser();
    }

    public void load() {
        loadBalance();
        syncTransactions();
    }

    public void refresh() {
        loadBalance();
        syncTransactions();
        refreshDone.setValue(true);
    }

    public void logout() {
        sessionManager.clearSession();
    }

    private void loadBalance() {
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
        walletRepository.fetchWallet(new WalletRepository.Callback<Wallet>() {
            @Override
            public void onSuccess(Wallet data) {
                balance.setValue(data.getBalance());
            }

            @Override
            public void onError(String message) {
            }
        });
    }

    private void syncTransactions() {
        transactionRepository.syncFromServer(new TransactionRepository.Callback<List<Transaction>>() {
            @Override
            public void onSuccess(List<Transaction> data) {
            }

            @Override
            public void onError(String message) {
                transactionRepository.loadLocalTransactions();
            }
        });
    }
}