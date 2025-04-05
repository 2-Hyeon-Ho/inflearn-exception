package hello.exception.exhandler.advice;

import hello.exception.api.ApiExceptionControllerV3;
import hello.exception.exception.UserException;
import hello.exception.exhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(assignableTypes = {ApiExceptionControllerV3.class})
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandler (IllegalArgumentException e){

        log.error("[exceptionHandler ex]", e);
        return new ErrorResult("BAD", e.getMessage());
    }

    @ExceptionHandler(UserException.class)  //메서드 파라미터에 해당 클래스를 받고 있다면 UserException.class 생략 가능
    public ResponseEntity<ErrorResult> userExHandler(UserException e) {

        log.error("[exceptionHandler ex]", e);
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandler(Exception e) {

        log.error("[exceptionHandler ex]", e);
        return new ErrorResult("EX", "내부오류");
    }

}
