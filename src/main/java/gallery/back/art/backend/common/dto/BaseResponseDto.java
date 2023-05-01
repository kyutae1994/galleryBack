package gallery.back.art.backend.common.dto;

import gallery.back.art.backend.common.code.ErrorCode;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BaseResponseDto<T> {
    private final T responseData;
    private final String errorCode;
    private final String errorReason;

    @Builder
    public BaseResponseDto(T responseData, String errorCode, String errorReason) {
        this.responseData = responseData;
        this.errorCode = errorCode;
        this.errorReason = errorReason;
    }

    public static BaseResponseDto of(Object responseData){
        return BaseResponseDto
                .builder()
                .responseData(responseData)
                .errorCode(ErrorCode.SUCCESS.getCode())
                .errorReason(ErrorCode.SUCCESS.getReason())
                .build();
    }

    public static BaseResponseDto of(Object responseData, ErrorCode code){
        return BaseResponseDto
                .builder()
                .responseData(responseData)
                .errorCode(code.getCode())
                .errorReason(code.getReason())
                .build();
    }
}
