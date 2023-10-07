package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 전략을 파라미터로 전달 받는 방식! (스프링 부트 대부분이 이런 전략패턴을 사용하고 있다)
 * 장점: 파라미터로 전달받기 때문에 런타임 시점마다 교체가 가능하다.
 * 단점: 실행할 때마다 전략을 계속 지정해주어야 한다. (파라미터로 전달해주어야 한다는 뜻)
 */
@Slf4j
public class ContextV2 {

    // 거대한 문맥은 변하지 않는다.
    public void execute(Strategy strategy) {
        long startTime = System.currentTimeMillis();

        strategy.call(); // 일부 전략이 변경된다.

        long entTime = System.currentTimeMillis();
        long resultTime = entTime - startTime;
        log.info("resultTime={}", resultTime);
    }
}
