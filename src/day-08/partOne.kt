package `day-08`

import java.io.File

data class Position(
    val r: Int,
    val c: Int,
)

operator fun Position.plus(other: Position): Position = Position(this.r + other.r, this.c + other.c)

operator fun Position.times(scalar: Int) = Position(scalar * this.r, scalar * this.c)

operator fun Position.minus(other: Position) = this + (other * -1)

typealias Grid = List<List<Char>>

fun Grid.withIndices(): Sequence<Pair<Position, Char>> =
    sequence {
        for (r in indices) {
            for (c in this@withIndices[r].indices) {
                yield(Pair(Position(r, c), this@withIndices[r][c]))
            }
        }
    }

fun Grid.inbounds(position: Position): Boolean {
    if ((position.r < 0) || (position.r > this.size - 1)) {
        return false
    }
    if ((position.c < 0) || (position.c > this.first().size - 1)) {
        return false
    }
    return true
}

fun main() {
    val grid: Grid =
        File("src/day-08/input.txt")
            .readText()
            .split("\\r?\\n".toRegex())
            .map { it.toList() }

    print(solution(grid))
}

fun solution(grid: Grid): Int {
    val nodePositions: MutableMap<Char, MutableList<Position>> = mutableMapOf()
    val antinodePositions: MutableSet<Position> = mutableSetOf()

    grid
        .withIndices()
        .filter { (_, value) -> value != '.' }
        .forEach { (position, value) ->
            nodePositions[value]?.forEach {
                val delta = it - position
                if (grid.inbounds(it + delta)) {
                    antinodePositions.add(it + delta)
                }
                if (grid.inbounds(position - delta)) {
                    antinodePositions.add(position - delta)
                }
            }
            nodePositions.getOrPut(value) { mutableListOf() }.add(position)
        }
    return antinodePositions.size
}
