package imageloader.coil.fetch

import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.DrawableResult
import coil.fetch.Fetcher
import coil.size.Size
import common.graphics.drawable.CheckerBoardDrawable

class CheckerFetcher : Fetcher<Checker> {
    override suspend fun fetch(pool: BitmapPool, data: Checker, size: Size, options: Options) =
        DrawableResult(
            drawable = CheckerBoardDrawable.createWith(options.context, data.dp),
            isSampled = false,
            dataSource = DataSource.DISK
        )

    override fun key(data: Checker): String? = null
}
