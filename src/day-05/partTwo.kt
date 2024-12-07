package `day-05-part-2`

import java.io.File
import java.util.Vector

data class Page(var number: String, var rules: Map<String, Set<String>>) : Comparable<Page> {
    override fun compareTo(other: Page): Int {
        val pagesHigherThanThis = rules[this.number] ?: return 1
        val pagesHigherThanOther = rules[other.number] ?: return -1

        if (this.number in pagesHigherThanOther) {
            return 1
        }

        if (other.number in pagesHigherThanThis) {
            return -1
        }

        return 0
    }

    override fun toString(): String {
        return number
    }
}

fun main() {
    var (rawRules, rawUpdates) = File("src/day-05/input.txt")
        .readText()
        .split("\\r?\\n\\r?\\n".toRegex())

    var rules = rawRules
        .split("\\r?\\n".toRegex())
        .map {
            val (leftPage, rightPage) = it.split("|")
            Pair(leftPage, rightPage)
        }
        .groupBy({ it.first }, { it.second })
        .mapValues { (_, v) -> v.toSet() }

    var updates = rawUpdates
        .split("\\r?\\n".toRegex())
        .map {
            it.split(",").map { pageNumber ->
                Page(pageNumber, rules)
            }
        }

    println(updates
        .filter {
            it != it.sorted()
        }
        .map {
            it.sorted()
        }
        .flatMap { update ->
            update.filterIndexed { idx, _ ->
                idx == update.size / 2
            }
        }
        .sumOf { it.number.toInt() }
    )
}

