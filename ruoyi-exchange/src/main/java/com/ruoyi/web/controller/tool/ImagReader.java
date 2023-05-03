package com.ruoyi.web.controller.tool;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param
 * @Author: sddd
 * @Date: 2022/4/5,17:25
 * @return:
 **/
@Component
public class ImagReader {

    private final ConcurrentHashMap<Integer,BufferedImage> ori_targets;

    public ImagReader()throws IOException{
        ori_targets=new ConcurrentHashMap<>();
        init();
    }

    private void init() throws IOException{
        String s="/img/";
        for (int i = 1; i <= 20; i++) {
            String a=s+"a"+i+".png";
            InputStream resourceAsStream = ImagReader.class.getResourceAsStream(a);
            BufferedImage read = ImageIO.read(resourceAsStream);
            ori_targets.put(i,read);
        }

    }




    private BufferedImage createNew(BufferedImage buf, int width, int height, boolean redraw){
        if (redraw &&buf.getType()==BufferedImage.TYPE_3BYTE_BGR) {
            BufferedImage bufferBackGroundImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = buf.getRGB(x, y);
                    bufferBackGroundImage.setRGB(x,y,rgb );
                }
            }
            return bufferBackGroundImage;
        } else {
            BufferedImage bufferedImage = new BufferedImage(width,height,buf.getType());
            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.drawRenderedImage(buf,null);
            graphics.dispose();
            return bufferedImage;
        }
    }

    public BufferedImage getTargetPicture()  {
        BufferedImage buf = getPic();
        return createNew(buf,buf.getWidth(),buf.getHeight(),true);
    }

    private BufferedImage getPic()  {
        Random random = new Random();
        int i = random.nextInt(20) + 1;
        return ori_targets.get(i);
    }

    public BufferedImage getRotateImage(double ang)  {
        BufferedImage buf = getPic();
        int height = buf.getHeight();
        int width = buf.getWidth();
        int type = buf.getColorModel().getTransparency();
        BufferedImage img;
        Graphics2D graphics2d;
        (graphics2d = (img = new BufferedImage(width, height, type))
                .createGraphics()).setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.rotate(ang, (double) (width/2), (double)height/2);
        graphics2d.drawRenderedImage(buf, null);
        graphics2d.dispose();
        return img;
    }

}
