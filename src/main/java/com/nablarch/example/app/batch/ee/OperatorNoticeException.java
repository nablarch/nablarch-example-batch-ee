package com.nablarch.example.app.batch.ee;

/**
 * 運用に通知するための例外クラス。
 */
public class OperatorNoticeException extends RuntimeException {

    /**
     * メッセージを元に例外を構築する。
     *
     * @param message メッセージ
     */
    public OperatorNoticeException(final String message) {
        super(message);
    }

    /**
     * メッセージと原因例外を元に例外を構築する。
     *
     * @param message メッセージ
     * @param cause 原因例外
     */
    public OperatorNoticeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
