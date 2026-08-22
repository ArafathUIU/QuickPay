package com.arafath.quickpay.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.arafath.quickpay.QuickPayApplication;
import com.arafath.quickpay.R;
import com.arafath.quickpay.domain.model.Transaction;
import com.arafath.quickpay.domain.model.TransactionStatus;
import com.arafath.quickpay.domain.model.TransactionType;
import com.arafath.quickpay.util.Constants;
import com.arafath.quickpay.util.FormatUtils;

public class TransactionDetailFragment extends Fragment {

    private Transaction transaction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String txnId = getArguments() != null ? getArguments().getString(Constants.KEY_TXN_ID) : null;
        if (txnId != null) {
            QuickPayApplication.getInstance().getTransactionRepository()
                    .getCachedAsync(txnId, new com.arafath.quickpay.data.repository.TransactionRepository.Callback<Transaction>() {
                        @Override
                        public void onSuccess(Transaction data) {
                            transaction = data;
                            if (getView() != null) {
                                render(getView());
                            }
                        }

                        @Override
                        public void onError(String message) {
                        }
                    });
        }
        render(view);

        view.findViewById(R.id.shareButton).setOnClickListener(v -> shareReceipt());
    }

    private void render(View view) {
        TextView title = view.findViewById(R.id.detailTitle);
        TextView counterparty = view.findViewById(R.id.detailCounterparty);
        TextView amount = view.findViewById(R.id.detailAmount);
        TextView message = view.findViewById(R.id.detailMessage);
        TextView txnAmount = view.findViewById(R.id.detailTxnAmount);
        TextView txnId = view.findViewById(R.id.detailTxnId);
        TextView date = view.findViewById(R.id.detailDate);
        TextView status = view.findViewById(R.id.detailStatus);

        if (transaction == null) {
            title.setText(R.string.error_generic);
            counterparty.setText("");
            amount.setText(FormatUtils.currency(0));
            txnAmount.setText("-");
            txnId.setText("-");
            date.setText("-");
            status.setText("-");
            return;
        }

        switch (transaction.getStatus()) {
            case SUCCESS:
                title.setText(R.string.payment_success);
                message.setText("Transaction completed successfully.");
                break;
            case FAILED:
                title.setText(R.string.payment_failed);
                message.setText(transaction.getFailureReason() != null
                        ? transaction.getFailureReason() : "Payment could not be completed.");
                break;
            case REVERSED:
                title.setText(R.string.payment_reversed);
                message.setText("The payment could not be completed. Money was returned to your wallet.");
                break;
            default:
                title.setText(R.string.processing_title);
                message.setText(R.string.processing_subtitle);
        }

        counterparty.setText(counterpartyName());
        amount.setText(FormatUtils.currency(transaction.getAmount()));
        amount.setTextColor(statusColor());
        txnAmount.setText(FormatUtils.amount(transaction.getAmount()));
        txnId.setText(transaction.getReference());
        date.setText(FormatUtils.dateTime(transaction.getCreatedAt()));
        status.setText(transaction.getStatus().name());
        status.setTextColor(statusColor());
    }

    private String counterpartyName() {
        if (transaction.getType() == TransactionType.MERCHANT_PAYMENT) {
            return transaction.getMerchantName() != null ? transaction.getMerchantName() : "Merchant";
        }
        if (transaction.getType() == TransactionType.ADD_MONEY) {
            return "Added Money";
        }
        return "Send Money";
    }

    private int statusColor() {
        int res = R.color.text_primary;
        if (transaction.getStatus() == TransactionStatus.SUCCESS) {
            res = R.color.success;
        } else if (transaction.getStatus() == TransactionStatus.FAILED) {
            res = R.color.danger;
        } else if (transaction.getStatus() == TransactionStatus.REVERSED) {
            res = R.color.warning;
        }
        return ContextCompat.getColor(requireContext(), res);
    }

    private void shareReceipt() {
        if (transaction == null) {
            return;
        }
        String text = "QuickPay Receipt\n"
                + "Type: " + transaction.getType() + "\n"
                + "Amount: " + FormatUtils.currency(transaction.getAmount()) + "\n"
                + "Status: " + transaction.getStatus() + "\n"
                + "Reference: " + transaction.getReference() + "\n"
                + "Date: " + FormatUtils.dateTime(transaction.getCreatedAt());
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(send, "Share receipt"));
    }
}