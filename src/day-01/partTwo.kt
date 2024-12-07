package `day-01`

import java.io.File
import kotlin.math.abs

fun main() {
    val input = File("src/day-01/input.txt").readText().split(" {3}|\\r?\\n".toRegex())

    val leftList = input
        .map { it.toInt() }
        .filterIndexed { idx, _ -> idx % 2 == 0 }

    val rightIdsFrequencyMap = input
        .map { it.toInt() }
        .filterIndexed { idx, _ -> idx % 2 == 1 }
        .groupingBy { it }
        .eachCount()

    println(
        leftList
            .sumOf { leftValue -> leftValue * rightIdsFrequencyMap.getOrDefault(leftValue, 0) }
    )
}