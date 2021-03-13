package imageloader.coil.fetch

import android.content.Context
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.DrawableResult
import coil.fetch.Fetcher
import coil.size.Size
import common.custombitmap.CheckerBoardDrawable
import common.ext.dp2px
import kotlin.math.roundToInt

class CheckerFetcher : Fetcher<Checker> {
    override suspend fun fetch(pool: BitmapPool, data: Checker, size: Size, options: Options) =
        DrawableResult(
            drawable = checkerBoardDrawable(options.context, data),
            isSampled = false,
            dataSource = DataSource.DISK
        )

    override fun key(data: Checker): String? = null

    private val checkerBoardDrawable: (Context, Checker) -> CheckerBoardDrawable by lazy {
        { ctx, data ->
            CheckerBoardDrawable(ctx.dp2px(data.dp).roundToInt())
        }
    }
}
