package gallery.back.art.backend.common.util;

public class CommonUtil {

    /**
     * String 값에 대해서 null 이나 빈 값 체크
     * @param o
     * @return chk
     */
    public static boolean isStringEmpty(String o) {
        boolean chk = false;
        if (o == null || o.isBlank()) {
            chk = true;
        }

        return chk;
    }
}
