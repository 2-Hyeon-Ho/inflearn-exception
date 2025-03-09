# 03.09 공부기록  
## 서블릿 예외 처리  
- Exception(예외)
- response.sendError(Http 상태 코드, 오류 메시지)

예외 발생 흐름  
`
WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
`  

sendError 흐름  
`
WAS(sendError 호출 기록 확인) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러
(response.sendError())
`

예외 발생과 오류 페이지 요청 흐름  
```
1. WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
2. WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/error-page/500) -> View
```  
&rarr; 예외가 발생되어 WAS까지 전파된 후 WAS는 오류페이지 경로를 찾아 오류페이지를 호출하며 
이때, 서블릿, 인터셉터, 컨트롤러가 모두 다시 호출된다.  
서버 내부에서 오류페이지를 호출하며 필터와 인터셉터를 한번 더 호출하는건 비효율적이기에 해결하기 위해 *DispatcherType*이라는 추가정보를 활용  

### DispatcherType  
`REQUEST` : 클라이언트 요청  
`ERROR` : 오류 요청  
`FORWARD` : MVC에서 배웠던 서블릿에서 다른 서블릿이나 JSP를 호출할 때 `RequestDispatcher.forward(request, response);`  
`INCLUDE` : 서블릿에서 다른 서블릿이나 JSP의 결과를 포함할 때 `RequestDispatcher.include(request, response);`  
`ASYNC` : 서블릿 비동기 호출  

### Filter에서 중복 호출   
`
filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
`  
두 가지를 모두 넣으면 클라이언트 요청과 에러페이지 요청에도 필터가 호출된다.  
기본 값은 DispatcherType.REQUEST로 에러페이지 요청에 필터를 호출할게 아니라면 기본값으로 사용  

### 인터셉터에서 중복 호출  
```
registry.addInterceptor(new LogInterceptor())
            .order(1)
            .addPathPatterns("/**")
            .excludePathPatterns("/css/**", "*.ico", "/error", "/error-page/**");   //오류 페이지 경로 제외
```  
**인터셉터는 서블릿에서 제공하는게 아닌 스프링에서 제공하는 기능**으로 DispatcherType과 무관하게 무조건 호출된다.  
그러므로 에러페이지 경로에는 excludePathPatterns에 추가하여 호출되지 않도록 설정  

### 전체 흐름 정리  
```
1. WAS(/error-ex, dispatchType=REQUEST) -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러
2. WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
3. WAS 오류 페이지 확인
4. WAS(/error-page/500, dispatchType=ERROR) -> 필터(x) -> 서블릿 -> 인터셉터(x) -> 컨트롤러(/error-page/500) -> View
```  

## 스프링부트 예외 처리  
스프링부트는 ErrorPage를 자동으로 등록하고 /error라는 경로로 기본 오류페이지를 설정한다.  
***참고***  
`ErrorMvcAutoConfiguration` 이라는 클래스가 오류 페이지를 자동으로 등록하는 역할을 한다.  

오류가 발생했을 때 오류페이지로 /error를 기본으로 요청한다.
스프링부트가 자동으로 등록한 `BasicErrorController`는 이 경로를 기본으로 받는다.  

그로인해 개발자는 오류 페이지 화면과 `BasicErrorController`가 제공하는 룰과 우선순위에 따라 등록만 하면 된다.  

### 뷰 선택 우선순위  
1. 뷰 템플릿  
   `resources/templates/error/500.html`
   `resources/templates/error/5xx.html`
2. 정적 리소스(`static` , `public` )  
   `resources/static/error/400.html`
   `resources/static/error/404.html`
   `resources/static/error/4xx.html`
3. 적용 대상이 없을 때 뷰 이름(`error` )  
   `resources/templates/error.html`