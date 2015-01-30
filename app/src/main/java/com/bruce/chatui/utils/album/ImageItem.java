
package com.bruce.chatui.utils.album;

import java.io.Serializable;

/**
 * 
 * @Description 图片对象Model
 *
 */
@SuppressWarnings("serial")
public class ImageItem implements Serializable {
    private String imageId; //图片ID
    private String thumbnailPath; //图片略缩图地址
    private String imagePath; //图片地址
    private boolean isSelected = false; //是否选择

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
