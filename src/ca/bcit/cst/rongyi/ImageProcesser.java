package ca.bcit.cst.rongyi;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * ImageProcesser
 *
 * @author Rongyi Chen
 * @version 2017
 */
public class ImageProcesser {

    public static Image boxBlur(Image img) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();
        PixelReader reader = img.getPixelReader();
        WritableImage after = new WritableImage(reader, w, h);
        PixelWriter writer = after.getPixelWriter();
        
        for (int r = 1; r < w - 1; r++) {
            for (int c = 1; c < h - 1; c++) {
                Color color = averageLAB(
                        LAB.fromFXColor(reader.getColor(r - 1, c - 1)), 
                        LAB.fromFXColor(reader.getColor(r - 1, c)), 
                        LAB.fromFXColor(reader.getColor(r - 1, c + 1)), 
                        LAB.fromFXColor(reader.getColor(r, c - 1)), 
                        LAB.fromFXColor(reader.getColor(r, c)), 
                        LAB.fromFXColor(reader.getColor(r, c + 1)), 
                        LAB.fromFXColor(reader.getColor(r + 1, c - 1)), 
                        LAB.fromFXColor(reader.getColor(r + 1, c )), 
                        LAB.fromFXColor(reader.getColor(r + 1, c + 1))
                        ).FXColor();
                writer.setColor(r, c, color);
            }
        }
        
        return after;
    }
    
    public static Image mosaic(Image img, int pixels) {
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();
        PixelReader reader = img.getPixelReader();
        WritableImage after = new WritableImage(reader, w, h);
        PixelWriter writer = after.getPixelWriter();
        
        int rpc = w / pixels;
        int cpc = h / pixels;
        
        for (int x = 0; x < rpc; x++) {
            for (int y = 0; y < cpc; y++) {
                // normal 
                LAB[] arr = new LAB[pixels * pixels];
                int index = 0;
                for (int r = x * pixels; r < (x + 1) * pixels; r++) {
                    for (int c = y * pixels; c < (y + 1) * pixels; c++) {
                        arr[index++] = LAB.fromFXColor(reader.getColor(r, c));
                    }
                }
                Color color = averageLAB(arr).FXColor();
                for (int r = x * pixels; r < (x + 1) * pixels; r++) {
                    for (int c = y * pixels; c < (y + 1) * pixels; c++) {
                        writer.setColor(r, c, color);
                    }
                }
                // right
                arr = new LAB[pixels * pixels];
                index = 0;
                for (int r = w - pixels; r < w; r++) {
                    for (int c = y * pixels; c < (y + 1) * pixels; c++) {
                        arr[index++] = LAB.fromFXColor(reader.getColor(r, c));
                    }
                }
                color = averageLAB(arr).FXColor();
                for (int r = rpc * pixels; r < w; r++) {
                    for (int c = y * pixels; c < (y + 1) * pixels; c++) {
                        writer.setColor(r, c, color);
                    }
                }
            }
            // bottom
            LAB[] arr = new LAB[pixels * pixels];
            int index = 0;
            for (int r = x * pixels; r < (x + 1) * pixels; r++) {
                for (int c = h - pixels; c < h; c++) {
                    arr[index++] = LAB.fromFXColor(reader.getColor(r, c));
                }
            }
            Color color = averageLAB(arr).FXColor();
            for (int r = x * pixels; r < (x + 1) * pixels; r++) {
                for (int c = cpc * pixels; c < h; c++) {
                    writer.setColor(r, c, color);
                }
            }
        }
        // bottom-right corner
        LAB[] arr = new LAB[pixels * pixels];
        int index = 0;
        for (int r = w - pixels; r < w; r++) {
            for (int c = h - pixels; c < h; c++) {
                arr[index++] = LAB.fromFXColor(reader.getColor(r, c));
            }
        }
        Color color = averageLAB(arr).FXColor();
        for (int r = rpc * pixels; r < w; r++) {
            for (int c = cpc * pixels; c < h; c++) {
                writer.setColor(r, c, color);
            }
        }
        
        return after;
    }
    
    public static LAB averageLAB(LAB ... labs) {
        int avgL = 0;
        int avgA = 0;
        int avgB = 0;
        
        for (LAB l : labs) {
            avgL += l.L;
            avgA += l.a;
            avgB += l.b;
        }
        
        avgL /= labs.length;
        avgA /= labs.length;
        avgB /= labs.length;
        
        return new LAB(avgL, avgA, avgB);
    }
    
}
