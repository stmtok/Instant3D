package com.stmtok.common.gl

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2
import com.stmtok.common.geom.Point
import com.stmtok.common.geom.Rotation


abstract class SceneDrawer(
    /** 基準となる大さ（cm） */
    var distance: Double = 10.0,
    /** カメラの初期位置を設定 */
    protected val defaultCameraPosition: Point = Point(),
    /** カメラの初期角度を設定 */
    protected val defaultCameraRotation: Rotation = Rotation(),
) {

    /** カメラの位置を返します */
    abstract fun getCameraPosition(): Point

    /** カメラの方向を返します */
    abstract fun getCameraRotation(): Rotation

    /** モデル空間の位置を返します */
    abstract fun getModelPosition(): Point

    /** モデル空間の角度を返します */
    abstract fun getModelRotation(): Rotation

    /** モデル空間の拡大率を返します */
    abstract fun getModelScale(): Double

    /** 光源設定 */
    open fun lightSetting(gl: GL2) {
        // 画面外の上方から照明を当てるように設定
        val ambient = floatArrayOf(0.3f, 0.3f, 0.3f, 1f)
        val diffuse = floatArrayOf(0.3f, 0.3f, 0.3f, 1f)
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, floatArrayOf(0f, distance.toFloat(), 0f, 1f), 0)
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0)
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0)

        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, floatArrayOf(0f, -distance.toFloat(), 0f, 1f), 0)
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, ambient, 0)
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, diffuse, 0)
    }

    /** 描画スレッド実行毎に呼ばれる */
    open fun displaySync() = Unit

    open fun drawScene(gl: GL) {
        val gl2 = gl.gL2
        // 透過設定を有効化
        gl.glEnable(GL.GL_BLEND)
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA)

        // カメラの姿勢の初期値に依存する角度を設定
        gl2.glRotated(defaultCameraRotation.x, 1.0, 0.0, 0.0)
        gl2.glRotated(defaultCameraRotation.y, 0.0, 1.0, 0.0)
        gl2.glRotated(defaultCameraRotation.z, 0.0, 0.0, 1.0)
        gl2.glTranslated(defaultCameraPosition.x, defaultCameraPosition.y, defaultCameraPosition.z)
        // カメラを設定
        val camRot = getCameraRotation()
        gl2.glRotated(-camRot.x, 1.0, 0.0, 0.0)
        gl2.glRotated(-camRot.y, 0.0, 1.0, 0.0)
        gl2.glRotated(-camRot.z, 0.0, 0.0, 1.0)
        val camPos = getCameraPosition()
        gl2.glTranslated(-camPos.x, -camPos.y, -camPos.z)

        // 光源設定
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
        gl.glDisable(GL.GL_BLEND)
    }

    /** VR空間を描画する */
    abstract fun drawVRSpace(gl: GL)

    /** モデル空間を描画する */
    abstract fun drawModelSpace(gl: GL)
}