/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image_conv;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author dam
 */
public class Image_Conv {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here

        BufferedImage img = ImageIO.read(new File("src\\images\\car.jpg"));
        BufferedImage img_blurr = ImageIO.read(new File("src\\images\\car.jpg"));
        BufferedImage img_focus = ImageIO.read(new File("src\\images\\car.jpg"));
        BufferedImage img_edge = ImageIO.read(new File("src\\images\\car.jpg"));

        JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(img.getWidth() * 4, img.getHeight());
        frame.setVisible(true);

        int blurr[][] = {
            {1, 1, 1},
            {1, 1, 1},
            {1, 1, 1}
        };

        int focus[][] = {
            {0, -1, 0},
            {-1, -5, -1},
            {0, -1, 0}
        };

        int negative[][] = {
            {-1, -1, -1},
            {0, 0, 0},
            {1, 1, 1}
        };

        JPanel pane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                draw(img, img, 0, null, g);
                draw(img_blurr, img, 1, negative, g);
                draw(img_focus, img, 2, blurr, g);
                draw(img_edge, img, 3, focus, g);

            }
        };

        frame.add(pane);
    }

    private static void draw(BufferedImage new_img, BufferedImage img, int position, int[][] effect, Graphics g) {

        if (effect != null) {

            byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
            byte[] new_pixels = ((DataBufferByte) new_img.getRaster().getDataBuffer()).getData();

            int weight = weight(effect);

            for (int width = 1; width < img.getWidth() - 1; width++) {
                for (int height = 1; height < img.getHeight() - 1; height++) {

                    int b_total = 0;
                    int g_total = 0;
                    int r_total = 0;

                    for (int i = 0; i < effect.length; i++) {
                        for (int j = 0; j < effect.length; j++) {

                            int new_height = height + j - effect.length / 2;
                            int new_width = width + i - effect.length / 2;

                            int p_position = (new_width + new_height * img.getWidth());

                            int B = pixels[3 * p_position] & 0xff;
                            int G = pixels[1 + 3 * p_position] & 0xff;
                            int R = pixels[2 + 3 * p_position] & 0xff;

                            b_total += B * effect[i][j];
                            g_total += G * effect[i][j];
                            r_total += R * effect[i][j];

                        }
                    }

                    if (b_total != 0 && weight != 0) {
                        new_pixels[3 * (width + height * new_img.getWidth())]
                                = (byte) (b_total / weight);
                    } else {
                        new_pixels[3 * (width + height * new_img.getWidth())]
                                = (byte) b_total;
                    }

                    if (g_total != 0 && weight != 0) {
                        new_pixels[1 + 3 * (width + height * new_img.getWidth())]
                                = (byte) (g_total / weight);
                    } else {
                        new_pixels[1 + 3 * (width + height * new_img.getWidth())]
                                = (byte) g_total;
                    }

                    if (r_total != 0 && weight != 0) {
                        new_pixels[2 + 3 * (width + height * new_img.getWidth())]
                                = (byte) (r_total / weight);
                    } else {
                        new_pixels[2 + 3 * (width + height * new_img.getWidth())]
                                = (byte) r_total;
                    }

                }
            }

        }

        g.drawImage(new_img, new_img.getWidth() * position, 0, null);
    }

    public static int weight(int effect[][]) {

        int weight = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                weight += effect[i][j];
            }
        }

        return weight;
    }
}
