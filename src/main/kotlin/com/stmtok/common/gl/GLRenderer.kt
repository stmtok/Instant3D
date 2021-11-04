package com.stmtok.common.gl

import com.jogamp.opengl.GLCapabilities
import com.jogamp.opengl.GLEventListener
import com.jogamp.opengl.GLProfile
import com.jogamp.opengl.awt.GLCanvas

abstract class GLRenderer(
    val fovy: Double,
    val near: Double,
    val far: Double
) : GLCanvas(GLCapabilities(GLProfile.get(GLProfile.GL2))), GLEventListener {
    abstract fun getSceneDrawer(): SceneDrawer?
    abstract fun setSceneDrawer(d: SceneDrawer?)
}