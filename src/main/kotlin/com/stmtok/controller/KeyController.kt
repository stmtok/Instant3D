package com.stmtok.controller

import com.stmtok.model.DemoModel
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class KeyController(private val model: DemoModel) : KeyListener {

    override fun keyTyped(e: KeyEvent) = Unit
    override fun keyReleased(e: KeyEvent) {
    }

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_LEFT -> {
                if (e.isShiftDown) {
                    model.moveModelLeft()
                } else {
                    model.rotModelLeft()
                }
            }
            KeyEvent.VK_RIGHT -> {
                if (e.isShiftDown) {
                    model.moveModelRight()
                } else {
                    model.rotModelRight()
                }
            }
            KeyEvent.VK_UP -> {
                if (e.isShiftDown) {
                    // y方向平行移動
                    model.moveModelUp()
                } else {
                    model.rotModelUp()
                }
            }
            KeyEvent.VK_DOWN -> {
                if (e.isShiftDown) {
                    // -y方向平行移動
                    model.moveModelDown()
                } else {
                    model.rotModelDown()
                }
            }
            KeyEvent.VK_D -> {
                model.moveCameraRight()
            }
            KeyEvent.VK_A -> {
                model.moveCameraLeft()
            }
            KeyEvent.VK_S -> {
                model.moveCameraDown()
            }
            KeyEvent.VK_W -> {
                model.moveCameraUp()
            }
            KeyEvent.VK_Q -> {
                if (e.isShiftDown) {
                    model.modelScaleUp()
                } else {
                    model.cameraZoomIn()
                }
            }
            KeyEvent.VK_Z -> {
                if (e.isShiftDown) {
                    model.modelScaleDown()
                } else {
                    model.cameraZoomOut()
                }
            }
            KeyEvent.VK_C -> {
                model.resetCamera()
            }
            KeyEvent.VK_SPACE -> {
                model.resetModel()
            }
            KeyEvent.VK_ENTER -> {
                model.flipPut()
            }
        }
    }
}