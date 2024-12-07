package `day-04-part-2`

import java.io.File
import java.util.Vector

typealias Grid = List<List<Char>>

operator fun Grid.get(namedPair: NamedPair<Int>): Char {
    return this[namedPair.row][namedPair.col]
}

data class NamedPair<T>(val row: T, val col: T)

operator fun NamedPair<Int>.plus(other: NamedPair<Int>): NamedPair<Int> {
    return NamedPair(this.row + other.row, this.col + other.col)
}

operator fun NamedPair<Int>.times(scalar: Int): NamedPair<Int> {
    return NamedPair(this.row * scalar, this.col * scalar)
}

fun lookAroundALetter(grid: Grid, letterAPosition: NamedPair<Int>): Int {

    var downLeftDirection = NamedPair(-1, -1)
    var upLeftDirection = NamedPair(-1, +1)
    var downRightDirection = NamedPair(+1, -1)
    var upRightDirection = NamedPair(+1, +1)

    var directions = listOf(
        downRightDirection,
        downLeftDirection,
        upRightDirection,
        upLeftDirection
    )

    if (directions.map{ it + letterAPosition }.any { !onGrid(grid, it) }) {
        return 0
    }

    if (setOf(grid[letterAPosition + upLeftDirection], grid[letterAPosition + downRightDirection]) != setOf('S', 'M')) {
        return 0
    }

    if (setOf(grid[letterAPosition + upRightDirection], grid[letterAPosition + downLeftDirection]) != setOf('S', 'M')) {
        return 0
    }

    return 1
}

fun onGrid(grid: Grid, position: NamedPair<Int>): Boolean {
    return position.row >= 0
            && position.row < grid[0].size
            && position.col >= 0
            && position.col < grid.size
}

fun main() {
    val grid = File("src/day-04/input.txt")
        .readText()
        .split("\\r?\\n".toRegex())
        .map { it.toList() }

    var result = 0;


    grid
        .forEachIndexed { r, line ->
            line.forEachIndexed() { c, char ->
                if (char == 'A') {
                    result += lookAroundALetter(grid, NamedPair(r, c))
                }
            }
        }

    //same thing as result but it looks cute
    val foldResult = grid.foldIndexed(0) { r, acc, lst ->
        acc + lst.foldIndexed(0) { c, innerAcc, char ->
            if (char == 'A') {
                innerAcc + lookAroundALetter(grid, NamedPair(r, c))
            } else {
                innerAcc
            }
        }
    }

    println(foldResult)
}

