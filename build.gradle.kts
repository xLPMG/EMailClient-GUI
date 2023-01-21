plugins {
    application 
    id("org.openjfx.javafxplugin") version "0.0.13"
}

repositories {
    mavenCentral() 
}

dependencies {
    implementation("jakarta.mail:jakarta.mail-api:2.0.1")
    implementation("com.sun.mail:jakarta.mail:2.0.1")
    
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1") 
    testImplementation("org.mockito:mockito-junit-jupiter:5.0.0")
}

javafx {
    version = "19"
    modules("javafx.controls", "javafx.fxml", "javafx.web")
}

application {
    mainClass.set("fsu.grumbach_hofmann.emailclientgui.Main") 
}

tasks.named<Test>("test") {
    useJUnitPlatform() 
}