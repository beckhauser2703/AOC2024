package `day-06-part-2`

import java.io.File

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
    data class Guard(var direction: Direction, var curPos: NamedPair<Int>) : Cell()

    data class Obstacle(
        val fromDirectionToHasBeenHit: MutableMap<Direction, Boolean> = createDefaultMap(),
        val beenVisited: Boolean = false,
    ) : Cell()

    data class RegularCell(
        val fromDirectionToHasBeenHit: MutableMap<Direction, Boolean> = createDefaultMap(),
        var beenVisited: Boolean = false,
        var possibleObstacleForLoop: Boolean = false
    ) : Cell()

    companion object {
        fun createDefaultMap(): MutableMap<Direction, Boolean> =
            Direction.entries.associateWith { false }.toMutableMap()
    }
}

typealias Grid<T> = List<List<T>>

operator fun <T> Grid<T>.get(namedPair: NamedPair<Int>): T {
    return this[namedPair.row][namedPair.col]
}

fun Grid<Cell>.printGrid() {
    this
        .forEach {
            println(
                it.map {
                    cell ->
                    getCellChar(cell)
                }
            )

        }
}

private fun getCellChar(cell: Cell) = when (cell) {
    is Cell.Obstacle -> '#'
    is Cell.Guard -> '^'
    is Cell.RegularCell -> {
        val hasBeenTransversedSideways =
            cell.fromDirectionToHasBeenHit[Direction.LEFT]!! || cell.fromDirectionToHasBeenHit[Direction.RIGHT]!!
        val hasBeenTransversedUpOrDown =
            cell.fromDirectionToHasBeenHit[Direction.UP]!! || cell.fromDirectionToHasBeenHit[Direction.DOWN]!!

        when {
            cell.possibleObstacleForLoop -> 'O'
            hasBeenTransversedSideways && hasBeenTransversedUpOrDown -> '+'
            hasBeenTransversedSideways -> '─'
            hasBeenTransversedUpOrDown -> '|'
            else -> '.'
        }
    }
}

data class NamedPair<T>(val row: T, val col: T)

operator fun NamedPair<Int>.plus(other: NamedPair<Int>): NamedPair<Int> {
    return NamedPair(this.row + other.row, this.col + other.col)
}

operator fun NamedPair<Int>.minus(other: NamedPair<Int>): NamedPair<Int> {
    return NamedPair(this.row - other.row, this.col - other.col)
}


fun guardReachableSquares(grid: Grid<Cell>, guard: Cell.Guard): Int {

    var result = 0

    val obstacleHasBeenHitBefore: (Cell) -> Boolean = { cell ->
        when (cell) {
            is Cell.Obstacle -> cell.beenVisited && cell.fromDirectionToHasBeenHit[guard.direction]!!
            else -> false
        }
    }


    while (onGrid(grid, guard.curPos + guard.direction.delta) && !obstacleHasBeenHitBefore(grid[guard.curPos])) {

        if (canStartLoop(grid, guard)) {
            result += 1
        }

        guard.curPos += guard.direction.delta
        when (val curCell = grid[guard.curPos]) {
            is Cell.Obstacle -> {
                guard.curPos -= guard.direction.delta
                guard.direction = Direction.rotate90(guard.direction)
                curCell.fromDirectionToHasBeenHit[guard.direction] = true
            }

            is Cell.RegularCell -> {
                if (!curCell.beenVisited) {
                    curCell.beenVisited = true
                    curCell.fromDirectionToHasBeenHit[guard.direction] = true
                }
            }

            else -> {}
        }
        grid.printGrid()
    }

    return result
}

fun canStartLoop(grid: Grid<Cell>, guard: Cell.Guard): Boolean {
    val possibleLoopDirection = Direction.rotate90(guard.direction)
    val possibleLoopEntryCellIdx = guard.curPos + possibleLoopDirection.delta
    val possibleNewObstaclePuttingCellIdx = guard.curPos + guard.direction.delta

    if (!onGrid(grid, possibleLoopEntryCellIdx) || !onGrid(grid, possibleNewObstaclePuttingCellIdx)) {
        return false
    }

    val possibleLoopEntryCell = grid[possibleLoopEntryCellIdx]
    val possibleNewObstaclePuttingCell = grid[possibleNewObstaclePuttingCellIdx]

    if (possibleNewObstaclePuttingCell is Cell.Obstacle) {
        return false
    }

    return when (possibleLoopEntryCell) {
        is Cell.RegularCell -> possibleLoopEntryCell.fromDirectionToHasBeenHit[possibleLoopDirection]!!.also {
            if (it && possibleNewObstaclePuttingCell is Cell.RegularCell) possibleNewObstaclePuttingCell.possibleObstacleForLoop = true
        }
        else -> false
    }
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

    val grid: Grid<Cell> = File("src/day-06/example_input.txt")
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

