package org.LetterRecognition.model;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

public class SelectedImage {

    /** Method converts Mat to Image type to add to ImageView on form
     * @param frame to be converted to image type
     * @return Image type object
     */
    public static Image mat2Image(Mat frame)
    {
        return SwingFXUtils.toFXImage(mat2BufferedImage(frame), null);
    }

    /** Method converts Mat to BufferedImage type
     * @param mat to be converted to BufferedImage type
     * @return BufferedImage type object
     */
    public static BufferedImage mat2BufferedImage(Mat mat) {
        BufferedImage bufferedImage = new BufferedImage(
                mat.width(),
                mat.height(),
                mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR
        );
        WritableRaster writableRaster = bufferedImage.getRaster();
        DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
        mat.get(0,0,dataBufferByte.getData());

        return bufferedImage;
    }

    /** Method is used to process image to be processed for object detection
     * @param frame to be processed
     * @return processed frame
     */
    public static Mat processImage(Mat frame){
        Mat processed = new Mat(frame.height(), frame.width(), frame.type());
        Imgproc.GaussianBlur(frame, processed, new Size(7,7),1);
        Imgproc.cvtColor(processed, processed, Imgproc.COLOR_RGB2GRAY);
        Imgproc.Canny(processed, processed, 200, 25);
        Imgproc.dilate(processed, processed, new Mat(), new Point(-1, -1), 1);
        return processed;
    }

    /** Method is used to markOutline of objects listed in image
     * @param processedFrame processed frame for object detection
     * @param originalFrame original frame to draw on to display to end user
     * @param model used to write predictions on frame
     */
    public static void markOutline(final Mat processedFrame, final Mat originalFrame, MultiLayerNetwork model) {
        final List<MatOfPoint> allOutlines = new ArrayList<>();
        Imgproc.findContours(processedFrame, allOutlines, new Mat(processedFrame.size(), processedFrame.type()), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int i = 0; i < allOutlines.size(); i++){
            final double value = Imgproc.contourArea(allOutlines.get(i));
            final Rect rect = Imgproc.boundingRect(allOutlines.get(i));
            final boolean isNotNoise = value > 1000;
            if (isNotNoise) {
                BufferedImage bufferedImage = mat2BufferedImage(
                        new Mat(originalFrame, new Rect(rect.x, rect.y, rect.width, rect.height)));
                Imgproc.rectangle(
                        originalFrame,
                        new Point(rect.x, rect.y),
                        new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 0, 255),
                        5);
            }
        }
    }

    /** Method is used to crop frame to specified size focusing on detected object
     * @param capturedFrame original frame to crop image from
     * @return cropped BufferedImage
     */
    public static BufferedImage cropObject(final Mat capturedFrame) {
        final List<MatOfPoint> allOutlines = new ArrayList<>();
        Mat processedFrame = processImage(capturedFrame);
        Imgproc.findContours(processedFrame, allOutlines, new Mat(processedFrame.size(), processedFrame.type()), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int i = 0; i < allOutlines.size(); i++){
            final double value = Imgproc.contourArea(allOutlines.get(i));
            final Rect rect = Imgproc.boundingRect(allOutlines.get(i));
            final boolean isNotNoise = value > 1000;
            if (isNotNoise) {
                BufferedImage bufferedImage = mat2BufferedImage(
                        new Mat(capturedFrame, new Rect(rect.x, rect.y, rect.width, rect.height)));
                return bufferedImage;
            }
        }
        return null;
    }

    /** Method is used to process buffered image to match dimensions found in dataset for better prediction
     * @param bufferedImage image to be processed
     * @param threshold used to pull out noise from bufferedImage
     * @return processed bufferedImage
     */
    public static BufferedImage blackWhite(BufferedImage bufferedImage, int threshold) {
        double boarderScale = 1.0;
        int boarderWidth;
        int adjustedHeight;
        int adjustedWidth;
        if (bufferedImage.getHeight() > bufferedImage.getWidth()) {
            boarderWidth = (int) (bufferedImage.getHeight() * boarderScale);
            adjustedHeight = bufferedImage.getHeight() + boarderWidth;
            adjustedWidth = bufferedImage.getHeight() + boarderWidth;
        }
        else {
            boarderWidth = (int) (bufferedImage.getWidth() * boarderScale);
            adjustedHeight = bufferedImage.getWidth() + boarderWidth;
            adjustedWidth = bufferedImage.getWidth() + boarderWidth;
        }


        BufferedImage output = new BufferedImage(adjustedWidth, adjustedHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = output.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0,0,adjustedWidth,adjustedHeight);
        graphics.drawImage(bufferedImage,
                (adjustedWidth/2)-(bufferedImage.getWidth()/2),
                (adjustedHeight/2)-(bufferedImage.getHeight()/2),
                null);
        graphics.dispose();


        WritableRaster raster = output.getRaster();
        int[] pixels = new int[output.getWidth()];
        for (int y = 0; y < output.getHeight(); y++) {
            raster.getPixels(0, y, output.getWidth(), 1, pixels);
            for (int i = 0; i < pixels.length; i++) {
                if (pixels[i] < threshold) pixels[i] = 0;
                else pixels[i] = 255;
            }
            raster.setPixels(0, y, output.getWidth(), 1, pixels);
        }


        BufferedImage resizedImage = new BufferedImage(128, 128, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(output, 0, 0, 128, 128, null);
        graphics2D.dispose();


        return resizedImage;

    }

    /** Method is used on thread to update ImageView object with selected image
     * @param property ImageView object
     * @param value Image to be placed on object
     */
    public static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
        Platform.runLater(() -> {
            property.set(value);
        });
    }

}
