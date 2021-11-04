package com.stmtok.common.gl

import com.jogamp.opengl.GL
import com.stmtok.common.geom.Point
import com.stmtok.common.geom.Rotation

abstract class DimensionDrawer(
    var dimension: Dimension = Dimension.FREE,
    distance: Double = 10.0,
    defaultCameraPosition: Point = Point(),
    defaultCameraRotation: Rotation = Rotation(),
) : SceneDrawer(distance, defaultCameraPosition, defaultCameraRotation) {

    enum class Dimension {
        TOP, FRONT, SIDE, FREE
    }

    override fun drawScene(gl: GL) {
        val gl2 = gl.gL2
        // 透過設定を有効化
        gl2.glEnable(GL.GL_BLEND)
        gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA)

        // カメラを設定
        when (dimension) {
            Dimension.FRONT -> Unit // デフォルト
            Dimension.SIDE -> gl2.glRotated(-90.0, 0.0, 1.0, 0.0) // 左を向く
            Dimension.TOP -> gl2.glRotated(90.0, 1.0, 0.0, 0.0) // 下を向く
            Dimension.FREE -> {
                // カメラの姿勢の初期値に依存する角度を設定
                gl2.glRotated(defaultCameraRotation.x, 1.0, 0.0, 0.0)
                gl2.glRotated(defaultCameraRotation.y, 0.0, 1.0, 0.0)
                gl2.glRotated(defaultCameraRotation.z, 0.0, 0.0, 1.0)
                gl2.glTranslated(defaultCameraPosition.x, defaultCameraPosition.y, defaultCameraPosition.z)

                val camRot = getCameraRotation()
                gl2.glRotated(-camRot.x, 1.0, 0.0, 0.0)
                gl2.glRotated(-camRot.y, 0.0, 1.0, 0.0)
                gl2.glRotated(-camRot.z, 0.0, 0.0, 1.0)
                val camPos = getCameraPosition()
                gl2.glTranslated(-camPos.x, -camPos.y, -camPos.z)
            }
        }
        lightSetting(gl2)

        // VR空間を描画
        drawVRSpace(gl)

        val modelScale = getModelScale()
        gl2.glScaled(modelScale, modelScale, modelScale)
        val modelRot = getModelRotation()
        gl2.glRotated(modelRot.x, 1.0, 0.0, 0.0)
        gl2.glRotated(modelRot.y, 0.0, 1.0, 0.0)
        gl2.glRotated(modelRot.z, 0.0, 0.0, 1.0)
        val modelPos = getModelPosition()
        gl2.glTranslated(modelPos.x, modelPos.y, modelPos.z)

        // モデル空間を描画
        drawModelSpace(gl)

        // 透過設定を無効化
        gl2.glDisable(GL.GL_BLEND)
    }

    open fun drawXYScene(gl: GL) = Unit
    open fun drawYZScene(gl: GL) = Unit
    open fun drawZXScene(gl: GL) = Unit
    open fun drawPerspectiveScene(gl: GL) = Unit
}