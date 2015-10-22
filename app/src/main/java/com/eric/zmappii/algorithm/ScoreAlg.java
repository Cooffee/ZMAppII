package com.eric.zmappii.algorithm;

/**
 * Created by eric on 15/7/26.
 */
public class ScoreAlg {

    // -标准参考数值
    private static final double STANDARD_HEARTBEAT = 65;
    private static final double STANDARD_BREATH = 18;

    // -权重
    private static final double WEIGHT_HEARTBEAT = 0.3;
    private static final double WEIGHT_BREATH = 0.3;
    private static final double WEIGHT_MOVE = 0.3;
    private static final double WEIGHT_DEEPSLEEP = 0.3;

    /**
     * 通过心率、呼吸、体动、深睡的数值，得出睡眠的得分
     * 100分为满分，分为四等份，每项得分25分
     * @param heartbeat 心率值
     * @param breath 呼吸值
     * @param move 体动值
     * @param deepsleep 深睡时长
     * @return
     */
    public static double getFinalScore(double heartbeat, double breath, double move, double deepsleep) {
        double scoreHb = 25 * (1 - Math.abs(heartbeat - STANDARD_HEARTBEAT) /
                STANDARD_HEARTBEAT * WEIGHT_HEARTBEAT);
        double scoreBr = 25 * (1 - Math.abs(breath - STANDARD_BREATH) /
                STANDARD_BREATH * WEIGHT_BREATH);
        double scoreMv = 25 * (1 - move * WEIGHT_MOVE);
        double scoreDs = 25 * deepsleep * WEIGHT_DEEPSLEEP;

        return scoreHb + scoreBr + scoreMv + scoreDs;
    }
}
