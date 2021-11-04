package com.stmtok.common.geom

import kotlin.math.abs


class Matrix(
    val row: Int,
    val column: Int,
    val elements: DoubleArray
) {

    constructor(row: Int, column: Int) : this(row, column, DoubleArray(row * column))
    constructor(row: Int, column: Int, e: IntArray) : this(row, column, e.map { it.toDouble() }.toDoubleArray())
    constructor(row: Int, column: Int, e: FloatArray) : this(row, column, e.map { it.toDouble() }.toDoubleArray())
    constructor(row: Int, column: Int, e: Array<Int>) : this(row, column, e.map { it.toDouble() }.toDoubleArray())
    constructor(row: Int, column: Int, e: Array<Float>) : this(row, column, e.map { it.toDouble() }.toDoubleArray())
    constructor(row: Int, column: Int, e: Array<Double>) : this(row, column, e.toDoubleArray())
    constructor(e: List<List<Double>>) : this(e.size, e[0].size, e.flatten().toDoubleArray())

    fun elements2D(): List<List<Double>> {
        return List(row) {
            val offset = it * column
            elements.copyOfRange(offset, offset + column).toList()
        }
    }

    private val mutableElements: Array<DoubleArray>
        get() = Array(row) {
            val offset = it * column
            elements.copyOfRange(offset, offset + column)
        }


    operator fun get(i: Int, j: Int): Double {
        require(i in 0 until row && j in 0 until column) { "index out of bounds i:$i<$row, j:$j<$column" }
        return elements[i * column + j]
    }

    operator fun plus(right: Matrix): Matrix {
        require(row == right.row && column == right.column) { "invalid size matrix." }
        return Matrix(row, column, elements.zip(right.elements) { a, b -> a + b }.toDoubleArray())
    }

    operator fun minus(right: Matrix): Matrix {
        require(row == right.row && column == right.column) { "invalid size matrix." }
        return Matrix(row, column, elements.zip(right.elements) { a, b -> a - b }.toDoubleArray())
    }

    operator fun times(k: Int): Matrix = times(k.toDouble())
    operator fun times(k: Long): Matrix = times(k.toDouble())
    operator fun times(k: Float): Matrix = times(k.toDouble())
    operator fun times(k: Double): Matrix = Matrix(row, column, elements.map { it * k }.toDoubleArray())
    operator fun times(right: Matrix): Matrix {
        require(column == right.row) { "invalid size matrix." }
        val result = DoubleArray(row * right.column)
        for (i in 0 until row) {
            val offset = i * column
            val resultOffset = i * right.column
            for (j in 0 until column) {
                if (elements[offset + j] != 0.0) {
                    val rOffset = j * right.column
                    for (k in 0 until right.column) {
                        if (right.elements[rOffset + k] != 0.0) {
                            result[resultOffset + k] += elements[offset + j] * right.elements[rOffset + k]
                        }
                    }
                }
            }
        }
        return Matrix(row, right.column, result)
    }

    operator fun div(k: Int): Matrix = div(k.toDouble())
    operator fun div(k: Long): Matrix = div(k.toDouble())
    operator fun div(k: Float): Matrix = div(k.toDouble())
    operator fun div(k: Double): Matrix = Matrix(row, column, elements.map { it / k }.toDoubleArray())

    val transpose: Matrix by lazy {
        val result = DoubleArray(elements.size)
        for (i in 0 until row) {
            val offset = i * column
            for (j in 0 until column) {
                if (elements[offset + j] != 0.0) {
                    result[j * row + i] = elements[offset + j]
                }
            }
        }
        Matrix(column, row, result)
    }

    fun solve(right: Matrix): Matrix? {
        require(row == right.row) { "invalid size matrix." }
        // lu分解
        val (processHistory, elem) = lu() ?: return null
        //-- 前進消去・後退代入 --
        val result = DoubleArray(right.row * right.column)
        // 前進消去
        for (i in 0 until row) {
            val resultOffset = i * right.column
            for (j in 0 until right.column) {
                var d = right[processHistory[i], j]
                for (k in 0 until i) {
                    d -= elem[i][k] * result[k * right.column + j]
                }
                result[resultOffset + j] = d
            }
        }

        // 後退代入
        for (i in row - 1 downTo 0) {
            val denominator = elem[i][i]
            val offset = i * right.column
            for (j in 0 until right.column) {
                val index = offset + j
                var dd = result[index]
                for (k in i + 1 until row) {
                    dd -= elem[i][k] * result[k * right.column + j]
                }
                result[index] = dd / denominator
                if (result[index].isNaN() || result[index].isInfinite()) return null
            }
        }
        return Matrix(right.row, right.column, result)
    }

    private fun lu(): Pair<IntArray, Array<DoubleArray>>? {
        require(row == column) { "it must be row.size == column.size. but row:$row, column:$column" }
        // 行交換情報
        val p = IntArray(row) { it }
        val e = mutableElements
        // ピボット操作によるLU分解
        for (i in 0 until row) {
            pivot(e, p, i)

            val denominator = e[i][i]
            for (j in i + 1 until row) {
                if (e[j][i] != 0.0) {
                    e[j][i] /= denominator
                    if (e[j][i].isNaN() || e[j][i].isInfinite()) return null
                    for (k in i + 1 until row) {
                        e[j][k] -= e[j][i] * e[i][k]
                    }
                }
            }
        }
        return p to e
    }

    private fun pivot(e: Array<DoubleArray>, p: IntArray, n: Int) {
        var maxVal = 0.0
        var swapRowNum: Int = n
        for (i in n until e.size) {
            val tmp: Double = abs(e[i][n])
            if (tmp > maxVal) {
                maxVal = tmp
                swapRowNum = i
            }
        }

        if (swapRowNum > n) {
            val tmpRow = e[n]
            e[n] = e[swapRowNum]
            e[swapRowNum] = tmpRow
            val tmpNum = p[n]
            p[n] = p[swapRowNum]
            p[swapRowNum] = tmpNum
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Matrix) return false
        if (row != other.row) return false
        if (column != other.column) return false
        return elements.contentEquals(other.elements)
    }

    override fun hashCode(): Int {
        return row * column * elements.hashCode()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("size: [ ${row}, $column ]\n")
        sb.append("elements:\n")
        for (i in 0 until row) {
            sb.append("|")
            for (j in 0 until column) {
                sb.append(" ${get(i, j)}")
            }
            sb.append(" |\n")
        }
        return sb.toString()
    }

    companion object {
        fun identity(size: Int): Matrix =
            Matrix(List(size) { i -> List(size) { j -> if (i == j) 1.0 else 0.0 } })

        fun diagonal(elements: List<Double>): Matrix =
            Matrix(elements.mapIndexed { i, d -> List(elements.size) { j -> if (i == j) d else 0.0 } })
    }

    init {
        require(elements.isNotEmpty()) { "elements is empty" }
        require(elements.size == row * column) { "invalid element size: ${elements.size}, row: $row, column: $column" }
    }

}