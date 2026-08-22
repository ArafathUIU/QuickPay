package com.arafath.quickpay.ui.transaction;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.arafath.quickpay.R;
import com.arafath.quickpay.domain.model.Transaction;
import com.arafath.quickpay.domain.model.TransactionStatus;
import com.arafath.quickpay.domain.model.TransactionType;
import com.arafath.quickpay.util.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    private final List<Transaction> items = new ArrayList<>();
    private OnItemClickListener listener;

    public void setData(List<Transaction> transactions) {
        items.clear();
        if (transactions != null) {
            items.addAll(transactions);
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = items.get(position);
        holder.icon.setImageResource(com.arafath.quickpay.util.TxnVisuals.iconRes(transaction.getType()));
        holder.icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(
                holder.itemView.getContext(), com.arafath.quickpay.util.TxnVisuals.iconTintRes(transaction.getType()))));
        holder.iconBg.setBackgroundResource(com.arafath.quickpay.util.TxnVisuals.circleBgRes(transaction.getType()));
        holder.title.setText(com.arafath.quickpay.util.TxnVisuals.titleFor(transaction));
        holder.subtitle.setText(FormatUtils.dateTime(transaction.getCreatedAt()));
        holder.amount.setText(signedAmount(transaction));
        holder.amount.setTextColor(amountColor(holder, transaction));
        holder.status.setText(transaction.getStatus().name());
        holder.status.setTextColor(statusColor(holder, transaction.getStatus()));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(transaction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
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

    private int amountColor(ViewHolder holder, Transaction transaction) {
        boolean inflow = transaction.getType() == TransactionType.ADD_MONEY
                || transaction.getType() == TransactionType.RECEIVE_MONEY;
        return ContextCompat.getColor(holder.itemView.getContext(),
                inflow ? R.color.success : R.color.text_primary);
    }

    private int statusColor(ViewHolder holder, TransactionStatus status) {
        int res;
        switch (status) {
            case SUCCESS:
                res = R.color.success;
                break;
            case FAILED:
                res = R.color.danger;
                break;
            case REVERSED:
                res = R.color.warning;
                break;
            default:
                res = R.color.text_secondary;
        }
        return ContextCompat.getColor(holder.itemView.getContext(), res);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final android.widget.ImageView icon;
        final View iconBg;
        final TextView title;
        final TextView subtitle;
        final TextView amount;
        final TextView status;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.itemIcon);
            iconBg = itemView.findViewById(R.id.itemIconBg);
            title = itemView.findViewById(R.id.itemTitle);
            subtitle = itemView.findViewById(R.id.itemSubtitle);
            amount = itemView.findViewById(R.id.itemAmount);
            status = itemView.findViewById(R.id.itemStatus);
        }
    }
}