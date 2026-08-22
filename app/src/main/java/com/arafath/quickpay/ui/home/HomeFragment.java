package com.arafath.quickpay.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.arafath.quickpay.QuickPayApplication;
import com.arafath.quickpay.R;
import com.arafath.quickpay.domain.model.Transaction;
import com.arafath.quickpay.domain.model.TransactionStatus;
import com.arafath.quickpay.domain.model.TransactionType;
import com.arafath.quickpay.domain.usecase.QrParser;
import com.arafath.quickpay.util.Constants;
import com.arafath.quickpay.util.FormatUtils;
import com.google.android.material.snackbar.Snackbar;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private TextView balanceText;
    private LinearLayout recentList;
    private View emptyRecentText;
    private Double lastBalance;
    private boolean balanceHidden = false;
    private final ActivityResultLauncher<ScanOptions> scanLauncher = registerForActivityResult(
            new ScanContract(), result -> {
                if (result.getContents() != null) {
                    handleScannedQr(result.getContents());
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        balanceText = view.findViewById(R.id.balanceText);
        recentList = view.findViewById(R.id.recentList);
        emptyRecentText = view.findViewById(R.id.emptyRecentText);

        view.findViewById(R.id.addMoneyCard).setOnClickListener(v ->
                navigate(R.id.action_home_to_addMoney));
        view.findViewById(R.id.sendMoneyCard).setOnClickListener(v ->
                navigate(R.id.action_home_to_sendMoney));
        view.findViewById(R.id.receiveCard).setOnClickListener(v ->
                navigate(R.id.action_home_to_receive));
        view.findViewById(R.id.scanCard).setOnClickListener(v -> startScan());
        view.findViewById(R.id.viewAllText).setOnClickListener(v ->
                navigate(R.id.action_home_to_history));
        view.findViewById(R.id.notificationButton).setOnClickListener(v ->
                Snackbar.make(view, R.string.notifications_none, Snackbar.LENGTH_SHORT).show());

        ImageView balanceToggle = view.findViewById(R.id.balanceToggle);
        balanceToggle.setOnClickListener(v -> {
            balanceHidden = !balanceHidden;
            balanceToggle.setImageResource(balanceHidden
                    ? R.drawable.ic_visibility_off : R.drawable.ic_visibility);
            renderBalance();
        });

        com.google.android.material.floatingactionbutton.FloatingActionButton fabScan =
                view.findViewById(R.id.fabScan);
        fabScan.setOnClickListener(v -> startScan());

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                view.findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_history) {
                navigate(R.id.action_home_to_history);
                return false;
            } else if (id == R.id.menu_profile) {
                navigate(R.id.action_home_to_profile);
                return false;
            }
            return true;
        });

        androidx.swiperefreshlayout.widget.SwipeRefreshLayout refresh =
                view.findViewById(R.id.refreshLayout);
        refresh.setOnRefreshListener(() -> {
            viewModel.refresh();
            refresh.setRefreshing(false);
        });

        viewModel.getGreeting().observe(getViewLifecycleOwner(), text -> {
            TextView greeting = view.findViewById(R.id.greetingText);
            greeting.setText(text);
        });

        viewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {
            lastBalance = balance;
            renderBalance();
        });

        viewModel.getRecentTransactions().observe(getViewLifecycleOwner(), this::renderTransactions);

        String phone = QuickPayApplication.getInstance().getSessionManager().getUserPhone();
        TextView phoneText = view.findViewById(R.id.phoneText);
        if (phone != null) {
            phoneText.setText(phone);
        }

        String name = QuickPayApplication.getInstance().getSessionManager().getUserName();
        TextView avatarInitial = view.findViewById(R.id.avatarInitial);
        if (name != null && !name.isEmpty()) {
            avatarInitial.setText(name.substring(0, 1).toUpperCase());
        }

        viewModel.load();
    }

    private void renderBalance() {
        if (balanceHidden) {
            balanceText.setText("৳ ••••••");
        } else if (lastBalance != null) {
            balanceText.setText(FormatUtils.currency(lastBalance));
        }
    }

    private void renderTransactions(List<Transaction> transactions) {
        recentList.removeAllViews();
        if (transactions == null || transactions.isEmpty()) {
            emptyRecentText.setVisibility(View.VISIBLE);
            return;
        }
        emptyRecentText.setVisibility(View.GONE);
        int count = Math.min(transactions.size(), 5);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (int i = 0; i < count; i++) {
            recentList.addView(buildItemView(inflater, transactions.get(i)));
        }
    }

    private View buildItemView(LayoutInflater inflater, Transaction transaction) {
        View item = inflater.inflate(R.layout.item_transaction, recentList, false);
        TextView title = item.findViewById(R.id.itemTitle);
        TextView subtitle = item.findViewById(R.id.itemSubtitle);
        TextView amount = item.findViewById(R.id.itemAmount);
        TextView status = item.findViewById(R.id.itemStatus);

        title.setText(titleFor(transaction));
        subtitle.setText(FormatUtils.dateTime(transaction.getCreatedAt()));
        amount.setText(signedAmount(transaction));
        amount.setTextColor(amountColor(transaction));
        status.setText(transaction.getStatus().name());
        status.setTextColor(statusColor(transaction.getStatus()));

        item.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString(Constants.KEY_TXN_ID, transaction.getTransactionId());
            navigate(R.id.action_history_to_detail, args);
        });
        return item;
    }

    private void startScan() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan a QuickPay QR code");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(false);
        scanLauncher.launch(options);
    }

    private void handleScannedQr(String raw) {
        QrParser.QrData data = QrParser.parse(raw);
        if (data == null) {
            Snackbar.make(requireView(), R.string.error_invalid_qr, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (data.isMerchant()) {
            Bundle args = new Bundle();
            args.putString(Constants.KEY_QR_DATA, raw);
            navigate(R.id.action_home_to_merchantPayment, args);
        } else if (data.isUser()) {
            Bundle args = new Bundle();
            args.putString(Constants.KEY_QR_DATA, raw);
            navigate(R.id.action_home_to_sendMoney, args);
        } else {
            Snackbar.make(requireView(), R.string.error_invalid_qr, Snackbar.LENGTH_LONG).show();
        }
    }

    private String titleFor(Transaction transaction) {
        if (transaction.getType() == TransactionType.MERCHANT_PAYMENT) {
            return transaction.getMerchantName() != null ? transaction.getMerchantName() : "Merchant Payment";
        }
        if (transaction.getType() == TransactionType.ADD_MONEY) {
            return "Added Money";
        }
        return transaction.getNote() != null && !transaction.getNote().isEmpty()
                ? transaction.getNote() : "Send Money";
    }

    private String signedAmount(Transaction transaction) {
        boolean inflow = transaction.getType() == TransactionType.ADD_MONEY
                || transaction.getType() == TransactionType.RECEIVE_MONEY;
        String prefix = inflow ? "+" : "-";
        if (transaction.getStatus() == TransactionStatus.REVERSED) {
            return "0.00";
        }
        return prefix + FormatUtils.amount(transaction.getAmount());
    }

    private int amountColor(Transaction transaction) {
        boolean inflow = transaction.getType() == TransactionType.ADD_MONEY
                || transaction.getType() == TransactionType.RECEIVE_MONEY;
        return getColor(inflow ? R.color.success : R.color.text_primary);
    }

    private int statusColor(TransactionStatus status) {
        switch (status) {
            case SUCCESS:
                return getColor(R.color.success);
            case FAILED:
                return getColor(R.color.danger);
            case REVERSED:
                return getColor(R.color.warning);
            default:
                return getColor(R.color.text_secondary);
        }
    }

    private int getColor(int res) {
        return androidx.core.content.ContextCompat.getColor(requireContext(), res);
    }

    private void navigate(int actionId) {
        NavHostFragment.findNavController(HomeFragment.this).navigate(actionId);
    }

    private void navigate(int actionId, Bundle args) {
        NavHostFragment.findNavController(HomeFragment.this).navigate(actionId, args);
    }
}