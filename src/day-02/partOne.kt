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

    val deltaLevelsByReport = preprocessedInput
        .map { getDeltaValues(it) }

    val safeRange: (Int) -> Boolean = { x -> abs(x) in 1..3 }

    val alwaysIncreasingOrDecreasing: (List<Int>) -> Boolean = { lst -> (lst.all { it > 0} || lst.all { it < 0})}

    println(
        deltaLevelsByReport
            .count {
                report -> report.all { safeRange(it) } && alwaysIncreasingOrDecreasing(report)
            }
    )
}

fun getDeltaValues(report: List<Int>): List<Int> {
    return report.windowed(2) { level ->
        level.last() - level.first()
    }
}
