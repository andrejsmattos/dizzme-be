package com.dizzme.service;

import com.dizzme.dto.QRCodeResponse;
import com.dizzme.exception.BusinessException;
import com.dizzme.repository.QRCodeRepository;
import com.google.zxing.Writer;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class QRCodeService {

    @Autowired
    private QRCodeRepository qrCodeRepository;

    public QRCodeResponse generateQRCode(String url, Integer size) throws BusinessException {
        try {
            Writer writer = new com.google.zxing.qrcode.QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(url,
                    com.google.zxing.BarcodeFormat.QR_CODE, size, size);

            BufferedImage bufferedImage = new java.awt.image.BufferedImage(
                    size, size, java.awt.image.BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
                }
            }

            ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            return new QRCodeResponse("data:image/png;base64," + base64Image, url, size);

        } catch (Exception e) {
            throw new BusinessException("Erro ao gerar QR Code: " + e.getMessage());
        }
    }

    public QRCodeResponse generateSurveyQRCode(String surveyPublicId, Integer size) throws BusinessException {
        String url = "http://localhost:4200/survey/" + surveyPublicId;
        return generateQRCode(url, size != null ? size : 300);
    }
}
