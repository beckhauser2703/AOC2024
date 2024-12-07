package `day-04`

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

fun lookAroundXLetter(grid: Grid, letterXPosition: NamedPair<Int>): Int {
    var directions = ((-1..1)
        .flatMap { r ->
            (-1..1)
                .map { c -> NamedPair(r, c) }
        })

    return directions
        .count {
            lookDirectionFromXLetter(grid, letterXPosition, it)
        }
}

fun lookDirectionFromXLetter(grid: Grid, letterXPosition: NamedPair<Int>, direction: NamedPair<Int>): Boolean {
    if (!onGrid(grid, letterXPosition + (direction * 3))) {
        return false
    }

    val isM = grid[letterXPosition + direction] == 'M'
    val isA = grid[letterXPosition + direction * 2] == 'A'
    val isS = grid[letterXPosition + direction * 3] == 'S'
    return isM && isA && isS
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
                if (char == 'X') {
                    result += lookAroundXLetter(grid, NamedPair(r, c))
                }
            }
        }

    //same thing as result but it looks cute
    val foldResult = grid.foldIndexed(0) { r, acc, lst ->
        acc + lst.foldIndexed(0) {
                c, innerAcc, char ->
            if (char == 'X') {
                innerAcc + lookAroundXLetter(grid, NamedPair(r, c))
            } else {
                innerAcc
            }
        }
    }

    println(foldResult)
}

