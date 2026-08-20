package com.arafath.quickpay.ui.transaction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.arafath.quickpay.QuickPayApplication;
import com.arafath.quickpay.data.repository.TransactionRepository;
import com.arafath.quickpay.domain.model.Transaction;
import com.arafath.quickpay.domain.model.TransactionStatus;
import com.arafath.quickpay.domain.model.TransactionType;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryViewModel extends ViewModel {

    private final TransactionRepository transactionRepository;
    private final MutableLiveData<List<Transaction>> filtered = new MutableLiveData<>(new ArrayList<>());
    private List<Transaction> all = new ArrayList<>();
    private TransactionFilter filter = TransactionFilter.ALL;

    public enum TransactionFilter {
        ALL, PAYMENTS, SEND, ADD, FAILED, REVERSED
    }

    public TransactionHistoryViewModel() {
        this.transactionRepository = QuickPayApplication.getInstance().getTransactionRepository();
        transactionRepository.observeLocalTransactions().observeForever(transactions -> {
            if (transactions != null) {
                all = transactions;
                applyFilter();
            }
        });
    }

    public LiveData<List<Transaction>> getFiltered() {
        return filtered;
    }

    public void setFilter(TransactionFilter filter) {
        this.filter = filter;
        applyFilter();
    }

    private void applyFilter() {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : all) {
            if (matches(t)) {
                result.add(t);
            }
        }
        filtered.setValue(result);
    }

    private boolean matches(Transaction t) {
        switch (filter) {
            case PAYMENTS:
                return t.getType() == TransactionType.MERCHANT_PAYMENT;
            case SEND:
                return t.getType() == TransactionType.SEND_MONEY;
            case ADD:
                return t.getType() == TransactionType.ADD_MONEY;
            case FAILED:
                return t.getStatus() == TransactionStatus.FAILED;
            case REVERSED:
                return t.getStatus() == TransactionStatus.REVERSED;
            default:
                return true;
        }
    }
}