package com.xosmig.githw.controller

import com.xosmig.githw.GithwTestBase
import com.xosmig.githw.refs.Head
import com.xosmig.githw.utils.Sha256
import java.nio.file.Files.*
import java.nio.file.Path
import java.util.*
import org.junit.Test
import org.junit.Assert.*

class IntegrationTests : GithwTestBase() {

    data class Point(val lineN: Int, val col: Int) {
        val up get() = Point(lineN - 1, col)
        val down get() = Point(lineN + 1, col)
    }

    class Grid(test: String) {
        private val lines: List<String>
        val width: Int
        val height: Int get() = lines.size - 2

        init {
            val lines = test.lines()
            width = lines.map { it.length }.max()!!
            this.lines = ArrayList<String>()
            val emptyLine = " %${width}s ".format("")
            this.lines.add(emptyLine)
            lines.mapTo(this.lines) { " %-${width}s ".format(it) }
            this.lines.add(emptyLine)
        }

        operator fun get(lineN: Int) = lines[lineN + 1]  // first line is empty
        operator fun get(point: Point) = this[point.lineN][point.col]

        fun goFrom(point: Point): Point = when {
            this[point.up] == '|'   -> goUp(point.up)
            this[point.down] == '|' -> goDown(point.down)
            else -> point
        }

        fun goUp(point: Point): Point = if (this[point] == '|') { goUp(point.up) } else { point }
        fun goDown(point: Point): Point = if (this[point] == '|') { goDown(point.down) } else { point }
    }

    fun interpretTest(test: String) {
        val files = HashMap<Int, TreeSet<Path>>()
        val commits = HashMap<Int, TreeSet<Sha256>>()
        val grid = Grid(test)

        fun getBranchNum() = (githw.head as Head.BranchPointer).branch.name.toInt()

        fun commit() {
            githw.commit(randomUtils.nextString())
            commits[getBranchNum()]!!.add(githw.commit.sha256)
        }

        githw.newBranch(0.toString())
        files[0] = TreeSet<Path>()
        commits[0] = TreeSet<Sha256>()
        commits[0]!!.add(githw.commit.sha256)

        for (col in IntRange(0, grid.width - 1)) {
            for (lineN in IntRange(0, grid.height - 1)) {
                val c = grid[lineN][col]
                val connectedLine = grid.goFrom(Point(lineN, col)).lineN
                val curBranch = lineN.toString()
                val connectedBranch = connectedLine.toString()
                when (c) {
                    'N' -> { /*new branch*/
                        githw.switchBranch(connectedBranch)
                        githw.newBranch(curBranch)
                        files[lineN] = TreeSet<Path>(files[connectedLine])
                        commits[lineN] = TreeSet<Sha256>(commits[connectedLine])
                    }
                    '*' -> { /*commit*/
                        githw.switchBranch(curBranch)
                        val fileName = root.resolve(randomUtils.nextString())
                        createFile(fileName)
                        files[lineN]!!.add(fileName)
                        githw.addAll()
                        commit()
                    }
                    'M' -> { /*merge*/
                        githw.switchBranch(curBranch)
                        githw.merge(connectedBranch)
                        files[lineN]!!.addAll(files[connectedLine]!!)
                        commits[lineN]!!.addAll(commits[connectedLine]!!)
                        commits[lineN]!!.add(githw.commit.sha256)
                    }
                    'R' -> { /*remove random file*/
                        githw.switchBranch(curBranch)
                        val file = files[lineN]!!.first()
                        files[lineN]!!.remove(file)
                        githw.remove(file)
                        commit()
                    }
                    'X' -> { /*delete branch*/
                        githw.deleteBranch(curBranch)
                        files.remove(lineN)
                        commits.remove(lineN)
                    }
                    else -> { /*nothing to do*/ }
                }
                for ((branchNum, filesList) in files) {
                    githw.switchBranch(branchNum.toString())
                    try {
                        assertEquals(filesList.toList(), githw.walkExclude(root, onlyFiles = true).sorted())
                        assertEquals(commits[branchNum]!!.toList(), githw.getLog().map {it.sha256}.sorted())
                    } catch (e: Throwable) {
                        println("in colon $col")
                        println("on branch $branchNum")
                        println("operation: ${grid[lineN][col]}")
                        throw e
                    }
                }
            }
        }
    }

    @Test
    fun integrationTest1() {
        val script = """
            |0: o-*-*-M-*---*---o-M
            |   |     |         | |
            |2: N-*-o-o-*-o-X   | |
            |       |     |     | |
            |4:     N---*-M-*-*-M-o
        """.trimMargin()
        interpretTest(script)
    }

    @Test
    fun integrationTestWithRemove() {
        val script = """
            |0: o-*-*-M-----R---o-M
            |   |     |         | |
            |2: N---o-o-*-o-X   | |
            |       |     |     | |
            |4:     N-----M-R---M-o
        """.trimMargin()
        interpretTest(script)
    }
}
