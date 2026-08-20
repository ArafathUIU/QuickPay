package com.arafath.quickpay;

import android.app.Application;

import com.arafath.quickpay.data.local.database.AppDatabase;
import com.arafath.quickpay.data.remote.network.ApiClient;
import com.arafath.quickpay.data.repository.AuthRepository;
import com.arafath.quickpay.data.repository.MerchantRepository;
import com.arafath.quickpay.data.repository.TransactionRepository;
import com.arafath.quickpay.data.repository.UserRepository;
import com.arafath.quickpay.data.repository.WalletRepository;
import com.arafath.quickpay.util.SessionManager;

public class QuickPayApplication extends Application {

    private static QuickPayApplication instance;

    private SessionManager sessionManager;
    private ApiClient apiClient;
    private AppDatabase database;

    private AuthRepository authRepository;
    private WalletRepository walletRepository;
    private TransactionRepository transactionRepository;
    private MerchantRepository merchantRepository;
    private UserRepository userRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sessionManager = new SessionManager(this);
        database = AppDatabase.getInstance(this);
        apiClient = new ApiClient(sessionManager);

        authRepository = new AuthRepository(apiClient.getAuthApi(), database.userDao(), sessionManager);
        walletRepository = new WalletRepository(apiClient.getWalletApi(), database.walletDao(), sessionManager);
        transactionRepository = new TransactionRepository(apiClient.getTransactionApi(), database.transactionDao());
        merchantRepository = new MerchantRepository(apiClient.getMerchantApi());
        userRepository = new UserRepository(apiClient.getUserApi());
    }

    public static QuickPayApplication getInstance() {
        return instance;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public AuthRepository getAuthRepository() {
        return authRepository;
    }

    public WalletRepository getWalletRepository() {
        return walletRepository;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public MerchantRepository getMerchantRepository() {
        return merchantRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}