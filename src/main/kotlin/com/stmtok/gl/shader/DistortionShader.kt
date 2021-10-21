package com.stmtok.gl.shader

import com.jogamp.opengl.GL2

class DistortionShader : NormalShader(
    DEFAULT_VERTEX_SHADER_SOURCE,
    DISTORTION_FRAGMENT_SHADER_SOURCE
) {
    companion object {
        const val DISTORTION_FRAGMENT_SHADER_SOURCE = """
            #version 120
            varying vec2 vTextureCoord;
            uniform sampler2D sTexture;
            uniform float uBarrel;
            vec2 Distort(vec2 pos, float b)
            {
                vec2 p = (2.0 * pos) - 1.0;
                float radius = length(p);
                p = (1.0 - b) * p / ( 1.0 - b * radius);
                return 0.5 * (p + 1.0) ;
            }
            void main (void)
            {
                vec4 color = texture2D(sTexture, Distort(vTextureCoord, uBarrel));
                gl_FragColor = color;
            }        
        """
    }

    var barrel: Float = 0.3f

    override fun onDraw(gl: GL2) {
        gl.glUniform1f(getHandle(gl, "uBarrel"), barrel)
    }
}