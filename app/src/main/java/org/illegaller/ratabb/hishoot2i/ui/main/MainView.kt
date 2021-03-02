package org.illegaller.ratabb.hishoot2i.ui.main

import core.CoreResult

internal sealed class MainView
internal class Fail(val cause: Throwable, val isFromSave: Boolean) : MainView()
internal class Loading(val isFromSave: Boolean) : MainView()
internal class Success(val result: CoreResult) : MainView()
