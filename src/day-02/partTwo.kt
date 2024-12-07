package `day-02`

import java.io.File
import kotlin.math.abs

fun main() {
    val preprocessedInput = File("src/day-02/input.txt")
        .readText()
        .split("\\r?\\n".toRegex())
        .map { report ->
            report.split(" ").map { level -> level.toInt() }
        }

    println(
        preprocessedInput.
        count { problemDampener(it) }
    )
}

fun problemDampener(report: List<Int>, levelsAlreadySkipped: Int = 0): Boolean {
    if (levelsAlreadySkipped >= 2) {
        return false
    }

    val curIncreasingStatus: (Int, Int) -> Boolean = { x, y -> y - x > 0 }

    val firstReportIncreasingStatus = curIncreasingStatus(report[0], report[1])

    val secondReportIncreasingStatus = curIncreasingStatus(report[1], report[2])

    //The first 3 need this treatment as it isn't strictly defined yet if the list can only work with increasing or decreasing
    //Example: 1 4 3 2 1 should be safe by removing the first one but at first I cant pinpoint the strict monotonicity
    if (firstReportIncreasingStatus != secondReportIncreasingStatus) {
        return retryReportWithoutProblematicIndex(report, 0, levelsAlreadySkipped)
                || retryReportWithoutProblematicIndex(report, 1, levelsAlreadySkipped)
                || retryReportWithoutProblematicIndex(report, 2, levelsAlreadySkipped)
    }

    val safeRange: (Int, Int) -> Boolean = { x, y -> abs(x - y) in 1..3 && x != y}

    for (i in 1..< report.size) {
        val previousLevel = report[i - 1]
        val curLevel = report[i]
        if (curIncreasingStatus(previousLevel, curLevel) != firstReportIncreasingStatus || !safeRange(previousLevel, curLevel)) {
            return retryReportWithoutProblematicIndex(report, i - 1, levelsAlreadySkipped)
                    || retryReportWithoutProblematicIndex(report, i, levelsAlreadySkipped)
        }

    }
    return true
}

fun retryReportWithoutProblematicIndex(report: List<Int>, problematicIndex: Int, levelsAlreadySkipped: Int): Boolean {
    return problemDampener(report.filterIndexed { idx, _ -> idx != problematicIndex},levelsAlreadySkipped + 1)
}
