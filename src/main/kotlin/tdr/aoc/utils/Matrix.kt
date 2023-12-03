package tdr.aoc.utils

class Matrix(val matrix: Array<Array<Char>>) {

    companion object {
        fun fromInput(input: List<String>): Matrix = Array(input.size) {
            input[it].toCharArray().toTypedArray()
        }.let { Matrix(it) }
    }

    fun subMatrix(rowStart: Int, rowEnd: Int, columnStart: Int, columnEnd: Int): Matrix {
        val rowStartIdx = rowStart.coerceAtLeast(0)
        val rowEndIdx = rowEnd.coerceAtMost(matrix.size - 1)
        val subMatrix = Array(rowEndIdx - rowStartIdx + 1) { newRowIdx ->
            val rowIdx = newRowIdx + rowStartIdx
            val colStartIdx = columnStart.coerceAtLeast(0)
            val colEndIdx = columnEnd.coerceAtMost(matrix[rowIdx].size - 1)
            Array(colEndIdx - colStartIdx + 1) {
                val colIdx = colStartIdx + it
                matrix[rowIdx][colIdx]
            }
        }
        return Matrix(subMatrix)
    }

    override fun toString(): String = buildString {
        matrix.forEach {
            append(String(it.toCharArray()))
            append("\n")
        }
    }

}