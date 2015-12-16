package caged.coaa.com.cagedspace.Utils;

/**
 * Created by SaideepReddy on 12/11/2015.
 */
public class Performer {
    private String name;
    private String caption;
    private String image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Performer{" +
                "name='" + name + '\'' +
                ", caption='" + caption + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
