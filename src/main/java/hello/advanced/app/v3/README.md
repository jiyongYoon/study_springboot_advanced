### 기존의 구현과 문제점
v0 ~ v3 까지의 개발내용은, 요구사항인 `로그추적` 목적은 이루었으나, 여러가지 문제점이 있다.
1. 핵심기능과 부가기능이 섞여있다. 그리고 부가기능의 코드가 너무 많다.
   - ex) OrderServiceV3.java
      ```java
        public void orderItem(String itemId) {
            TraceStatus status = null;
            try {
                status = trace.begin("OrderService.orderItem()");
                orderRepository.save(itemId); <- 핵심기능은 한줄
                trace.end(status);
            } catch (Exception e) {
                trace.exception(status, e);
                throw e;
            }
        }
2. 클래스가 추가될 때마다 작업할 코드의 양이 많다.

> 동일한 패턴이 적용된 이 코드를 모듈화하여 디자인 패턴을 적용해볼 수 있다. <br>

