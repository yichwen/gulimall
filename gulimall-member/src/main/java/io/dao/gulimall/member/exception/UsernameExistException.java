package io.dao.gulimall.member.exception;

public class UsernameExistException extends RuntimeException {
    public UsernameExistException() {
        super("手机号码存在");
    }
}
