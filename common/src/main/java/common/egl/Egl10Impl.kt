package common.egl

import android.opengl.GLES10
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.egl.EGLSurface

internal class Egl10Impl : MaxTexture {
    companion object {
        private const val EGL_CONTEXT_CLIENT_VERSION = 0x3098
    }

    @Throws(Exception::class)
    override fun get(): Int? {
        val egL10: EGL10 = EGLContext.getEGL() as EGL10
        val eglDisplay: EGLDisplay = egL10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        val verMajorMinor = IntArray(2)
        egL10.eglInitialize(eglDisplay, verMajorMinor)
        val configAttr = intArrayOf(
            EGL10.EGL_COLOR_BUFFER_TYPE,
            EGL10.EGL_RGB_BUFFER,
            EGL10.EGL_LEVEL,
            0,
            EGL10.EGL_SURFACE_TYPE,
            EGL10.EGL_PBUFFER_BIT,
            EGL10.EGL_NONE
        )
        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfig = IntArray(1)
        egL10.eglChooseConfig(eglDisplay, configAttr, configs, 1, numConfig)
        if (numConfig[0] == 0) return null //
        val config = configs[0]
        val attributeSurface = intArrayOf(EGL10.EGL_WIDTH, 64, EGL10.EGL_HEIGHT, 64, EGL10.EGL_NONE)
        val eglSurface: EGLSurface = egL10.eglCreatePbufferSurface(
            eglDisplay,
            config,
            attributeSurface
        )
        val attributeContext = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 1, EGL10.EGL_NONE)
        val eglContext: EGLContext = egL10.eglCreateContext(
            eglDisplay,
            config,
            EGL10.EGL_NO_CONTEXT,
            attributeContext
        )
        egL10.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
        val maxSize = IntArray(1)
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0)
        egL10.eglMakeCurrent(
            eglDisplay,
            EGL10.EGL_NO_SURFACE,
            EGL10.EGL_NO_SURFACE,
            EGL10.EGL_NO_CONTEXT
        )
        egL10.eglDestroySurface(eglDisplay, eglSurface)
        egL10.eglDestroyContext(eglDisplay, eglContext)
        egL10.eglTerminate(eglDisplay)

        return maxSize[0]
    }
}
