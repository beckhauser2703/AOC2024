package `day-01`

import java.io.File
import kotlin.math.abs

fun main() {
    val input = File("src/day-01/input.txt").readText().split(" {3}|\\r?\\n".toRegex())

    val leftList = input
        .map { it.toInt() }
        .filterIndexed() { idx, _ -> idx % 2 == 0 }
        .sorted()

    val rightList = input
        .map { it.toInt() }
        .filterIndexed() { idx, _ -> idx % 2 == 1 }
        .sorted()

    println(
        (leftList zip rightList)
            .sumOf { (leftValue, rightValue) -> abs(leftValue - rightValue) }
    )
}