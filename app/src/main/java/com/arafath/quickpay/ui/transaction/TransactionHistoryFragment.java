package com.arafath.quickpay.ui.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arafath.quickpay.R;
import com.arafath.quickpay.domain.model.Transaction;
import com.arafath.quickpay.ui.transaction.TransactionHistoryViewModel.TransactionFilter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class TransactionHistoryFragment extends Fragment {

    private TransactionHistoryViewModel viewModel;
    private TransactionAdapter adapter;
    private TextView emptyText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TransactionHistoryViewModel.class);

        RecyclerView list = view.findViewById(R.id.transactionList);
        emptyText = view.findViewById(R.id.emptyText);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TransactionAdapter();
        adapter.setOnItemClickListener(transaction -> {
            Bundle args = new Bundle();
            args.putString("transaction_id", transaction.getTransactionId());
            NavHostFragment.findNavController(TransactionHistoryFragment.this)
                    .navigate(R.id.action_history_to_detail, args);
        });
        list.setAdapter(adapter);

        ChipGroup filterGroup = view.findViewById(R.id.filterGroup);
        bindChip(view, filterGroup, R.id.filterAll, TransactionFilter.ALL);
        bindChip(view, filterGroup, R.id.filterPayments, TransactionFilter.PAYMENTS);
        bindChip(view, filterGroup, R.id.filterSend, TransactionFilter.SEND);
        bindChip(view, filterGroup, R.id.filterAdd, TransactionFilter.ADD);
        bindChip(view, filterGroup, R.id.filterFailed, TransactionFilter.FAILED);
        bindChip(view, filterGroup, R.id.filterReversed, TransactionFilter.REVERSED);

        viewModel.getFiltered().observe(getViewLifecycleOwner(), this::render);
    }

    private void bindChip(View view, ChipGroup group, int chipId, TransactionFilter filter) {
        Chip chip = view.findViewById(chipId);
        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                viewModel.setFilter(filter);
            }
        });
    }

    private void render(List<Transaction> transactions) {
        adapter.setData(transactions);
        emptyText.setVisibility(transactions == null || transactions.isEmpty()
                ? View.VISIBLE : View.GONE);
    }
}