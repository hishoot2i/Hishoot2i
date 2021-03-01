package core

import entity.ImageSourcePath
import template.Template

interface CoreProcess {
    suspend fun preview(template: Template, sourcePath: ImageSourcePath): Preview
    suspend fun save(template: Template, sourcePath: ImageSourcePath): Save
}
