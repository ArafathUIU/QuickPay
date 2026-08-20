package com.arafath.quickpay.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.arafath.quickpay.data.local.dao.TransactionDao;
import com.arafath.quickpay.data.local.mapper.Mappers;
import com.arafath.quickpay.data.remote.api.TransactionApi;
import com.arafath.quickpay.data.remote.dto.SendMoneyRequest;
import com.arafath.quickpay.data.remote.interceptor.ApiErrorParser;
import com.arafath.quickpay.domain.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class TransactionRepository {

    private final TransactionApi transactionApi;
    private final TransactionDao transactionDao;
    private final MutableLiveData<List<Transaction>> localTransactions = new MutableLiveData<>(new ArrayList<>());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TransactionRepository(TransactionApi transactionApi, TransactionDao transactionDao) {
        this.transactionApi = transactionApi;
        this.transactionDao = transactionDao;
    }

    public LiveData<List<Transaction>> observeLocalTransactions() {
        return localTransactions;
    }

    public void loadLocalTransactions() {
        executor.execute(() -> {
            List<Transaction> list = new ArrayList<>();
            for (com.arafath.quickpay.data.local.entity.TransactionEntity entity : transactionDao.getAll()) {
                list.add(Mappers.toDomain(entity));
            }
            localTransactions.postValue(list);
        });
    }

    public void syncFromServer(Callback<List<Transaction>> callback) {
        executor.execute(() -> {
            try {
                Response<List<com.arafath.quickpay.data.remote.dto.TransactionDto>> response =
                        transactionApi.getTransactions().execute();
                if (response.isSuccessful() && response.body() != null) {
                    List<Transaction> list = new ArrayList<>();
                    for (com.arafath.quickpay.data.remote.dto.TransactionDto dto : response.body()) {
                        Transaction transaction = Mappers.toDomain(dto);
                        list.add(transaction);
                        transactionDao.upsert(Mappers.toEntity(transaction));
                    }
                    localTransactions.postValue(list);
                    callback.onSuccess(list);
                } else {
                    callback.onError(ApiErrorParser.parseError(response));
                }
            } catch (Exception e) {
                callback.onError(ApiErrorParser.parseThrowable(e));
            }
        });
    }

    public void sendMoney(String receiverId, String receiverPhone, double amount, String note, String pin,
                          Callback<Transaction> callback) {
        executor.execute(() -> {
            try {
                Response<com.arafath.quickpay.data.remote.dto.TransactionDto> response =
                        transactionApi.sendMoney(
                                new SendMoneyRequest(receiverPhone, receiverId, amount, note, pin)
                        ).execute();
                if (response.isSuccessful() && response.body() != null) {
                    Transaction transaction = Mappers.toDomain(response.body());
                    persist(transaction);
                    callback.onSuccess(transaction);
                } else {
                    callback.onError(ApiErrorParser.parseError(response));
                }
            } catch (Exception e) {
                callback.onError(ApiErrorParser.parseThrowable(e));
            }
        });
    }

    public void persist(Transaction transaction) {
        if (transaction == null) {
            return;
        }
        executor.execute(() -> {
            transactionDao.upsert(Mappers.toEntity(transaction));
            loadLocalTransactions();
        });
    }

    public Transaction getCached(String transactionId) {
        return Mappers.toDomain(transactionDao.getById(transactionId));
    }

    public interface Callback<T> {
        void onSuccess(T data);

        void onError(String message);
    }
}