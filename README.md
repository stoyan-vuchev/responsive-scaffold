# Responsive Scaffold [![](https://jitpack.io/v/stoyan-vuchev/responsive-scaffold.svg)](https://jitpack.io/#stoyan-vuchev/responsive-scaffold)

> A modified [Scaffold](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#scaffold) layout from the [androidx.compose.material3](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary) package with a support for Side Rail content.

## Table of Contents

* [Key Features](#key-features)
* [Requirements](#requirements)
* [Usage](#usage)
* [Gradle Kotlin DSL Setup](#gradle-kotlin-dsl-setup)
* [Gradle Groovy Setup](#gradle-groovy-setup)
* [Notice](#notice)
* [License](#license)

## Key Features

- ****Side Rail Support:**** The Responsive Scaffold layout is thoughtfully designed to seamlessly incorporate Side Rail content, making it ideal for implementing components like a [NavigationRail](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#navigationrail) on larger screen devices or compact devices in landscape mode. This creates a natural and efficient user navigation experience.


- ****Adaptive Window Sizing:**** Leveraging the capabilities of the [WindowSizeClass](https://developer.android.com/reference/kotlin/androidx/compose/material3/windowsizeclass/WindowSizeClass) from the [androidx.compose.material3](https://developer.android.com/reference/kotlin/androidx/compose/material3/windowsizeclass/package-summary) package, the layout dynamically adjusts its hierarchy based on the window width size. It enables the layout to smoothly switch between displaying a Bottom Bar content for compact window width size and displaying a Side Rail content for medium or expanded window width size.


- Also, when the window width size is medium or expanded, the Floating Action Button content disappears, in that case, the FAB should be included at the upper left of the screen, as stated in the [Material Design FAB guidelines](https://m3.material.io/components/floating-action-button/guidelines#db386471-8faf-4ded-ad55-8fc63ddb6e40). For example by placing it inside the header content of a [NavigationRail](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#navigationrail) component.

## Requirements

- Project minSdk version - `23`
- Project compileSdk version - `34`
- Jetpack Compose version - `1.5.0`
- Jetpack Compose Compiler version - `1.5.1`
- Kotlin version - `1.9.0`


## Usage

* `modifier` - a [Modifier](https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier) to be applied to the scaffold.
* `topBar` - a top app bar component, typically a [TopAppBar](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#topappbar).
* `bottomBar` - a bottom bar component, typically a [NavigationBar](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#navigationbar).
* `sideRail` - a side rail component, typically a [NavigationRail](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#navigationrail).
* `snackbarHost` - a component to host [Snackbars](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#snackbar).
* `floatingActionButton` - the main action button of the screen, typically a [FloatingActionButton](https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#floatingactionbutton).
* `floatingActionButtonPosition` - position of the FAB on the screen.
* `containerColor` - a color used for the background of the scaffold.
* `contentColor` - a preferred color for the content inside the scaffold.
* `contentWindowInsets` - a window insets to be passed to `contentColor` slot via [PaddingValues](https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/PaddingValues) params.
* `content` - the main content of the screen, e.g. a [LazyColumn](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/package-summary#LazyColumn).

<br/>

```kotlin
ResponsiveScaffold(
    topBar = { TopAppBar() },
    bottomBar = { NavigationBar() },
    sideRail = { NavigationRail() },
    floatingActionButton = { FAB() },
    // ...
) { paddingValues ->
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = paddingValues,
        // ...
    ) {
        // ...
    }
    
}
```
<br/>

## Gradle Kotlin DSL Setup

#### Step 1

* Add the Jitpack maven repository in your `settings.gradle.kts` file.

```kotlin
repositories {
    maven(url = "https://jitpack.io")
}
```

#### Step 2

* Add the Responsive Scaffold dependency in your module `build.gradle.kts` file.
* Latest version: [![](https://jitpack.io/v/stoyan-vuchev/responsive-scaffold.svg)](https://jitpack.io/#stoyan-vuchev/responsive-scaffold)

```kotlin
implementation("com.github.stoyan-vuchev:responsive-scaffold:<version>")
```

* Or if you're using a `libs.versions.toml` catalog, declare it there.

```toml
[versions]
responsive-scaffold = "<version>"

[libraries]
responsive-scaffold = { group = "com.github.stoyan-vuchev", name = "responsive-scaffold", version.ref = "responsive-scaffold" }
```

* Then include the dependency in your module `build.gradle.kts` file.

```kotlin
implementation(libs.responsive.scaffold)
```

#### Step 3

* Sync and rebuild the project.

<br/>

## Gradle Groovy Setup

#### Step 1

* Add the Jitpack maven repository in your project (root) level `build.gradle` file.

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

#### Step 2

* Add the Responsive Scaffold dependency in your module `build.gradle` file.
* Latest version: [![](https://jitpack.io/v/stoyan-vuchev/responsive-scaffold.svg)](https://jitpack.io/#stoyan-vuchev/responsive-scaffold)

```groovy
implementation 'com.github.stoyan-vuchev:responsive-scaffold:<version>'
```

#### Step 3

* Sync and rebuild the project.

<br/>

## Notice

This project includes software components that are subject to the Apache License, Version 2.0.

Portions of this software were originally developed by The Android Open Source Project.

Copyright 2021 The Android Open Source Project

Modifications and enhancements to this software have been made by Stoyan Vuchev, licensed under the Apache License, Version 2.0.

For more information, please refer to the [NOTICE](./NOTICE) file.

Attributions:

- The Android Open Source Project: https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#scaffold
- Stoyan Vuchev: [contact.stoyan.vuchev@gmail.com](mailto://contact.stoyan.vuchev@gmail.com)

## License

This project is open source and available under the Apache License, Version 2.0. For more information, please refer to the [LICENSE](./LICENSE) file.
