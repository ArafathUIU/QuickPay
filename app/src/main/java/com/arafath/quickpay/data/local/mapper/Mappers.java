package com.arafath.quickpay.data.local.mapper;

import com.arafath.quickpay.data.local.entity.TransactionEntity;
import com.arafath.quickpay.data.local.entity.UserEntity;
import com.arafath.quickpay.data.local.entity.WalletEntity;
import com.arafath.quickpay.data.remote.dto.AuthResponse;
import com.arafath.quickpay.data.remote.dto.MerchantDto;
import com.arafath.quickpay.data.remote.dto.TransactionDto;
import com.arafath.quickpay.data.remote.dto.UserDto;
import com.arafath.quickpay.data.remote.dto.WalletDto;
import com.arafath.quickpay.domain.model.Merchant;
import com.arafath.quickpay.domain.model.Transaction;
import com.arafath.quickpay.domain.model.TransactionStatus;
import com.arafath.quickpay.domain.model.TransactionType;
import com.arafath.quickpay.domain.model.User;
import com.arafath.quickpay.domain.model.Wallet;

public final class Mappers {

    private Mappers() {
    }

    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return new User(entity.getUserId(), entity.getName(), entity.getPhone(), entity.getEmail());
    }

    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserEntity(user.getUserId(), user.getName(), user.getPhone(), user.getEmail());
    }

    public static User toDomain(UserDto dto) {
        if (dto == null) {
            return null;
        }
        return new User(dto.getId(), dto.getName(), dto.getPhone(), dto.getEmail());
    }

    public static Wallet toDomain(WalletEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Wallet(entity.getWalletId(), entity.getUserId(), entity.getBalance(), entity.getUpdatedAt());
    }

    public static WalletEntity toEntity(Wallet wallet) {
        if (wallet == null) {
            return null;
        }
        return new WalletEntity(wallet.getWalletId(), wallet.getUserId(), wallet.getBalance(), wallet.getUpdatedAt());
    }

    public static Wallet toDomain(WalletDto dto) {
        if (dto == null) {
            return null;
        }
        return new Wallet(dto.getId(), dto.getUserId(), dto.getBalance(), dto.getUpdatedAt());
    }

    public static Transaction toDomain(TransactionEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Transaction(
                entity.getTransactionId(),
                entity.getReference(),
                entity.getType(),
                entity.getAmount(),
                entity.getSenderId(),
                entity.getReceiverId(),
                entity.getMerchantId(),
                entity.getMerchantName(),
                entity.getStatus(),
                entity.getFailureReason(),
                entity.getNote(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static TransactionEntity toEntity(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        return new TransactionEntity(
                transaction.getTransactionId(),
                transaction.getReference(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getSenderId(),
                transaction.getReceiverId(),
                transaction.getMerchantId(),
                transaction.getMerchantName(),
                transaction.getStatus(),
                transaction.getFailureReason(),
                transaction.getNote(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }

    public static Transaction toDomain(TransactionDto dto) {
        if (dto == null) {
            return null;
        }
        Transaction transaction = new Transaction();
        transaction.setTransactionId(dto.getId());
        transaction.setReference(dto.getReference());
        transaction.setType(parseType(dto.getType()));
        transaction.setAmount(dto.getAmount());
        transaction.setSenderId(dto.getSenderId());
        transaction.setReceiverId(dto.getReceiverId());
        transaction.setMerchantId(dto.getMerchantId());
        transaction.setMerchantName(dto.getMerchantName());
        transaction.setStatus(parseStatus(dto.getStatus()));
        transaction.setFailureReason(dto.getFailureReason());
        transaction.setNote(dto.getNote());
        transaction.setCreatedAt(dto.getCreatedAt());
        transaction.setUpdatedAt(dto.getUpdatedAt());
        return transaction;
    }

    public static Merchant toDomain(MerchantDto dto) {
        if (dto == null) {
            return null;
        }
        return new Merchant(dto.getId(), dto.getName(), dto.getCategory(), dto.getStatus());
    }

    public static User userFromAuth(AuthResponse response) {
        if (response == null) {
            return null;
        }
        return toDomain(response.getUser());
    }

    public static Wallet walletFromAuth(AuthResponse response) {
        if (response == null) {
            return null;
        }
        return toDomain(response.getWallet());
    }

    public static TransactionType parseType(String value) {
        if (value == null) {
            return TransactionType.SEND_MONEY;
        }
        try {
            return TransactionType.valueOf(value);
        } catch (IllegalArgumentException e) {
            return TransactionType.SEND_MONEY;
        }
    }

    public static TransactionStatus parseStatus(String value) {
        if (value == null) {
            return TransactionStatus.FAILED;
        }
        try {
            return TransactionStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return TransactionStatus.FAILED;
        }
    }
}