package com.arafath.quickpay.ui.transaction;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

            // Celebration animation for successful payments
            if (transaction != null && transaction.getStatus() == TransactionStatus.SUCCESS) {
                celebrateSuccess(view);
            }
        }, 2000);
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
                statusIcon.setTextColor(ContextCompat.getColor(requireContext(), R.color.success));
                tintCircle(view, R.color.success_light);
                title.setText(R.string.payment_success);
                message.setText("Transaction completed successfully.");
                break;
            case FAILED:
                statusIcon.setText("✕");
                statusIcon.setTextColor(ContextCompat.getColor(requireContext(), R.color.danger));
                tintCircle(view, R.color.danger_light);
                title.setText(R.string.payment_failed);
                message.setText(transaction.getFailureReason() != null
                        ? transaction.getFailureReason() : "Payment could not be completed.");
                reasonRow.setVisibility(View.VISIBLE);
                reason.setText(transaction.getFailureReason());
                break;
            case REVERSED:
                statusIcon.setText("↺");
                statusIcon.setTextColor(ContextCompat.getColor(requireContext(), R.color.warning));
                tintCircle(view, R.color.warning_light);
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

    private void tintCircle(View view, int colorRes) {
        View circle = view.findViewById(R.id.statusCircle);
        circle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), colorRes)));
    }

    private void celebrateSuccess(View view) {
        TextView statusIcon = view.findViewById(R.id.statusIcon);
        // Scale up bounce animation
        statusIcon.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(300)
                .setStartDelay(500)
                .withEndAction(() -> statusIcon.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(300))
                .start();
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
}