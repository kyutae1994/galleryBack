package gallery.back.art.backend.common.error;

import gallery.back.art.backend.common.code.ErrorCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CustomException extends Exception {
    private ErrorCode errorCode;
    private String className;
    public CustomException(ErrorCode errorCode, String className) {
        this.errorCode = errorCode;
        this.className = className;
        log.error("Exception 발생 > Code : {}, Message: {}, className: {}", errorCode.getCode(), errorCode.getReason(), className);
    }
}
