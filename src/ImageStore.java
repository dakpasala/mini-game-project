import java.util.*;

import processing.core.PApplet;
import processing.core.PImage;

public final class ImageStore
{
    private final Map<String, List<PImage>> images;
    private final List<PImage> defaultImages;

    private final int KEYED_RED_IDX = 2;
    private final int KEYED_GREEN_IDX = 3;
    private final int KEYED_BLUE_IDX = 4;
    private static final int COLOR_MASK = 0xffffff;
    private static final int KEYED_IMAGE_MIN = 5;

    public ImageStore(PImage defaultImage) {
        this.images = new HashMap<>();
        defaultImages = new LinkedList<>();
        defaultImages.add(defaultImage);
    }
    public List<PImage> getImageList(String key) {
        return this.images.getOrDefault(key, this.defaultImages);
    }
    public void processImageLine(
            //Map<String, List<PImage>> images,
            String line, PApplet screen)
    {
        String[] attrs = line.split("\\s");
        if (attrs.length >= 2) {
            String key = attrs[0];
            PImage img = screen.loadImage(attrs[1]);
            if (img != null && img.width != -1) {
                List<PImage> imgs = getImages(key);
                imgs.add(img);

                if (attrs.length >= KEYED_IMAGE_MIN) {
                    int r = Integer.parseInt(attrs[this.KEYED_RED_IDX]);
                    int g = Integer.parseInt(attrs[this.KEYED_GREEN_IDX]);
                    int b = Integer.parseInt(attrs[this.KEYED_BLUE_IDX]);
                    setAlpha(img, screen.color(r, g, b), 0);
                }
            }
        }
    }

    public List<PImage> getImages(
            //Map<String, List<PImage>> images,
            String key)
    {
        List<PImage> imgs = images.get(key);
        if (imgs == null) {
            imgs = new LinkedList<>();
            images.put(key, imgs);
        }
        return imgs;
    }
    public void loadImages(
            Scanner in, PApplet screen)
    {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                processImageLine(in.nextLine(), screen);
            }
            catch (NumberFormatException e) {
                System.out.println(
                        String.format("Image format error on line %d",
                                lineNumber));
            }
            lineNumber++;
        }
    }
    private static void setAlpha(PImage img, int maskColor, int alpha) {
        int alphaValue = alpha << 24;
        int nonAlpha = maskColor & COLOR_MASK;
        img.format = PApplet.ARGB;
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            if ((img.pixels[i] & COLOR_MASK) == nonAlpha) {
                img.pixels[i] = alphaValue | nonAlpha;
            }
        }
        img.updatePixels();
    }
}
