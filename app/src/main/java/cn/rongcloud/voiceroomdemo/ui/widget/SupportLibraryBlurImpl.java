/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.rongcloud.voiceroomdemo.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.RSRuntimeException;
import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;
import androidx.renderscript.Allocation.MipmapControl;

public class SupportLibraryBlurImpl implements BlurImpl {
    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mBlurScript;
    private Allocation mBlurInput;
    private Allocation mBlurOutput;
    static Boolean DEBUG = null;

    public SupportLibraryBlurImpl() {
    }

    public boolean prepare(Context context, Bitmap buffer, float radius) {
        if (this.mRenderScript == null) {
            try {
                this.mRenderScript = RenderScript.create(context);
                this.mBlurScript = ScriptIntrinsicBlur.create(this.mRenderScript, Element.U8_4(this.mRenderScript));
            } catch (RSRuntimeException var5) {
                if (isDebug(context)) {
                    throw var5;
                }

                this.release();
                return false;
            }
        }

        this.mBlurScript.setRadius(radius);
        this.mBlurInput = Allocation.createFromBitmap(this.mRenderScript, buffer, MipmapControl.MIPMAP_NONE, 1);
        this.mBlurOutput = Allocation.createTyped(this.mRenderScript, this.mBlurInput.getType());
        return true;
    }

    public void release() {
        if (this.mBlurInput != null) {
            this.mBlurInput.destroy();
            this.mBlurInput = null;
        }

        if (this.mBlurOutput != null) {
            this.mBlurOutput.destroy();
            this.mBlurOutput = null;
        }

        if (this.mBlurScript != null) {
            this.mBlurScript.destroy();
            this.mBlurScript = null;
        }

        if (this.mRenderScript != null) {
            this.mRenderScript.destroy();
            this.mRenderScript = null;
        }

    }

    public void blur(Bitmap input, Bitmap output) {
        this.mBlurInput.copyFrom(input);
        this.mBlurScript.setInput(this.mBlurInput);
        this.mBlurScript.forEach(this.mBlurOutput);
        this.mBlurOutput.copyTo(output);
    }

    static boolean isDebug(Context ctx) {
        if (DEBUG == null && ctx != null) {
            DEBUG = (ctx.getApplicationInfo().flags & 2) != 0;
        }

        return DEBUG == Boolean.TRUE;
    }
}
