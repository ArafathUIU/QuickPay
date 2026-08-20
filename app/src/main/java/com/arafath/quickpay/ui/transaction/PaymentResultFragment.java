package com.arafath.quickpay.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.arafath.quickpay.R;
import com.arafath.quickpay.domain.model.Transaction;
import com.arafath.quickpay.domain.model.TransactionStatus;
import com.arafath.quickpay.domain.model.TransactionType;
import com.arafath.quickpay.util.FormatUtils;
import com.arafath.quickpay.util.PaymentResultHolder;

public class PaymentResultFragment extends Fragment {

    private Transaction transaction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        transaction = PaymentResultHolder.get();

        View processingCard = view.findViewById(R.id.processingCard);
        View resultContent = view.findViewById(R.id.resultContent);
        processingCard.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            processingCard.setVisibility(View.GONE);
            resultContent.setVisibility(View.VISIBLE);
            renderResult(view);
        }, 2000);

        view.findViewById(R.id.doneButton).setOnClickListener(v -> {
            PaymentResultHolder.clear();
            NavHostFragment.findNavController(PaymentResultFragment.this)
                    .navigate(R.id.action_result_to_home);
        });

        view.findViewById(R.id.shareButton).setOnClickListener(v -> shareReceipt());
    }

    private void renderResult(View view) {
        TextView statusIcon = view.findViewById(R.id.statusIcon);
        TextView title = view.findViewById(R.id.resultTitle);
        TextView message = view.findViewById(R.id.resultMessage);
        TextView counterparty = view.findViewById(R.id.counterpartyName);
        TextView amount = view.findViewById(R.id.resultAmount);
        TextView txnId = view.findViewById(R.id.resultTxnId);
        TextView date = view.findViewById(R.id.resultDate);
        TextView status = view.findViewById(R.id.resultStatus);
        View reasonRow = view.findViewById(R.id.reasonRow);
        TextView reason = view.findViewById(R.id.resultReason);

        if (transaction == null) {
            title.setText(R.string.error_generic);
            status.setText("-");
            txnId.setText("-");
            date.setText("-");
            amount.setText(FormatUtils.currency(0));
            counterparty.setText("");
            return;
        }

        counterparty.setText(counterpartyName());
        amount.setText(FormatUtils.currency(transaction.getAmount()));
        txnId.setText(transaction.getReference());
        date.setText(FormatUtils.dateTime(transaction.getCreatedAt()));
        status.setText(transaction.getStatus().name());

        switch (transaction.getStatus()) {
            case SUCCESS:
                statusIcon.setText("✓");
                statusIcon.setTextColor(getColor(R.color.success));
                title.setText(R.string.payment_success);
                message.setText("Transaction completed successfully.");
                break;
            case FAILED:
                statusIcon.setText("✕");
                statusIcon.setTextColor(getColor(R.color.danger));
                title.setText(R.string.payment_failed);
                message.setText(transaction.getFailureReason() != null
                        ? transaction.getFailureReason() : "Payment could not be completed.");
                reasonRow.setVisibility(View.VISIBLE);
                reason.setText(transaction.getFailureReason());
                break;
            case REVERSED:
                statusIcon.setText("↺");
                statusIcon.setTextColor(getColor(R.color.warning));
                title.setText(R.string.payment_reversed);
                message.setText("Your payment was reversed and the money has been returned to your wallet.");
                reasonRow.setVisibility(View.VISIBLE);
                reason.setText(getString(R.string.amount_returned, FormatUtils.currency(transaction.getAmount())));
                break;
            default:
                statusIcon.setText("...");
                title.setText(R.string.processing_title);
                message.setText(R.string.processing_subtitle);
        }
    }

    private String counterpartyName() {
        if (transaction == null) {
            return "";
        }
        if (transaction.getType() == TransactionType.MERCHANT_PAYMENT) {
            return transaction.getMerchantName() != null ? transaction.getMerchantName() : "Merchant";
        }
        if (transaction.getType() == TransactionType.ADD_MONEY) {
            return "Added Money";
        }
        return "Send Money";
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

    private int getColor(int res) {
        return androidx.core.content.ContextCompat.getColor(requireContext(), res);
    }
}