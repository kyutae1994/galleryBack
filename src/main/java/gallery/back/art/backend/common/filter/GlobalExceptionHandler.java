package gallery.back.art.backend.common.filter;

import gallery.back.art.backend.common.code.ErrorCode;
import gallery.back.art.backend.common.dto.BaseResponseDto;
import gallery.back.art.backend.common.error.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CustomException.class)
    public ResponseEntity handleException(CustomException e) {
        return ResponseEntity.ok(BaseResponseDto.of("커스텁 익셉션 발생", e.getErrorCode()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handleException(RuntimeException e) {
        log.error(e.getMessage());
        return ResponseEntity.ok(BaseResponseDto.of("알 수 없는 에러 발생", ErrorCode.SYSTEM_ERROR));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleBadRequestException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.ok(BaseResponseDto.of("알 수 없는 에러 발생", ErrorCode.PAGE_NOT_FOUND));
    }
}
