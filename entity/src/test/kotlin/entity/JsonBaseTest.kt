package entity

import kotlinx.serialization.json.Json

abstract class JsonBaseTest {
    protected val default = Json { encodeDefaults = true }
}
