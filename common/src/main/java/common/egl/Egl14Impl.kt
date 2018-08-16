package common.egl

import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.opengl.GLES20
import android.os.Build.VERSION_CODES.JELLY_BEAN_MR1
import android.support.annotation.RequiresApi

@RequiresApi(JELLY_BEAN_MR1)
internal class Egl14Impl : MaxTexture {
    @Throws(Exception::class)
    override fun get(): Int? {
        val eglDisplay: EGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        val verMajorMinor = IntArray(2)
        EGL14.eglInitialize(eglDisplay, verMajorMinor, 0, verMajorMinor, 1)
        val configAttr = intArrayOf(
            EGL14.EGL_COLOR_BUFFER_TYPE,
            EGL14.EGL_RGB_BUFFER,
            EGL14.EGL_LEVEL,
            0,
            EGL14.EGL_RENDERABLE_TYPE,
            EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_SURFACE_TYPE,
            EGL14.EGL_PBUFFER_BIT,
            EGL14.EGL_NONE
        )
        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfig = IntArray(1)
        EGL14.eglChooseConfig(
            eglDisplay,
            configAttr,
            0,
            configs,
            0,
            1,
            numConfig,
            0
        )
        if (numConfig[0] == 0) return null //
        val config = configs[0]
        val attributeSurface = intArrayOf(EGL14.EGL_WIDTH, 64, EGL14.EGL_HEIGHT, 64, EGL14.EGL_NONE)
        val eglSurface: EGLSurface = EGL14.eglCreatePbufferSurface(
            eglDisplay,
            config,
            attributeSurface,
            0
        )
        val attributeContext = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
        val eglContext: EGLContext = EGL14.eglCreateContext(
            eglDisplay,
            config,
            EGL14.EGL_NO_CONTEXT,
            attributeContext,
            0
        )

        EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
        val maxSize = IntArray(1)
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0)

        EGL14.eglMakeCurrent(
            eglDisplay,
            EGL14.EGL_NO_SURFACE,
            EGL14.EGL_NO_SURFACE,
            EGL14.EGL_NO_CONTEXT
        )
        EGL14.eglDestroySurface(eglDisplay, eglSurface)
        EGL14.eglDestroyContext(eglDisplay, eglContext)
        EGL14.eglTerminate(eglDisplay)

        return maxSize[0]
    }
}