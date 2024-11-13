package poomasi.global.common;

public enum ImageFormat {
    JPEG,
    PNG,
    GIF,
    BMP,
    TIFF,
    UNKNOWN;

    public static ImageFormat from(String format) {
        switch (format) {
            case "jpeg":
            case "jpg":
                return JPEG;
            case "png":
                return PNG;
            case "gif":
                return GIF;
            case "bmp":
                return BMP;
            case "tiff":
                return TIFF;
            default:
                return UNKNOWN;
        }
    }
}
