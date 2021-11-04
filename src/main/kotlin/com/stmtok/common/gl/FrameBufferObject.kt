package com.stmtok.common.gl

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2


class FrameBufferObject {

    /** フレームバッファサイズ（幅） */
    var width: Int = 0
        private set

    /** フレームバッファサイズ（高）*/
    var height: Int = 0
        private set

    /** ピクセルバッファID */
    var textureName: Int = 0
        private set

    /** デプスバッファID */
    var renderBufferName: Int = 0
        private set

    /** フレームバッファID */
    var frameBufferName: Int = 0
        private set

    fun setup(gl: GL2, width: Int, height: Int) {
        val args = IntArray(1)
        gl.glGetIntegerv(GL.GL_MAX_TEXTURE_SIZE, args, 0)
        require(width <= args.first() && height <= args.first()) {
            "GL_MAX_TEXTURE_SIZE: ${args.first()}"
        }

        gl.glGetIntegerv(GL.GL_MAX_RENDERBUFFER_SIZE, args, 0)
        require(width <= args.first() && height <= args.first()) {
            "GL_MAX_RENDERBUFFER_SIZE: ${args.first()}"
        }

        this.width = width
        this.height = height
        // テクスチャ
        gl.glGenTextures(1, args, 0)
        textureName = args.first()
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureName)
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR)
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR)
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_BORDER)
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_BORDER)
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null)
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0)

        // レンダーバッファ
        gl.glGenRenderbuffers(1, args, 0)
        renderBufferName = args.first()
        gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, renderBufferName)
        gl.glRenderbufferStorage(GL.GL_RENDERBUFFER, GL2.GL_DEPTH_COMPONENT, width, height)
        gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, 0)

        // フレームバッファ
        gl.glGenFramebuffers(1, args, 0)
        frameBufferName = args.first()
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, frameBufferName)
        // フレームバッファオブジェクトにカラーバッファとしてテクスチャを結合
        gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D, textureName, 0)
        // フレームバッファオブジェクトにデプスバッファとしてレンダーバッファを結合
        gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, GL2.GL_RENDERBUFFER, renderBufferName)
        require(gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER) == GL.GL_FRAMEBUFFER_COMPLETE) {
            "failed to initialize framebuffer object"
        }
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0)
    }

    fun updateSize(gl: GL2, width: Int, height: Int) {
        val args = IntArray(1)
        gl.glGetIntegerv(GL.GL_MAX_TEXTURE_SIZE, args, 0)
        require(width <= args.first() && height <= args.first()) {
            "GL_MAX_TEXTURE_SIZE: ${args.first()}"
        }
        gl.glGetIntegerv(GL.GL_MAX_RENDERBUFFER_SIZE, args, 0)
        require(width <= args.first() && height <= args.first()) {
            "GL_MAX_RENDERBUFFER_SIZE: ${args.first()}"
        }
        this.width = width
        this.height = height

        gl.glBindTexture(GL.GL_TEXTURE_2D, textureName)
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null)
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0)

        gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, renderBufferName)
        gl.glRenderbufferStorage(GL.GL_RENDERBUFFER, GL2.GL_DEPTH_COMPONENT, width, height)
        gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, 0)
    }

    fun release(gl: GL2) {
        gl.glDeleteTextures(1, intArrayOf(textureName), 0)
        textureName = 0
        gl.glDeleteRenderbuffers(1, intArrayOf(renderBufferName), 0)
        renderBufferName = 0
        gl.glDeleteFramebuffers(1, intArrayOf(frameBufferName), 0)
        frameBufferName = 0
    }
}