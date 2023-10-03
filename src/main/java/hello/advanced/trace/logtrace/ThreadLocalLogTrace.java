package hello.advanced.trace.logtrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;

/** 쓰레드 로컬을 사용할 때 주의할 점!!
 * 쓰레드 로컬 값을 사용한 후 .remove()를 사용하여 값을 제거하지 않는 경우 문제가 발생함. <br>
 * Springboot의 Tomcat같은 WAS는 사용이 끝난 쓰레드를 실제로 버리지 않고 쓰레드 풀에서 꺼내서 사용하고 돌려놓기 때문에 <br>
 * 새로운 요청이 왔을 때 기존에 있던 내용을 담고 있던 쓰레드가 쓰레드 풀에서 나오는 경우가 생김 <br>
 */
@Slf4j
public class ThreadLocalLogTrace implements LogTrace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

//    private TraceId traceIdHolder; // traceId 동기화, 동시성 이슈 발생 예정
    private final ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();
    @Override
    public TraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = this.traceIdHolder.get();
        long startTimeMs = System.currentTimeMillis();
        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);
        return new TraceStatus(traceId, startTimeMs, message);
    }

    private void syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceIdHolder.set(new TraceId());
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }

    @Override
    public void end(TraceStatus status) {
        complete(status, null);
    }

    @Override
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = status.getTraceId();
        if (e == null) {
            log.info("[{}] {}{} time={}ms", traceId.getId(),
                addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(),
                resultTimeMs);
        } else {
            log.info("[{}] {}{} time={}ms ex={}", traceId.getId(),
                addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(),
                resultTimeMs,
                e.toString());
        }

        releaseTraceId();
    }

    private void releaseTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId.isFirstLevel()) {
            traceIdHolder.remove(); // destroy
        } else {
            traceIdHolder.set(traceId.createPreviousId());
        }
    }

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append( (i == level - 1) ? "|" + prefix : "| ");
        }
        return sb.toString();
    }
}
