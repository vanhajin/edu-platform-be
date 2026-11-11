package com.eduplatform.common.utils;

public final class ExceptionMessageUtil {

    private ExceptionMessageUtil() {
    }

    public static String getDetail(Throwable exception) {
        return getDetail("unknown-service", exception);
    }

    public static String getDetail(String serviceName, Throwable exception) {
        Throwable rootCause = getRootCause(exception);
        StackTraceElement[] stackTrace = rootCause.getStackTrace();
        StackTraceElement location = stackTrace.length == 0 ? null : stackTrace[0];

        String className = location == null ? rootCause.getClass().getName() : location.getClassName();
        String method = location == null ? "unknown" : location.getMethodName();
        int line = location == null ? -1 : location.getLineNumber();
        String message = rootCause.getMessage() == null ? rootCause.getClass().getSimpleName() : rootCause.getMessage();

        String normalizedServiceName = serviceName == null || serviceName.isBlank() ? "unknown-service" : serviceName;
        return normalizedServiceName + ":" + className + ":" + method + ":" + line + ":" + message;
    }

    private static Throwable getRootCause(Throwable exception) {
        Throwable result = exception;
        while (result.getCause() != null && result.getCause() != result) {
            result = result.getCause();
        }
        return result;
    }
}
