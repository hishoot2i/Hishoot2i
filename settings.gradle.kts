fun includeProject(name: String, path: String) {
    val pName = ":$name"
    settings.include(pName)
    project(pName).apply {
        projectDir = file(path)
        buildFileName = "$name.gradle.kts"
    }
}
includeProject("entity", "entity")
includeProject("common", "common")
includeProject("imageloader", "imageloader")
includeProject("template", "template")
includeProject("app", "app")
