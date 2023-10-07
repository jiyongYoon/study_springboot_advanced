package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 필드에 전략을 보관하는 방식 -> 선 조립, 후 실행 방식! <br>
 * 장점: 이후에는 context.execute()만 실행하면 된다. <br>
 * 단점: 조립 이후 전략 변경이 어렵다. 특히 Context를 싱글톤으로 관리하게 된다면, 다른 Strategy를 넘겨 받는 동안 동시성 이슈 등이 발생하게 된다. (wcm에서도 한번 고민했던 부분이다) <br>
 */
@Slf4j
public class ContextV1 {

    private Strategy strategy;

    public ContextV1(Strategy strategy) {
        this.strategy = strategy;
    }

    // 거대한 문맥은 변하지 않는다.
    public void execute() {
        long startTime = System.currentTimeMillis();

        strategy.call(); // 일부 전략이 변경된다.

        long entTime = System.currentTimeMillis();
        long resultTime = entTime - startTime;
        log.info("resultTime={}", resultTime);
    }
}
