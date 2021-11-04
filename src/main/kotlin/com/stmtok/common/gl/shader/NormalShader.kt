package com.stmtok.common.gl.shader

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

open class NormalShader(
    private val vertexShaderSource: String = DEFAULT_VERTEX_SHADER_SOURCE,
    private var fragmentShaderSource: String = DEFAULT_FRAGMENT_SHADER_SOURCE
) {
    companion object {
        const val DEFAULT_VERTEX_SHADER_SOURCE = """
            #version 120
            attribute vec4 aPosition;
            attribute vec4 aTextureCoord;
            varying vec2 vTextureCoord;
            void main(void)
            {
              gl_Position = aPosition;
              vTextureCoord = aTextureCoord.xy;
            }
        """
        const val DEFAULT_FRAGMENT_SHADER_SOURCE = """
            #version 120
            varying vec2 vTextureCoord;
            uniform sampler2D sTexture;
            void main (void)
            {
                gl_FragColor = texture2D(sTexture, vTextureCoord);
            }        
        """
        val VERTICES_DATA: FloatBuffer = floatArrayOf(
            // X, Y, Z, U, V
            -1.0f, 1.0f, 0f, 0f, 1f,
            1.0f, 1.0f, 0f, 1f, 1f,
            -1.0f, -1.0f, 0f, 0f, 0f,
            1.0f, -1.0f, 0f, 1f, 0f
        ).let {
            ByteBuffer.allocateDirect(it.size * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
                .put(it).position(0) as FloatBuffer
        }
        const val FLOAT_SIZE_BYTES = 4
        const val VERTICES_DATA_POS_SIZE = 3
        const val VERTICES_DATA_UV_SIZE = 2
        const val VERTICES_DATA_STRIDE_BYTES =
            (VERTICES_DATA_POS_SIZE + VERTICES_DATA_UV_SIZE) * FLOAT_SIZE_BYTES
        const val VERTICES_DATA_POS_OFFSET = 0 * FLOAT_SIZE_BYTES
        const val VERTICES_DATA_UV_OFFSET =
            VERTICES_DATA_POS_OFFSET + (VERTICES_DATA_POS_SIZE * FLOAT_SIZE_BYTES)

        fun loadShader(gl: GL2, type: Int, source: Array<String>): Int {
            val shader = gl.glCreateShader(type)
            gl.glShaderSource(shader, source.size, source, null)
            gl.glCompileShader(shader)
            val args = IntArray(1)
            gl.glGetShaderiv(shader, GL2.GL_COMPILE_STATUS, args, 0)
            if (args.first() == GL.GL_FALSE) {
                gl.glGetShaderiv(shader, GL2.GL_INFO_LOG_LENGTH, args, 0)
                val size = args.first()
                if (size > 0) {
                    val info = ByteArray(size)
                    gl.glGetShaderInfoLog(shader, size, args, 0, info, 0)
                    System.err.println(String(info))
                }
                return 0
            }
            return shader
        }

        fun createProgram(gl: GL2, vertexShader: Int, pixelShader: Int): Int {
            val program = gl.glCreateProgram()

            gl.glAttachShader(program, vertexShader)
            gl.glAttachShader(program, pixelShader)

            gl.glLinkProgram(program)
            gl.glValidateProgram(program)

            val args = IntArray(1)
            gl.glGetProgramiv(program, GL2.GL_LINK_STATUS, args, 0)
            require(args.first() == GL.GL_TRUE) {
                gl.glGetProgramiv(program, GL2.GL_INFO_LOG_LENGTH, args, 0)
                val size = args.first()
                val buf = ByteArray(size)
                gl.glGetProgramInfoLog(program, size, args, 0, buf, 0)
                String(buf)
            }
            return program
        }

        fun createBuffer(gl: GL2, data: FloatBuffer): Int {
            val args = IntArray(1)
            gl.glGenBuffers(1, args, 0)
            val bufferName = args.first()

            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferName)
            val size = data.capacity() * FLOAT_SIZE_BYTES
            gl.glBufferData(GL.GL_ARRAY_BUFFER, size.toLong(), data, GL.GL_STATIC_DRAW)
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0)
            return bufferName
        }

    }

    protected var vertexShader: Int = 0
    protected var fragmentShader: Int = 0
    protected var program: Int = 0
    var vertexBufferName: Int = 0
        private set
    private val handleMap: MutableMap<String, Int> = mutableMapOf()

    open fun setup(gl: GL2) {
        vertexShader = loadShader(gl, GL2.GL_VERTEX_SHADER, arrayOf(vertexShaderSource))
        fragmentShader = loadShader(gl, GL2.GL_FRAGMENT_SHADER, arrayOf(fragmentShaderSource))
        program = createProgram(gl, vertexShader, fragmentShader)
        vertexBufferName = createBuffer(gl, VERTICES_DATA)
    }

    open fun draw(gl: GL2, texName: Int) {
        gl.glUseProgram(program)

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferName)
        gl.glEnableVertexAttribArray(getHandle(gl, "aPosition"))
        gl.glVertexAttribPointer(
            getHandle(gl, "aPosition"),
            VERTICES_DATA_POS_SIZE, GL.GL_FLOAT, false,
            VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_POS_OFFSET.toLong()
        )

        gl.glEnableVertexAttribArray(getHandle(gl, "aTextureCoord"))
        gl.glVertexAttribPointer(
            getHandle(gl, "aTextureCoord"),
            VERTICES_DATA_UV_SIZE, GL.GL_FLOAT, false,
            VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_UV_OFFSET.toLong()
        )

        gl.glActiveTexture(GL.GL_TEXTURE0)
        gl.glBindTexture(GL.GL_TEXTURE_2D, texName)
        gl.glUniform1i(getHandle(gl, "sTexture"), 0)

        onDraw(gl)

        gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4)
        gl.glDisableVertexAttribArray(getHandle(gl, "aPosition"))
        gl.glDisableVertexAttribArray(getHandle(gl, "aTextureCoord"))
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0)

        gl.glUseProgram(0)
    }

    open fun onDraw(gl: GL2) = Unit

    open fun release(gl: GL2) {
        gl.glDeleteProgram(program)
        program = 0
        gl.glDeleteShader(vertexShader)
        vertexShader = 0
        gl.glDeleteShader(fragmentShader)
        fragmentShader = 0
        gl.glDeleteBuffers(1, intArrayOf(vertexBufferName), 0)
        vertexShader = 0
    }


    protected fun getHandle(gl: GL2, name: String): Int {
        val value = handleMap[name]
        if (value != null) return value
        var location = gl.glGetAttribLocation(program, name)
        if (location == -1) {
            location = gl.glGetUniformLocation(program, name)
        }
        require(location != -1) {
            "Could not get attribute or uniform location for $name -> ${this.javaClass}"
        }
        handleMap[name] = location
        return location
    }
}