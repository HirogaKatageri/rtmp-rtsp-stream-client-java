plugins {
    id("com.android.library")
    `maven-publish`
}

val buildVersionCode: Int = 204
val buildVersionName: String = "2.0.3.1"

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(30)
        versionCode = buildVersionCode
        versionName = buildVersionName
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    api(project(":encoder"))
    api(project(":rtmp"))
    api(project(":rtsp"))
}

afterEvaluate {
    publishing {
        publications {

            create<MavenPublication>("release") {

                from(components["release"])

                groupId = "dev.hirogakatageri"
                artifactId = "rtplibrary"
                version = buildVersionName

                pom {
                    name.set("RTP Library")
                    description.set("RTP Library by pedroSG94")

                    licenses {
                        license {
                            name.set("Apache License 2.0")
                            url.set("https://github.com/pedroSG94/rtmp-rtsp-stream-client-java/blob/master/LICENSE.txt")
                        }
                    }

                    developers {
                        developer {
                            id.set("pedroSG94")
                            name.set("Pedro Sánchez")
                            email.set("pedrossgg1994@gmail.com")
                        }
                    }
                }
            }

        }

        repositories {
            maven {
                name = "GithubPackages"
                url =
                    uri("https://maven.pkg.github.com/HirogaKatageri/rtmp-rtsp-stream-client-java")
                credentials {
                    username = System.getenv("GITHUB_USER")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}