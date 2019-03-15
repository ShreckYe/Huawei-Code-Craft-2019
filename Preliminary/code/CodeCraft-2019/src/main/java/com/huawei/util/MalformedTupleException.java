package com.huawei.util;

import java.io.IOException;

public class MalformedTupleException extends IOException {
    public MalformedTupleException() {
    }

    public MalformedTupleException(String message) {
        super(message);
    }

    public MalformedTupleException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedTupleException(Throwable cause) {
        super(cause);
    }
}
