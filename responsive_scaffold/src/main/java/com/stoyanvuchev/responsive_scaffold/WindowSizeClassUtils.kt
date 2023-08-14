/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Modifications by Stoyan Vuchev:
 *
 * 2023-08-14: Added isCompactWidth() method as WindowSizeClass extension.
 *
 * Please note that these modifications are subject to the terms of the Apache License, Version 2.0.
 */

package com.stoyanvuchev.responsive_scaffold

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

/** Checks if the width of the window is compact. */
fun WindowSizeClass.isCompactWidth(): Boolean {
    return this.widthSizeClass == WindowWidthSizeClass.Compact
}