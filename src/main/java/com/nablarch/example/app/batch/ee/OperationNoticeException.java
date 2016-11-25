package com.nablarch.example.app.batch.ee;

/**
 * 運用に通知するための例外クラス。
 */
public class OperationNoticeException extends RuntimeException {

    public OperationNoticeException(final String message) {
        super(message);
    }
}
