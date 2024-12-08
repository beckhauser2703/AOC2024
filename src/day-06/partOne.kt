package `day-06`

import java.io.File
import java.util.Vector

enum class Direction(val delta: NamedPair<Int>) {
    UP(delta = NamedPair(-1, 0)),
    DOWN(delta = NamedPair(1, 0)),
    LEFT(delta = NamedPair(0, -1)),
    RIGHT(delta = NamedPair(0, 1));

    companion object {
        fun rotate90(direction: Direction): Direction {
            return when (direction) {
                UP -> RIGHT
                DOWN -> LEFT
                LEFT -> UP
                RIGHT -> DOWN
            }
        }
    }
}

sealed class Cell {
    data class Guard(var direction: Direction, val curPos: NamedPair<Int>) : Cell()

    data class Obstacle(
        val fromDirectionToHasBeenHit: Map<Direction, Boolean> = defaultFromDirectionToHasBeenHit,
        val beenVisited: Boolean = false,
    ) : Cell()

    data class RegularCell(var beenVisited: Boolean = false) : Cell()

    companion object {
        var defaultFromDirectionToHasBeenHit: Map<Direction, Boolean> = Direction.entries.associateWith { false }
    }
}

typealias Grid<T> = List<List<T>>

operator fun <T> Grid<T>.get(namedPair: NamedPair<Int>): T {
    return this[namedPair.row][namedPair.col]
}

data class NamedPair<T>(val row: T, val col: T)

operator fun NamedPair<Int>.plus(other: NamedPair<Int>): NamedPair<Int> {
    return NamedPair(this.row + other.row, this.col + other.col)
}

operator fun NamedPair<Int>.minus(other: NamedPair<Int>): NamedPair<Int> {
    return NamedPair(this.row - other.row, this.col - other.col)
}


fun guardReachableSquares(grid: Grid<Cell>, guard: Cell.Guard): Int {
    var visitedCells = 1

    val obstacleHasBeenHitBefore: (Cell) -> Boolean = { cell ->
        when (cell) {
            is Cell.Obstacle -> cell.beenVisited && cell.fromDirectionToHasBeenHit[guard.direction]!!
            else -> false
        }
    }

    var curCellPos: NamedPair<Int> = guard.curPos
    var curCell: Cell

    while (onGrid(grid, curCellPos + guard.direction.delta) && !obstacleHasBeenHitBefore(grid[curCellPos])) {
        curCellPos += guard.direction.delta
        curCell = grid[curCellPos]
        when (curCell) {
            is Cell.Obstacle -> {
                curCellPos -= guard.direction.delta
                guard.direction = Direction.rotate90(guard.direction)
            }
            is Cell.RegularCell -> {
                if (!curCell.beenVisited) {
                    visitedCells += 1
                    curCell.beenVisited = true
                }
            }
            else -> {}
        }
    }
    return visitedCells
}

fun <T> onGrid(grid: Grid<T>, position: NamedPair<Int>): Boolean {
    return position.row >= 0
            && position.row < grid[0].size
            && position.col >= 0
            && position.col < grid.size
}



fun main() {
//    val fromPosToObstacle: MutableMap<NamedPair<Int>, Obstacle> = mutableMapOf()
    lateinit var guard: Cell.Guard;

    val grid: Grid<Cell> = File("src/day-06/input.txt")
        .readText()
        .split("\\r?\\n".toRegex())
        .mapIndexed { r, row ->
            row.mapIndexed { c, symbol ->
                when (symbol) {
                    '#' -> Cell.Obstacle()
                    '^' -> {
                        guard = Cell.Guard(Direction.UP, NamedPair(r, c))
                        guard
                    }
                    else -> Cell.RegularCell()
                }
            }
        }


    println(guardReachableSquares(grid, guard))
}

