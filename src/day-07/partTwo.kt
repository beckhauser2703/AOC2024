package `day-07-part-02`

import java.io.File

fun main() {
    val input = File("src/day-07/input.txt")
        .readText()
        .split("\\r?\\n".toRegex())
        .map {
            it
                .split(": ", " ")
                .map { item ->
                    item.toLong()
                }
        }

    val result = input
        .filter {
            backtrackOperations(it.subList(1, it.size), it.first())
        }.sumOf {
            it.first()
        }

    println(result)
}

fun backtrackOperations(operators: List<Long>, target: Long, curValue: Long? = null): Boolean {
    if (operators.size == 1 && curValue != null) {
        return curValue + operators.first() == target
                || curValue * operators.first() == target
                || ("${(curValue ?: "")}${operators.first()}").toLong() == target
    }

    val operatorSublist = operators.subList(1, operators.size)
    val curElement = operators.first()

    ((curValue ?: 0) + curElement).also {
        if (it > target) {
            return false
        }
        if (backtrackOperations(operatorSublist, target, it)) {
            return true
        }
    }

    ((curValue ?: 1) * curElement).also {
        if (it > target) {
            return false
        }
        if (backtrackOperations(operatorSublist, target, it)) {
            return true
        }
    }

    ("${(curValue ?: "")}$curElement").toLong().also {
        if (it > target) {
            return false
        }
        if (backtrackOperations(operatorSublist, target, it)) {
            return true
        }
    }

    return false
}

