package gallery.back.art.backend.common.code;

public enum ErrorCode {
    SUCCESS("200", "정상통신"),
    SYSTEM_ERROR("500", "알 수 없는 시스템 에러"),
    SYSTEM_FORBIDDEN_ERROR("401", "권한 없음"),
    PAGE_NOT_FOUND("404", "컨트롤러 없음"),
    SYSTEM_PERMISSION_DENY_ERROR("403", "권한 부족");

    private final String code;
    private final String reason;

    ErrorCode(String code, String reason){
        this.code = code;
        this.reason = reason;
    }

    public String getCode() {
        return code;
    }
    public String getReason() {
        return reason;
    }
}
