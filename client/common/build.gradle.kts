val mavenGroup: String by rootProject

group = "$mavenGroup.client-common"

dependencies {
    implementation(project(":api:common"))
    implementation(project(":api:client"))

    implementation(project(":common"))
}
