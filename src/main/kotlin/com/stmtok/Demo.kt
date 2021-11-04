package com.stmtok

import com.jogamp.opengl.util.FPSAnimator
import com.stmtok.common.gl.DimensionRenderer
import com.stmtok.controller.KeyController
import com.stmtok.model.DemoModel
import com.stmtok.view.DemoScene
import javax.swing.JFrame


fun main() {
    val renderer = DimensionRenderer(800, 800)
//    val renderer = NormalRenderer(800, 600)
//    val renderer = StereoRenderer(1200, 600, 32.0)
    val model = DemoModel()
    val keyController = KeyController(model)
    val scene = DemoScene(model)
    renderer.setSceneDrawer(scene)
    JFrame("Demo").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        contentPane.add(renderer)
        addKeyListener(keyController)
        requestFocusInWindow()
        pack()
        isVisible = true
    }
    val animator = FPSAnimator(renderer, 30)
    animator.start()
}
