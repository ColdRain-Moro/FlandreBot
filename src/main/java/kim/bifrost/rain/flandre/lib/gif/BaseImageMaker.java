package kim.bifrost.rain.flandre.lib.gif;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class BaseImageMaker {
    public InputStream makeImage(String path,
                                 ArrayList<AvatarModel> avatarList, ArrayList<TextModel> textList,
                                 boolean antialias) {
        try {
            BufferedImage sticker = ImageIO.read(new File(path + "0.png"));
            return bufferedImageToInputStream(ImageSynthesis.synthesisImage(
                    sticker, avatarList, textList, antialias));
        } catch (IOException e) {
            System.out.println("构造IMG失败，请检查 PetData");
            e.printStackTrace();
        }
        return null;
    }

    private InputStream bufferedImageToInputStream(BufferedImage bf) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bf, "png", os);
        return new ByteArrayInputStream(os.toByteArray());
    }
}
