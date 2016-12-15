package com.nablarch.example.app.batch.ee;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import javax.batch.runtime.Metric;
import javax.batch.runtime.context.StepContext;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;

/**
 * 進捗状況をログに出力するクラス。
 */
public class ProgressPrinter {

    private static final Logger LOGGER = LoggerManager.get("metrics");

    /** 処理対象件数 */
    private final long inputCount;

    /** 処理開始時間 */
    private final long startTime;

    /** 最新の状態 */
    private Current current;

    public ProgressPrinter(final long count) {
        inputCount = count;
        startTime = System.nanoTime();
        current = new Current(0, startTime);
        LOGGER.logInfo("処理件数: " + inputCount);
    }

    public void output(StepContext stepContext) {
        final long executedCount = getMetric(stepContext, Metric.MetricType.READ_COUNT).getValue();
        final long endTime = System.nanoTime();

        final Tps currentTps = new Tps(current.time, endTime, executedCount - current.count);
        final Tps totalTps = new Tps(startTime, endTime, executedCount);

        current = new Current(executedCount, endTime);

        final long remainCount = inputCount - executedCount;
        final long remainSeconds = (long) (remainCount / totalTps.getTps());
        final LocalDateTime estimatedEndTime = LocalDateTime.now()
                                                            .plus(remainSeconds, ChronoUnit.SECONDS);

        final NumberFormat numberFormat = new DecimalFormat("0.0");
        LOGGER.logInfo("ステップ: " + stepContext.getStepName()
                       + ", 今回のTPS: " + numberFormat.format(currentTps.getTps())
                       + ", 起動後からのTPS: " + numberFormat.format(totalTps.getTps())
                       + ", 残りの処理件数: " + (inputCount - executedCount)
                       + ", 終了予測時間: " + estimatedEndTime.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS")));
    }

    private Metric getMetric(StepContext stepContext, Metric.MetricType metricType) {
        for (final Metric metric : stepContext.getMetrics()) {
            if (metric.getType() == metricType) {
                return metric;
            }
        }
        return null;
    }

    private static class Current {

        private final long count;

        private final long time;

        public Current(final long count, final long time) {
            this.count = count;
            this.time = time;
        }
    }

    private static class Tps {

        private final long startTime;

        private final long endTime;

        private final long count;

        public Tps(final long startTime, final long endTime, final long count) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.count = count;
        }

        public double getTps() {
            final long executionTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            return ((double) count / executionTime) * 1000;
        }

    }
}
