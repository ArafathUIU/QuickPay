package com.arafath.quickpay.ui.receive;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.arafath.quickpay.QuickPayApplication;
import com.arafath.quickpay.R;
import com.arafath.quickpay.domain.usecase.QrParser;
import com.arafath.quickpay.util.SessionManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.HashMap;
import java.util.Map;

public class ReceiveFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_receive, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SessionManager session = QuickPayApplication.getInstance().getSessionManager();

        TextView userName = view.findViewById(R.id.userName);
        TextView userPhone = view.findViewById(R.id.userPhone);
        TextView walletId = view.findViewById(R.id.walletId);
        ImageView qrImage = view.findViewById(R.id.qrImage);

        userName.setText(session.getUserName());
        userPhone.setText(session.getUserPhone());
        walletId.setText("Wallet ID: " + (session.getWalletId() != null ? session.getWalletId() : "—"));

        String qrContent = QrParser.buildUserQr(session.getUserId());
        Bitmap bitmap = generateQr(qrContent, 480);
        if (bitmap != null) {
            qrImage.setImageBitmap(bitmap);
        }
    }

    private Bitmap generateQr(String content, int size) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix matrix = new MultiFormatWriter().encode(
                    content, BarcodeFormat.QR_CODE, size, size, hints);
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, matrix.get(x, y) ? android.graphics.Color.BLACK
                            : android.graphics.Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            return null;
        }
    }
}