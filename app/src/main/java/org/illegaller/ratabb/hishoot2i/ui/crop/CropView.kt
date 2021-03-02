package org.illegaller.ratabb.hishoot2i.ui.crop

internal sealed class CropView
internal class Fail(val cause: Throwable) : CropView()
internal class Success(val uriCrop: String) : CropView()
