package io.dao.gulimall.member.exception;

public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("用户名存在");
    }
}
